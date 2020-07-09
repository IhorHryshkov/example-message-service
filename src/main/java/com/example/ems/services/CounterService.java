/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.CountersDAO;
import com.example.ems.database.models.Counters;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CounterService {

	private final CountersDAO countersDAO;

	public CounterService(CountersDAO countersDAO) {
		this.countersDAO = countersDAO;
	}

	public List<Counters> getByUserId(UUID userId) {
		return countersDAO.findByUserId(userId);
	}

}
