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
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.network.controller.user.AddIn;
import com.example.ems.dto.network.controller.user.AddOut;
import com.example.ems.dto.network.controller.user.AllIn;
import com.example.ems.dto.network.controller.user.AllOut;
import com.example.ems.utils.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.example.ems.database.dao.pg.specification.UsersSpecification.findByCriteria;

@Slf4j
@Service
public class UserService {

	@Value("${parameters.default.status}")
	private String defaultStatus;

	private final UsersDAO usersDAO;
	private final StatusDAO statusDAO;
	private final StateDAO stateDAO;
	private final QueueService queueService;

	public UserService(
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

	@CacheEvict(value = "userCache", allEntries = true)
	public States add(AddIn data) {
		if (this.stateDAO.add(String.format("userState::add::%s", States.IN_PROGRESS), data.toHashKey(), data) != null) {
			log.info("Username {} add is progress", data.getUsername());
			return States.IN_PROGRESS;
		}
		List<Users> users = this.usersDAO.findByStatusNameIgnoreCaseAndUsername(defaultStatus, data.getUsername());
		if (users != null && !users.isEmpty()) {
			log.info("User {} is online", data.getUsername());
			this.stateDAO.del(String.format("userState::add::%s", States.IN_PROGRESS), data.toHashKey());
			return States.IN_PROGRESS;
		}
		this.queueService.sendMessage(String.format("add.%s", data.getUsername()), data, this.queueService.getRabbitMQSettings().getUser());
		return States.RESOLVE;
	}

	//id - still the same as a exchange name
	@RabbitListener(id = "user")
	public void listen(Message message, AddIn data) {
		MDC.put("resId", data.getResId());
		log.debug("Message: {}", message);
		if (!this.queueService.endedRetryCount(message)) {
			log.debug("Data: {}", data);
			List<Status> statuses = this.statusDAO.findByNameIgnoreCase(defaultStatus);
			if (statuses == null || statuses.isEmpty()) {
				log.error("Default status by name {} not found in table", defaultStatus);
			} else {
				List<Users> users = this.usersDAO.findByUsername(data.getUsername());
				Users user = users != null && !users.isEmpty() ? users.get(0) : null;
				if (user == null) {
					user = new Users(data.getUsername(), statuses.get(0));
				} else {
					user.setStatus(statuses.get(0));
				}
				user = this.usersDAO.save(user);
				AddOut addOut = new AddOut(user.getId().toString(), data.getUsername(), data.getResId());
				this.queueService.sendMessage(String.format("websocket.%s", data.getUsername()), addOut, this.queueService.getRabbitMQSettings().getWebsocket());
			}
		}
		this.stateDAO.del(String.format("userState::add::%s", States.IN_PROGRESS), data.toHashKey());
		this.queueService.removeDeclares(String.format("add.%s", data.getUsername()), this.queueService.getRabbitMQSettings().getUser().getExchange());
	}

	@Cacheable(value = "userCache", key = "#root.getMethodName() + \"::ifNoneMatch::\" + #params.toHashKey()", unless = "#result == null || #result.getData() == null || #result.getData().size() == 0")
	public AllOut<Users> all(AllIn params) {
		List<Users> users = this.usersDAO.findAll(findByCriteria(params));
		String etag = DigestUtils.sha256Hex(String.format("%s:%s:%d", UUID.randomUUID().toString(), params.getPath(), Instant.now().toEpochMilli()));

		return new AllOut<>(etag, users);
	}
}
