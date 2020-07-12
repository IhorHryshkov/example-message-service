/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.UsersDAO;
import com.example.ems.database.dao.specification.UsersSpecification;
import com.example.ems.database.models.Users;
import com.example.ems.network.models.user.Add;
import com.example.ems.network.models.user.AllIn;
import com.example.ems.network.models.user.AllOut;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.example.ems.database.dao.specification.UsersSpecification.findByCriteria;

@Slf4j
@Service
public class UserService {

	private final UsersDAO usersDAO;

	public UserService(UsersDAO usersDAO) {
		this.usersDAO = usersDAO;
	}

	@CacheEvict(value = "userCache", allEntries = true)
	public UUID add(Add data) {
		return null;
	}

	@Cacheable(value = "userCache", key = "#root.getMethodName() + \"::\" + #params.toHashKey()", unless = "#result == null || #result.getData() == null || #result.getData().size() == 0")
	public AllOut<Users> all(AllIn params) {
		List<Users> users = this.usersDAO.findAll(findByCriteria(params));
		String etag = DigestUtils.sha256Hex(String.format("%s:%s:%d", UUID.randomUUID().toString(), params.getPath(), Instant.now().toEpochMilli()));

		return new AllOut<>(etag, users, params.getIfNoneMatch());
	}
}
