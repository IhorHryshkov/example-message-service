/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:14
 */
package com.example.ems.network.controllers;

import com.example.ems.database.models.Counters;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.network.controllers.exceptions.global.ResponseIfNoneMatchException;
import com.example.ems.network.models.Res;
import com.example.ems.network.models.counter.GetByIdIn;
import com.example.ems.network.models.counter.GetByIdOut;
import com.example.ems.services.CacheService;
import com.example.ems.services.CounterService;
import com.example.ems.utils.network.Response;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "${parameters.controllers.counter.rootPath}")
public class CounterController {

	private final CounterService counterService;
	private final CacheService cacheService;
	private final Response<Object> response;

	CounterController(CounterService counterService, CacheService cacheService, Response<Object> response) {
		this.counterService = counterService;
		this.response = response;
		this.cacheService = cacheService;
	}

	@GetMapping("${parameters.controllers.counter.getById}")
	@ResponseStatus(HttpStatus.OK)
	ResponseEntity<Res<Object>> getById(@Valid GetByIdIn params) {
		params.setResId(MDC.get("resId"));
		params.setPath(MDC.get("fullPathQuery"));

		if (this.cacheService.exist(String.format("counterCache::getById::forMatch::%s", MDC.get("ifNoneMatch")))) {
			throw new ResponseIfNoneMatchException();
		}
		GetByIdOut<Counters> counters = counterService.getByUserId(params);
		if (counters.getData() == null || counters.getData().isEmpty()) {
			throw new ResponseEmptyException();
		}
		this.cacheService.set(String.format("counterCache::getById::forMatch::%s", counters.getEtag()), "");
		return response.formattedSuccess(counters.getData(), MediaType.APPLICATION_JSON, HttpStatus.OK.value(), counters.getEtag());
	}
}
