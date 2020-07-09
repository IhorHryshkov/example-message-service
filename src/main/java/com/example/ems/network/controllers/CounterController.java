/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:14
 */
package com.example.ems.network.controllers;

import com.example.ems.database.models.Counters;
import com.example.ems.network.models.counter.GetById;
import com.example.ems.services.CounterService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/v1")
public class CounterController {

	private final CounterService counterService;

	CounterController(CounterService counterService) {
		this.counterService = counterService;
	}

	@GetMapping("/counter/{userId}")
	@ResponseStatus(HttpStatus.OK)
	List<Counters> getById(@Valid GetById pathParams) {
		return counterService.getByUserId(pathParams.getUserId());
	}
}
