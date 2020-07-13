/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:14
 */
package com.example.ems.network.controllers;

import com.example.ems.database.models.Users;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.network.controllers.exceptions.global.ResponseIfNoneMatchException;
import com.example.ems.network.models.Res;
import com.example.ems.network.models.user.Add;
import com.example.ems.network.models.user.AllIn;
import com.example.ems.network.models.user.AllOut;
import com.example.ems.services.CacheService;
import com.example.ems.services.UserService;
import com.example.ems.utils.network.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/v1")
public class UserController {

	private final UserService userService;
	private final CacheService cacheService;
	private final Response<Object> response;

	UserController(CacheService cacheService, UserService userService, Response<Object> response) {
		this.userService = userService;
		this.response = response;
		this.cacheService = cacheService;
	}

	@GetMapping("/user")
	@ResponseStatus(HttpStatus.OK)
	ResponseEntity<Res<Object>> all(@Valid AllIn query) {
		query.setResId(MDC.get("resId"));
		query.setPath(MDC.get("fullPathQuery"));

		if (this.cacheService.exist(String.format("userCache::all::forMatch::%s", MDC.get("ifNoneMatch")))) {
			throw new ResponseIfNoneMatchException();
		}
		AllOut<Users> users = this.userService.all(query);
		if (users.getData() == null || users.getData().isEmpty()) {
			throw new ResponseEmptyException();
		}
		this.cacheService.set(String.format("userCache::%s::forMatch::%s", "all", users.getEtag()), "");
		return response.formattedSuccess(users.getData(), MediaType.APPLICATION_JSON, HttpStatus.OK.value(), users.getEtag());
	}

	@PostMapping("/user")
	ResponseEntity<Res<Users>> add(@Valid @RequestBody Add addUser) {
		return null;
	}

}
