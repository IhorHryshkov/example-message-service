/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import static com.example.ems.database.dao.pg.specification.UsersSpecification.findByCriteria;

import com.example.ems.database.dao.pg.CountersDAO;
import com.example.ems.database.dao.pg.StatusDAO;
import com.example.ems.database.dao.pg.TypesDAO;
import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.mq.CallbackMQ;
import com.example.ems.dto.mq.StatusMQ;
import com.example.ems.dto.network.controller.user.AddIn;
import com.example.ems.dto.network.controller.user.AddOut;
import com.example.ems.dto.network.controller.user.AllIn;
import com.example.ems.dto.network.controller.user.AllOut;
import com.example.ems.dto.network.controller.user.UpdateIn;
import com.example.ems.dto.network.controller.user.UpdateOut;
import com.example.ems.services.components.UserCounterComponent;
import com.example.ems.utils.enums.States;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

  @Value("${parameters.default.status}")
  private String defaultStatus;

  private final UsersDAO usersDAO;
  private final StatusDAO statusDAO;
  private final StateDAO stateDAO;
  private final TypesDAO typesDAO;
  private final QueueService queueService;
  private final UserCounterComponent userCounterComponent;

  public UserService(
      UsersDAO usersDAO,
      StateDAO stateDAO,
      StatusDAO statusDAO,
      TypesDAO typesDAO,
      CountersDAO countersDAO,
      QueueService queueService,
      UserCounterComponent userCounterComponent) {
    this.usersDAO = usersDAO;
    this.stateDAO = stateDAO;
    this.statusDAO = statusDAO;
    this.typesDAO = typesDAO;
    this.queueService = queueService;
    this.userCounterComponent = userCounterComponent;
  }

  @Caching(
      evict = {
        @CacheEvict(value = "userCache::all::forMatch", allEntries = true),
        @CacheEvict(value = "userCache::all::ifNoneMatch", allEntries = true)
      })
  public States add(AddIn data) {
    if (stateDAO.add(
            String.format("userState::add::%s", States.IN_PROGRESS), data.toHashKey(), data)
        != null) {
      log.info("Username {} add is in progress", data.getUsername());
      return States.IN_PROGRESS;
    }
    Users user =
        usersDAO.findByStatusNameIgnoreCaseAndUsername(defaultStatus, data.getUsername()).stream()
            .findFirst()
            .orElse(null);
    if (user != null) {
      log.info("User {} is online", data.getUsername());
      stateDAO.del(String.format("userState::add::%s", States.IN_PROGRESS), data.toHashKey());
      return States.IN_PROGRESS;
    }
    queueService.sendMessage(
        String.format("user.add.%s", data.getUsername()),
        new CallbackMQ<>(data.getUsername(), data.getResId(), data),
        queueService.getRabbitMQSettings().getUserAdd());
    return States.RESOLVE;
  }

  // id - still the same as a id name
  @RabbitListener(id = "userAdd")
  public void listenUserAdd(Message message, CallbackMQ<AddIn> in) {
    MDC.put("resId", in.getResId());
    log.debug("Message: {}", message);
    AddIn data = in.getData();
    if (queueService.isGoRetry(message)) {
      log.debug("Data: {}", data);
      Status status =
          statusDAO.findByNameIgnoreCase(defaultStatus).stream().findFirst().orElse(null);
      if (status == null) {
        log.error("Default status by name {} not found in table", defaultStatus);
      } else {
        Users user = usersDAO.findByUsername(data.getUsername()).stream().findFirst().orElse(null);
        if (user == null) {
          user = new Users(data.getUsername(), status);
        } else {
          user.setStatus(status);
        }
        user = usersDAO.save(user);
        AddOut addOut = new AddOut(user.getId().toString(), data.getResId());
        queueService.sendMessage(
            String.format("websocket.%s", in.getQueueName()),
            new CallbackMQ<>(in.getQueueName(), in.getResId(), addOut),
            queueService.getRabbitMQSettings().getWebsocket());
      }
    }
    stateDAO.del(String.format("userState::add::%s", States.IN_PROGRESS), data.toHashKey());
    queueService.removeDeclares(
        String.format("user.add.%s", in.getQueueName()),
        queueService.getRabbitMQSettings().getUserAdd().getExchange());
  }

  @Caching(
      evict = {
        @CacheEvict(value = "counterCache::getById::forMatch", key = "#data.toHashUserId()"),
        @CacheEvict(value = "counterCache::getByUserId::ifNoneMatch", key = "#data.toHashUserId()"),
        @CacheEvict(value = "userCache::all::forMatch", allEntries = true),
        @CacheEvict(value = "userCache::all::ifNoneMatch", allEntries = true)
      })
  public States updateCounterAndStatus(UpdateIn data) {
    log.debug("Start stateDAO.add: {}", Instant.now());
    if (stateDAO.add(
            String.format("userState::updateCounterAndStatus::%s", States.IN_PROGRESS),
            data.toHashKey(),
            data)
        != null) {
      log.info("Status ID {} by User ID {} is in progress", data.getStatusId(), data.getUserId());
      return States.IN_PROGRESS;
    }
    log.debug("End stateDAO.add: {}", Instant.now());

    log.debug("Start getUserOrNotFound: {}", Instant.now());
    Users user =
        userCounterComponent.getUserOrNotFound(
            data.getUserId(),
            "userState::updateCounterAndStatus::%s",
            data.toHashKey(),
            data.toHashUserId());
    log.debug("End getUserOrNotFound: {}", Instant.now());

    user.getStatus().setId(data.getStatusId());
    queueService.sendMessage(
        String.format("user.update.%s", user.getUsername()),
        new CallbackMQ<>(user.getUsername(), data.getResId(), new StatusMQ(user, data)),
        queueService.getRabbitMQSettings().getUserUpdate());
    return States.RESOLVE;
  }

  // id - still the same as a exchange name
  @RabbitListener(id = "userUpdate")
  public void listenUserUpdate(Message message, CallbackMQ<StatusMQ> in) {
    MDC.put("resId", in.getResId());
    log.debug("CallbackMQ: {}", in.getData());
    Users user = in.getData().getUser();
    UpdateIn updateIn = in.getData().getUpdate();
    if (queueService.isGoRetry(message)) {
      user = usersDAO.save(user);
      Types type =
          typesDAO.findByNameIgnoreCase(user.getStatus().getName()).stream()
              .findFirst()
              .orElse(null);
      userCounterComponent.incCounter(
          type, user, null, "userState::updateCounterAndStatus::%s", updateIn.toHashKey());
      UpdateOut updateOut = new UpdateOut(user.getId().toString(), in.getResId());
      queueService.sendMessage(
          String.format("websocket.%s", in.getQueueName()),
          new CallbackMQ<>(in.getQueueName(), in.getResId(), updateOut),
          queueService.getRabbitMQSettings().getWebsocket());
      stateDAO.del(
          String.format("userState::updateCounterAndStatus::%s", States.INIT),
          updateIn.toHashKey());
    }
    stateDAO.del(
        String.format("userState::updateCounterAndStatus::%s", States.IN_PROGRESS),
        updateIn.toHashKey());
    queueService.removeDeclares(
        String.format("user.update.%s", in.getQueueName()),
        queueService.getRabbitMQSettings().getUserUpdate().getExchange());
  }

  @Cacheable(
      value = "userCache::all::ifNoneMatch",
      key = "#params.toHashKey()",
      unless = "#result == null || #result.getData() == null || #result.getData().size() == 0")
  public AllOut<Users> all(AllIn params) {
    List<Users> users = usersDAO.findAll(findByCriteria(params));
    String etag =
        DigestUtils.sha256Hex(
            String.format(
                "%s:%s:%d",
                UUID.randomUUID().toString(), params.getPath(), Instant.now().toEpochMilli()));

    return new AllOut<>(etag, users);
  }
}
