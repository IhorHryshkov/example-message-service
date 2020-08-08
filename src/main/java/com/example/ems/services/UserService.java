/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.database.dao.redis.UserStateDAO;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.network.controller.user.AddIn;
import com.example.ems.dto.network.controller.user.AllIn;
import com.example.ems.dto.network.controller.user.AllOut;
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

import static com.example.ems.database.dao.pg.specification.UsersSpecification.findByCriteria;

@Slf4j
@Service
public class UserService {

	private final UsersDAO usersDAO;
	private final UserStateDAO userStateDAO;
	private final QueueService queueService;

	public UserService(
			UsersDAO usersDAO,
			UserStateDAO userStateDAO,
			QueueService queueService
	) {
		this.usersDAO = usersDAO;
		this.userStateDAO = userStateDAO;
		this.queueService = queueService;
	}

	@CacheEvict(value = "userCache", allEntries = true)
	public void add(AddIn data) {
		this.queueService.sendMessage(data.getUsername(), "user", data);
	}

	@RabbitListener(id = "user")
	public void listen(Message message, AddIn data) {
		MDC.put("resId", data.getResId());
		log.debug("Data: {}", data);
		log.debug("Error count: {}", message);
		if (!this.queueService.endedRetryCount(message)) {
			if (data.getUsername().equals("tester1")) {
				throw new RuntimeException("Test error");
			}
		}

		this.queueService.removeDeclares(data.getUsername(), "user");
	}

	@Cacheable(value = "userCache", key = "#root.getMethodName() + \"::ifNoneMatch::\" + #params.toHashKey()", unless = "#result == null || #result.getData() == null || #result.getData().size() == 0")
	public AllOut<Users> all(AllIn params) {
		List<Users> users = this.usersDAO.findAll(findByCriteria(params));
		String etag = DigestUtils.sha256Hex(String.format("%s:%s:%d", UUID.randomUUID().toString(), params.getPath(), Instant.now().toEpochMilli()));

		return new AllOut<>(etag, users);
	}
}
