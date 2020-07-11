/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.UsersDAO;
import com.example.ems.database.models.Users;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.network.models.user.All;
import com.example.ems.services.iface.MainService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements MainService<Users, All, UUID> {

	private final UsersDAO usersDAO;

	public UserService(UsersDAO usersDAO) {
		this.usersDAO = usersDAO;
	}

	@Override
	public UUID add(Users data) {
		return null;
	}

	@Override
	public Users update(Users data, UUID id) {
		return null;
	}

	@Override
	public Users getById(UUID id) {
		return null;
	}

	@Override
	public List<Users> all(All params) {
		return this.usersDAO.findAll();
	}
}
