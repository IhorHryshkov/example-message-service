/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:14
 */
package com.example.ems.network.controllers;

import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.State;
import com.example.ems.dto.network.controller.status.AllIn;
import com.example.ems.dto.network.controller.status.AllOut;
import com.example.ems.dto.network.controller.status.UpdateIn;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.services.CacheService;
import com.example.ems.services.StatusService;
import com.example.ems.utils.enums.States;
import com.example.ems.utils.network.Response;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "${parameters.controllers.status.rootPath}")
public class StatusController {

	private final StatusService statusService;
	private final Response<Object> response;
	private final CacheService cacheService;

	StatusController(StatusService statusService, CacheService cacheService, Response<Object> response) {
		this.statusService = statusService;
		this.response = response;
		this.cacheService = cacheService;
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	ResponseEntity<Res<Object>> all(@Valid AllIn params) {
		params.setResId(MDC.get("resId"));
		params.setPath(MDC.get("fullPathQuery"));
		this.cacheService.existOrIfNoneMatch(String.format("statusCache::all::forMatch::%s", MDC.get("ifNoneMatch")));
		AllOut<Status> statuses = this.statusService.all(params);
		if (statuses.getData() == null || statuses.getData().isEmpty()) {
			throw new ResponseEmptyException();
		}
		this.cacheService.setKeyForCheckWithTtlDivider(String.format("statusCache::all::forMatch::%s", statuses.getEtag()), 2);
		return response.formattedSuccess(statuses.getData(), MediaType.APPLICATION_JSON, HttpStatus.OK.value(), statuses.getEtag());
	}

	@PutMapping
	ResponseEntity<Res<Object>> update(@Valid @RequestBody UpdateIn params) {
		params.setResId(MDC.get("resId"));

		States state = this.statusService.updateCounterAndStatus(params);
		if (state != States.RESOLVE) {
			return response.formattedSuccess(new State(state.toString()), MediaType.APPLICATION_JSON, HttpStatus.ACCEPTED.value(), "");
		}

		return response.formattedSuccess(params, MediaType.APPLICATION_JSON, HttpStatus.OK.value(), "");
	}

}
