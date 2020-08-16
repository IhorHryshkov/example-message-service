/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.pg.CountersDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Counters;
import com.example.ems.dto.mq.CallbackMQ;
import com.example.ems.dto.network.controller.counter.AddIn;
import com.example.ems.dto.network.controller.counter.GetByIdIn;
import com.example.ems.dto.network.controller.counter.GetByIdOut;
import com.example.ems.utils.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CounterService {

	private final CountersDAO  countersDAO;
	private final StateDAO     stateDAO;
	private final QueueService queueService;

	public CounterService(CountersDAO countersDAO, StateDAO stateDAO, QueueService queueService) {
		this.countersDAO  = countersDAO;
		this.stateDAO     = stateDAO;
		this.queueService = queueService;
	}

	@Cacheable(value = "counterCache",
	           key = "#root.getMethodName() + \"::ifNoneMatch::\" + #params.toHashKey()",
	           unless = "#result == null || #result.getData() == null || #result.getData().size() == 0")
	public GetByIdOut<Counters> getByUserId(GetByIdIn params) {
		List<Counters> counters = countersDAO.findByKeysUserId(params.getUserId());
		String etag = DigestUtils.sha256Hex(String.format(
				"%s:%s:%d",
				UUID.randomUUID().toString(),
				params.getPath(),
				Instant.now().toEpochMilli()
		));

		return new GetByIdOut<>(etag, counters);
	}

	@CacheEvict(value = "counterCache", key = "\"getByUserId::ifNoneMatch::\" + #data.toHashUserId()")
	public States add(AddIn data) {
		if (this.stateDAO.add(String.format("counterState::add::%s", States.IN_PROGRESS), data.toHashKey(), data) !=
		    null) {
			log.info("User ID {} add is in progress", data.getUserId());
			return States.IN_PROGRESS;
		}
		if (data.getCount() == null || data.getCount() <= 0) {
			data.setCount(1L);
		}
		this.queueService.sendMessage(
				String.format("counter.%s", data.getUserId()),
				new CallbackMQ<>(data.getUserId(), data.getResId(), data),
				this.queueService.getRabbitMQSettings().getUserAdd()
		);
		return States.RESOLVE;
	}

}
