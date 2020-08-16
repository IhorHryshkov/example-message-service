/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.pg.CountersDAO;
import com.example.ems.database.dao.pg.StatusDAO;
import com.example.ems.database.dao.pg.TypesDAO;
import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Counters;
import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.database.pg.ids.CountersIds;
import com.example.ems.dto.mq.CallbackMQ;
import com.example.ems.dto.mq.StatusMQ;
import com.example.ems.dto.network.controller.user.*;
import com.example.ems.network.controllers.exceptions.status.UserIDNotFoundException;
import com.example.ems.utils.enums.States;
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

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.example.ems.database.dao.pg.specification.UsersSpecification.findByCriteria;

@Slf4j
@Service
public class UserService {

	@Value("${parameters.default.status}")
	private String defaultStatus;

	private final UsersDAO     usersDAO;
	private final StatusDAO    statusDAO;
	private final StateDAO     stateDAO;
	private final TypesDAO     typesDAO;
	private final CountersDAO  countersDAO;
	private final QueueService queueService;

	public UserService(
			UsersDAO usersDAO,
			StateDAO stateDAO,
			StatusDAO statusDAO,
			TypesDAO typesDAO,
			CountersDAO countersDAO,
			QueueService queueService
	) {
		this.usersDAO     = usersDAO;
		this.statusDAO    = statusDAO;
		this.stateDAO     = stateDAO;
		this.typesDAO     = typesDAO;
		this.countersDAO  = countersDAO;
		this.queueService = queueService;
	}

	@CacheEvict(value = "userCache", allEntries = true)
	public States add(AddIn data) {
		if (this.stateDAO.add(String.format("userState::add::%s", States.IN_PROGRESS), data.toHashKey(), data) !=
		    null) {
			log.info("Username {} add is in progress", data.getUsername());
			return States.IN_PROGRESS;
		}
		Users user = this.usersDAO.findByStatusNameIgnoreCaseAndUsername(defaultStatus, data.getUsername())
		                          .stream()
		                          .findFirst()
		                          .orElse(null);
		if (user != null) {
			log.info("User {} is online", data.getUsername());
			this.stateDAO.del(String.format("userState::add::%s", States.IN_PROGRESS), data.toHashKey());
			return States.IN_PROGRESS;
		}
		this.queueService.sendMessage(
				String.format("add.%s", data.getUsername()),
				new CallbackMQ<>(data.getUsername(), data.getResId(), data),
				this.queueService.getRabbitMQSettings().getUserAdd()
		);
		return States.RESOLVE;
	}

	//id - still the same as a id name
	@RabbitListener(id = "userAdd")
	public void listenUserAdd(Message message, CallbackMQ<AddIn> in) {
		MDC.put("resId", in.getResId());
		log.debug("Message: {}", message);
		AddIn data = in.getData();
		if (this.queueService.isGoRetry(message)) {
			log.debug("Data: {}", data);
			Status status = this.statusDAO.findByNameIgnoreCase(defaultStatus).stream().findFirst().orElse(null);
			if (status == null) {
				log.error("Default status by name {} not found in table", defaultStatus);
			} else {
				Users user = this.usersDAO.findByUsername(data.getUsername()).stream().findFirst().orElse(null);
				if (user == null) {
					user = new Users(data.getUsername(), status);
				} else {
					user.setStatus(status);
				}
				user = this.usersDAO.save(user);
				AddOut addOut = new AddOut(user.getId().toString(), data.getResId());
				this.queueService.sendMessage(
						String.format("websocket.%s", in.getQueueName()),
						new CallbackMQ<>(in.getQueueName(), in.getResId(), addOut),
						this.queueService.getRabbitMQSettings().getWebsocket()
				);
			}
		}
		this.stateDAO.del(String.format("userState::add::%s", States.IN_PROGRESS), data.toHashKey());
		this.queueService.removeDeclares(
				String.format("add.%s", in.getQueueName()),
				this.queueService.getRabbitMQSettings().getUserAdd().getExchange()
		);
	}

	@Caching(evict = {
			@CacheEvict(value = "counterCache::getById::forMatch", key = "#data.toHashUserId()"),
			@CacheEvict(value = "counterCache::getByUserId::ifNoneMatch", key = "#data.toHashUserId()"),
			@CacheEvict(value = "userCache", allEntries = true)
	})
	public States updateCounterAndStatus(UpdateIn data) {
		if (this.stateDAO.add(
				String.format("userState::updateCounterAndStatus::%s", States.IN_PROGRESS),
				data.toHashKey(),
				data
		) != null) {
			log.info("Status ID {} by User ID {} is in progress", data.getStatusId(), data.getUserId());
			return States.IN_PROGRESS;
		}

		Users user = getUserOrNotFound(
				data.getUserId(),
				"userState::updateCounterAndStatus::%s",
				data.toHashKey(),
				data.toHashUserId()
		);

		user.getStatus().setId(data.getStatusId());
		this.queueService.sendMessage(
				String.format("update.%s", user.getUsername()),
				new CallbackMQ<>(
						user.getUsername(),
						data.getResId(),
						new StatusMQ(user, data)
				),
				this.queueService.getRabbitMQSettings().getUserUpdate()
		);
		return States.RESOLVE;
	}

	//id - still the same as a exchange name
	@RabbitListener(id = "userUpdate")
	public void listenUserUpdate(Message message, CallbackMQ<StatusMQ> in) {
		MDC.put("resId", in.getResId());
		log.debug("CallbackMQ: {}", in.getData());
		Users    user     = in.getData().getUser();
		UpdateIn updateIn = in.getData().getUpdate();
		if (this.queueService.isGoRetry(message)) {
			user = this.usersDAO.save(user);
			Types type = this.typesDAO.findByNameIgnoreCase(user.getStatus().getName())
			                          .stream()
			                          .findFirst()
			                          .orElse(null);
			Counters counter = null;
			if (type == null) {
				log.warn("Type name {} not found", user.getStatus().getName());
			} else {
				CountersIds countersIds = new CountersIds(user.getId(), type.getId());
				counter = this.countersDAO.findById(countersIds).orElse(null);
				if (counter == null) {
					counter = new Counters(countersIds, BigInteger.valueOf(1L));
				} else {
					counter.setCounts(counter.getCounts().add(BigInteger.valueOf(1L)));
				}
				this.countersDAO.save(counter);
			}
			UpdateOut updateOut = new UpdateOut(user.getId().toString(), in.getResId());
			this.queueService.sendMessage(
					String.format("websocket.%s", in.getQueueName()),
					new CallbackMQ<>(in.getQueueName(), in.getResId(), updateOut),
					this.queueService.getRabbitMQSettings().getWebsocket()
			);
		}
		this.stateDAO.del(
				String.format("userState::updateCounterAndStatus::%s", States.IN_PROGRESS),
				updateIn.toHashKey()
		);
		this.queueService.removeDeclares(
				String.format("update.%s", in.getQueueName()),
				this.queueService.getRabbitMQSettings().getUserUpdate().getExchange()
		);
	}

	@Cacheable(value = "userCache",
	           key = "#root.getMethodName() + \"::ifNoneMatch::\" + #params.toHashKey()",
	           unless = "#result == null || #result.getData() == null || #result.getData().size() == 0")
	public AllOut<Users> all(AllIn params) {
		List<Users> users = this.usersDAO.findAll(findByCriteria(params));
		String etag = DigestUtils.sha256Hex(String.format(
				"%s:%s:%d",
				UUID.randomUUID().toString(),
				params.getPath(),
				Instant.now().toEpochMilli()
		));

		return new AllOut<>(etag, users);
	}

	@Cacheable(value = "userCache",
	           key = "#root.getMethodName() + \"::ifNoneMatch::\" + #hashUserId",
	           unless = "#result == null")
	public Users getUserOrNotFound(UUID userId, String delStateKey, String hashKeyToDel, String hashUserId) {
		Users user = this.usersDAO.findById(userId).orElse(null);
		if (user == null) {
			log.error("User ID {} not found", userId);
			this.stateDAO.del(
					String.format(delStateKey, States.IN_PROGRESS),
					hashKeyToDel
			);
			throw new UserIDNotFoundException();
		}
		return user;
	}
}
