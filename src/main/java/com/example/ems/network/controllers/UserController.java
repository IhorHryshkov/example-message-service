/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:14
 */
package com.example.ems.network.controllers;

import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.State;
import com.example.ems.dto.network.controller.user.AddIn;
import com.example.ems.dto.network.controller.user.AllIn;
import com.example.ems.dto.network.controller.user.AllOut;
import com.example.ems.dto.network.controller.user.UpdateIn;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.services.CacheService;
import com.example.ems.services.UserService;
import com.example.ems.utils.enums.States;
import com.example.ems.utils.network.Response;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "${parameters.controllers.user.rootPath}")
public class UserController {

  private final UserService userService;
  private final Response<Object> response;
  private final CacheService cacheService;

  UserController(UserService userService, CacheService cacheService, Response<Object> response) {
    this.userService = userService;
    this.response = response;
    this.cacheService = cacheService;
  }

  @GetMapping
  public ResponseEntity<Res> all(@Valid AllIn params) {
    params.setResId(MDC.get("resId"));
    params.setPath(MDC.get("fullPathQuery"));
    this.cacheService.existOrIfNoneMatch(
        String.format("userCache::all::forMatch::%s", MDC.get("ifNoneMatch")));
    AllOut<Users> users = this.userService.all(params);
    if (users.getData() == null || users.getData().isEmpty()) {
      throw new ResponseEmptyException();
    }
    this.cacheService.setKeyForCheckWithTtlDivider(
        String.format("userCache::all::forMatch::%s", users.getEtag()), 2);
    return response.formattedSuccess(
        users.getData(), MediaType.APPLICATION_JSON, HttpStatus.OK.value(), users.getEtag());
  }

  @PutMapping({"${parameters.controllers.user.update}"})
  public ResponseEntity<Res> update(
      @PathVariable("userId")
          @Valid
          @NotNull
          @Pattern(
              regexp =
                  "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$",
              message = "User ID is not UUID")
          String userId,
      @RequestBody @Valid UpdateIn body) {
    body.setResId(MDC.get("resId"));
    body.setUserId(userId);
    States state = this.userService.updateCounterAndStatus(body);
    if (state != States.RESOLVE) {
      return response.formattedSuccess(
          new State(state.toString()), MediaType.APPLICATION_JSON, HttpStatus.ACCEPTED.value(), "");
    }

    body.setResId(null);
    return response.formattedSuccess(body, MediaType.APPLICATION_JSON, HttpStatus.OK.value(), "");
  }

  @PostMapping
  public ResponseEntity<Res> add(@RequestBody @Valid AddIn params) {
    params.setResId(MDC.get("resId"));

    States state = this.userService.add(params);
    if (state != States.RESOLVE) {
      return response.formattedSuccess(
          new State(state.toString()), MediaType.APPLICATION_JSON, HttpStatus.ACCEPTED.value(), "");
    }

    params.setResId(null);
    return response.formattedSuccess(
        params, MediaType.APPLICATION_JSON, HttpStatus.CREATED.value(), "");
  }
}
