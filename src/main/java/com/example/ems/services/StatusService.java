/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.pg.StatusDAO;
import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.network.controller.status.AllIn;
import com.example.ems.dto.network.controller.status.AllOut;
import com.example.ems.services.iface.MainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.example.ems.database.dao.pg.specification.StatusSpecification.findByCriteria;

@Slf4j
@Service
public class StatusService
		implements MainService<Status, Integer, AllIn, AllOut<Status>, Status, Integer, Status, Status> {

	private final StatusDAO statusDAO;

	public StatusService(StatusDAO statusDAO) {
		this.statusDAO = statusDAO;
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
	@Cacheable(value = "statusCache",
	           key = "#root.getMethodName() + \"::ifNoneMatch::\" + #params.toHashKey()",
	           unless = "#result == null || #result.getData() == null || #result.getData().size() == 0")
	public AllOut<Status> all(AllIn params) {
		List<Status> statuses = this.statusDAO.findAll(findByCriteria(params));

		String etag = DigestUtils.sha256Hex(String.format(
				"%s:%s:%d",
				UUID.randomUUID().toString(),
				params.getPath(),
				Instant.now().toEpochMilli()
		));

		return new AllOut<>(etag, statuses);
	}
}
