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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CounterService {

	private final CountersDAO          countersDAO;
	private final StateDAO             stateDAO;
	private final TypesDAO             typeDAO;
	private final QueueService         queueService;
	private final UserCounterComponent userCounterComponent;

	public CounterService(
			CountersDAO countersDAO,
			StateDAO stateDAO,
			TypesDAO typeDAO,
			QueueService queueService,
			UserCounterComponent userCounterComponent
	) {
		this.countersDAO          = countersDAO;
		this.stateDAO             = stateDAO;
		this.typeDAO              = typeDAO;
		this.queueService         = queueService;
		this.userCounterComponent = userCounterComponent;
	}

	@Cacheable(value = "counterCache",
	           key = "#root.getMethodName() + \"::ifNoneMatch::\" + #params.toHashKey()",
	           unless = "#result == null || #result.getData() == null")
	public GetByIdOut<List<Counters>> getByUserId(GetByIdIn params) {
		List<Counters> counters = countersDAO.findByKeysUserId(params.getUserId());
		String etag = DigestUtils.sha256Hex(String.format(
				"%s:%s:%d",
				UUID.randomUUID().toString(),
				params.getPath(),
				Instant.now().toEpochMilli()
		));

		return new GetByIdOut<>(etag, counters);
	}

	@Caching(evict = {
			@CacheEvict(value = "counterCache::getById::forMatch", key = "#data.toHashUserId()"),
			@CacheEvict(value = "counterCache::getByUserId::ifNoneMatch", key = "#data.toHashUserId()")
	})
	public States add(AddIn data) {
		if (this.stateDAO.add(String.format("counterState::add::%s", States.IN_PROGRESS), data.toHashKey(), data) !=
		    null) {
			log.info("User ID {} add is in progress", data.getUserId());
			return States.IN_PROGRESS;
		}
		if (data.getCount() == null || data.getCount() <= 0) {
			data.setCount(1L);
		}
		Users user = userCounterComponent.getUserOrNotFound(
				data.getUserId(),
				"counterState::add::%s",
				data.toHashKey(),
				data.toHashUserId()
		);
		this.queueService.sendMessage(
				String.format("counter.add.%s", user.getUsername()),
				new CallbackMQ<>(user.getUsername(), data.getResId(), new CounterMQ(user, data)),
				this.queueService.getRabbitMQSettings().getCounterAdd()
		);
		return States.RESOLVE;
	}

	//id - still the same as a id name
	@RabbitListener(id = "counterAdd")
	public void listenCounterAdd(Message message, CallbackMQ<CounterMQ> in) {
		MDC.put("resId", in.getResId());
		log.debug("Message: {}", message);
		Users user = in.getData().getUser();
		AddIn data = in.getData().getAdd();
		if (this.queueService.isGoRetry(message)) {
			log.debug("Data: {}", data);
			Types type = this.typeDAO.findById(data.getTypeId()).orElse(null);
			userCounterComponent.incCounter(type, user, data.getCount(), "counterState::add::%s", data.toHashKey());
			AddOut addOut = new AddOut(user.getId().toString(), in.getResId());
			this.queueService.sendMessage(
					String.format("websocket.%s", in.getQueueName()),
					new CallbackMQ<>(in.getQueueName(), in.getResId(), addOut),
					this.queueService.getRabbitMQSettings().getWebsocket()
			);
			this.stateDAO.del(String.format("counterState::add::%s", States.INIT), data.toHashKey());
		}
		this.stateDAO.del(String.format("counterState::add::%s", States.IN_PROGRESS), data.toHashKey());
		this.queueService.removeDeclares(
				String.format("counter.add.%s", in.getQueueName()),
				this.queueService.getRabbitMQSettings().getCounterAdd().getExchange()
		);
	}
}
