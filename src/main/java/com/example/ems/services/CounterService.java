/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import com.example.ems.database.dao.CountersDAO;
import com.example.ems.database.models.Counters;
import com.example.ems.network.models.counter.GetByIdIn;
import com.example.ems.network.models.counter.GetByIdOut;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CounterService {

	private final CountersDAO countersDAO;

	public CounterService(CountersDAO countersDAO) {
		this.countersDAO = countersDAO;
	}

	@Cacheable(value = "counterCache", key = "#root.getMethodName() + \"::ifNoneMatch::\" + #params.toHashKey()", unless = "#result == null || #result.getData() == null || #result.getData().size() == 0")
	public GetByIdOut<Counters> getByUserId(GetByIdIn params) {
		List<Counters> counters = countersDAO.findByUserId(params.getUserId());
		String etag = DigestUtils.sha256Hex(String.format("%s:%s:%d", UUID.randomUUID().toString(), params.getPath(), Instant.now().toEpochMilli()));

		return new GetByIdOut<>(etag, counters);
	}

}
