/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:14
 **/
package com.example.ems.network.controllers;

import com.example.ems.dto.network.controller.Callback;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.services.CallbackService;
import com.example.ems.utils.network.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "${parameters.controllers.callback.rootPath}")
public class CallbackController {

	private final CallbackService callbackService;
	private final Response<Object> response;

	CallbackController(CallbackService callbackService, Response<Object> response) {
		this.callbackService = callbackService;
		this.response = response;
	}

	@PostMapping
	ResponseEntity<Res<Object>> add(@Valid @RequestBody Callback params) {
		params.setResId(MDC.get("resId"));

		this.callbackService.removeState(params.getResId());

		return response.formattedSuccess(params, MediaType.APPLICATION_JSON, HttpStatus.OK.value(), "");
	}

}
