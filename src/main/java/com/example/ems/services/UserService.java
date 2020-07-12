/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.UsersDAO;
import com.example.ems.database.models.Users;
import com.example.ems.network.models.status.Add;
import com.example.ems.network.models.user.All;
import com.example.ems.services.iface.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

	@Cacheable(value = "userCache", key = "#root.methodName + \"::\" + #params.toHashKey()", unless = "#result.size() == 0")
	public List<Users> all(All params) {
		return this.usersDAO.findAll();
	}
}
