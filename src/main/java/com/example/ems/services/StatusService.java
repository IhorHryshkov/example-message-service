/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.pg.StatusDAO;
import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.network.controller.status.AllIn;
import com.example.ems.dto.network.controller.status.AllOut;
import com.example.ems.dto.network.controller.status.UpdateIn;
import com.example.ems.services.iface.MainService;
import com.example.ems.utils.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.example.ems.database.dao.pg.specification.StatusSpecification.findByCriteria;

@Slf4j
@Service
public class StatusService implements MainService<Status, Integer, AllIn, AllOut<Status>, Status, Integer, Status, Status> {

	private final UsersDAO usersDAO;
	private final StatusDAO statusDAO;
	private final StateDAO stateDAO;
	private final QueueService queueService;

	public StatusService(
			UsersDAO usersDAO,
			StateDAO stateDAO,
			StatusDAO statusDAO,
			QueueService queueService
	) {
		this.usersDAO = usersDAO;
		this.statusDAO = statusDAO;
		this.stateDAO = stateDAO;
		this.queueService = queueService;
	}

	@CacheEvict(value = "statusCache", allEntries = true)
	public States updateCounterAndStatus(UpdateIn data) {
		if (this.stateDAO.add(String.format("statusState::updateCounterAndStatus::%s", States.IN_PROGRESS), data.toHashKey(), data) != null) {
			log.info("Status ID {} by User ID {} is in progress", data.getStatusId(), data.getUserId());
			return States.IN_PROGRESS;
		}
//		Users user = this.usersDAO.findById(UUID.fromString(data.getUserId())).orElseThrow(() -> {
//			log.error("User ID {} not found", data.getUserId());
//			this.stateDAO.del(String.format("statusState::updateCounterAndStatus::%s", States.IN_PROGRESS), data.toHashKey());
//			throw new ResponseEmptyException();
//		});
		this.queueService.sendMessage(String.format("status.%s", data.getUserId()), data, this.queueService.getRabbitMQSettings().getStatus());
		return States.RESOLVE;
	}

	@Override
	public Integer add(Status data) {
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Status update(Status data, Integer integer) {
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Status getById(Integer integer) {
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	@Cacheable(value = "statusCache", key = "#root.getMethodName() + \"::ifNoneMatch::\" + #params.toHashKey()", unless = "#result == null || #result.getData() == null || #result.getData().size() == 0")
	public AllOut<Status> all(AllIn params) {
		List<Status> statuses = this.statusDAO.findAll(findByCriteria(params));
		String etag = DigestUtils.sha256Hex(String.format("%s:%s:%d", UUID.randomUUID().toString(), params.getPath(), Instant.now().toEpochMilli()));

		return new AllOut<>(etag, statuses);
	}

	//id - still the same as a exchange name
	@RabbitListener(id = "status")
	public void listen(Message message, UpdateIn data) {
		MDC.put("resId", data.getResId());
		log.debug("Receive data: {}", data);
	}
}
