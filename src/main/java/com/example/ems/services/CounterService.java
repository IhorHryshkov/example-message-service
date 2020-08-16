/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.pg.CountersDAO;
import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Counters;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.mq.CallbackMQ;
import com.example.ems.dto.network.controller.counter.AddIn;
import com.example.ems.dto.network.controller.counter.GetByIdIn;
import com.example.ems.dto.network.controller.counter.GetByIdOut;
import com.example.ems.network.controllers.exceptions.status.UserIDNotFoundException;
import com.example.ems.utils.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class CounterService {

	private final CountersDAO  countersDAO;
	private final StateDAO     stateDAO;
	private final UserService  userService;
	private final QueueService queueService;

	public CounterService(
			CountersDAO countersDAO,
			StateDAO stateDAO,
			UserService userService,
			QueueService queueService
	) {
		this.countersDAO  = countersDAO;
		this.stateDAO     = stateDAO;
		this.userService  = userService;
		this.queueService = queueService;
	}

	@Cacheable(value = "counterCache",
	           key = "#root.getMethodName() + \"::ifNoneMatch::\" + #params.toHashKey()",
	           unless = "#result == null || #result.getData() == null")
	public GetByIdOut<Counters> getByUserId(GetByIdIn params) {
		Counters counter = countersDAO.findByKeysUserId(params.getUserId()).stream().findFirst().orElse(null);
		String etag = DigestUtils.sha256Hex(String.format(
				"%s:%s:%d",
				UUID.randomUUID().toString(),
				params.getPath(),
				Instant.now().toEpochMilli()
		));

		return new GetByIdOut<>(etag, counter);
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
		Users user = this.userService.getUserOrNotFound(
				data.getUserId(),
				"counterState::add::%s",
				data.toHashKey(),
				data.toHashUserId()
		);
		this.queueService.sendMessage(
				String.format("counter.%s", user.getUsername()),
				new CallbackMQ<>(user.getUsername(), data.getResId(), data),
				this.queueService.getRabbitMQSettings().getCounterAdd()
		);
		return States.RESOLVE;
	}

}
