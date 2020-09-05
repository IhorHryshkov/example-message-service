/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.pg.CountersDAO;
import com.example.ems.database.dao.pg.TypesDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Counters;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.mq.CallbackMQ;
import com.example.ems.dto.mq.CounterMQ;
import com.example.ems.dto.network.controller.counter.AddIn;
import com.example.ems.dto.network.controller.counter.AddOut;
import com.example.ems.dto.network.controller.counter.GetByIdIn;
import com.example.ems.dto.network.controller.counter.GetByIdOut;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/** Processing service of counter {@link Counters} data */
@Slf4j
@Service
public class CounterService {

  private final CountersDAO countersDAO;
  private final StateDAO stateDAO;
  private final TypesDAO typeDAO;
  private final QueueService queueService;
  private final UserCounterComponent userCounterComponent;

  public CounterService(
      CountersDAO countersDAO,
      StateDAO stateDAO,
      TypesDAO typeDAO,
      QueueService queueService,
      UserCounterComponent userCounterComponent) {
    this.countersDAO = countersDAO;
    this.stateDAO = stateDAO;
    this.typeDAO = typeDAO;
    this.queueService = queueService;
    this.userCounterComponent = userCounterComponent;
  }

  /**
   * Get counters {@link Counters} by user ID from DB or from cache also generation ETag value for
   * response and add result to cache
   *
   * @param params Object {@link GetByIdIn} with user ID and other support data
   * @return result object with list of counters and etag value {@link GetByIdOut}
   */
  @Cacheable(
      value = "counterCache::getByUserId::ifNoneMatch",
      key = "#params.toHashKey()",
      unless = "#result == null || #result.getData() == null")
  public GetByIdOut<List<Counters>> getByUserId(GetByIdIn params) {
    List<Counters> counters = countersDAO.findByKeysUserId(params.getUserId());
    String etag =
        DigestUtils.sha256Hex(
            String.format(
                "%s:%s:%d",
                UUID.randomUUID().toString(), params.getPath(), Instant.now().toEpochMilli()));

    return new GetByIdOut<>(etag, counters);
  }

  /**
   * Add new counter to DB and clean from cache. Return state process.
   *
   * @param data Object {@link AddIn} with user ID, status ID, count and other support data
   * @return result state value {@link States}
   */
  @Caching(
      evict = {
        @CacheEvict(value = "counterCache::getById::forMatch", key = "#data.toHashUserId()"),
        @CacheEvict(value = "counterCache::getByUserId::ifNoneMatch", key = "#data.toHashUserId()")
      })
  public States add(AddIn data) {
    if (this.stateDAO.add(
            String.format("counterState::add::%s", States.IN_PROGRESS), data.toHashKey(), data)
        != null) {
      log.info("User ID {} add is in progress", data.getUserId());
      return States.IN_PROGRESS;
    }
    if (data.getCount() == null || data.getCount() <= 0) {
      data.setCount(1L);
    }
    Users user =
        userCounterComponent.getUserOrNotFound(
            data.getUserId(), "counterState::add::%s", data.toHashKey(), data.toHashUserId());
    this.queueService.sendMessage(
        String.format("counter.add.%s", user.getUsername()),
        new CallbackMQ<>(user.getUsername(), data.getResId(), new CounterMQ(user, data)),
        this.queueService.getRabbitMQSettings().getCounterAdd());
    return States.RESOLVE;
  }

  // id - still the same as a id name
  /**
   * Listener of AMQP messages, increment counter and send message to web socket. If has an error
   * then it will retry send message to web socket queue
   *
   * @param message AMQP message {@link Message} with data
   * @param in Body {@link CallbackMQ} of AMQP message after serialization and data {@link
   *     CounterMQ}
   * @throws NullPointerException If some values is null
   * @throws IllegalArgumentException If some wrong in values
   */
  @RabbitListener(id = "counterAdd")
  public void listenCounterAdd(Message message, CallbackMQ<CounterMQ> in) {
    MDC.put("resId", in.getResId());
    log.debug("Message: {}", message);
    Users user = in.getData().getUser();
    AddIn data = in.getData().getAdd();
    if (this.queueService.isGoRetry(message)) {
      log.debug("Data: {}", data);
      Types type = this.typeDAO.findById(data.getTypeId()).orElse(null);
      userCounterComponent.incCounter(
          type, user, data.getCount(), "counterState::add::%s", data.toHashKey());
      AddOut addOut = new AddOut(user.getId().toString(), in.getResId());
      this.queueService.sendMessage(
          String.format("websocket.%s", in.getQueueName()),
          new CallbackMQ<>(in.getQueueName(), in.getResId(), addOut),
          this.queueService.getRabbitMQSettings().getWebsocket());
      this.stateDAO.del(String.format("counterState::add::%s", States.INIT), data.toHashKey());
    }
    this.stateDAO.del(String.format("counterState::add::%s", States.IN_PROGRESS), data.toHashKey());
    this.queueService.removeDeclares(
        String.format("counter.add.%s", in.getQueueName()),
        this.queueService.getRabbitMQSettings().getCounterAdd().getExchange());
  }
}
