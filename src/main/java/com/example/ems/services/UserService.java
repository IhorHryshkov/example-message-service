/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.StatusDAO;
import com.example.ems.database.models.Status;
import com.example.ems.network.models.status.Add;
import com.example.ems.network.models.status.All;
import com.example.ems.services.iface.MainService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements MainService<Status, All, Integer> {

	private final StatusDAO statusDAO;

	public UserService(StatusDAO statusDAO) {
		this.statusDAO = statusDAO;
	}

	public Integer updateCounterAndStatus(Add data) {
		return null;
	}

	@Override
	public Integer add(Status data) {
		return null;
	}

	@Override
	public Status update(Status data, Integer integer) {
		return null;
	}

	@Override
	public Status getById(Integer integer) {
		return null;
	}

	@Override
	public List<Status> all(All params) {
		return this.statusDAO.findAll();
	}
}
