/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:14
 */
package com.example.ems.network.controllers;

import com.example.ems.dto.database.pg.Counters;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.State;
import com.example.ems.dto.network.controller.counter.AddIn;
import com.example.ems.dto.network.controller.counter.GetByIdIn;
import com.example.ems.dto.network.controller.counter.GetByIdOut;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.services.CacheService;
import com.example.ems.services.CounterService;
import com.example.ems.utils.enums.States;
import com.example.ems.utils.network.Response;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "${parameters.controllers.counter.rootPath}")
public class CounterController {

  private final CounterService counterService;
  private final CacheService cacheService;
  private final Response<Object> response;

  CounterController(
      CounterService counterService, CacheService cacheService, Response<Object> response) {
    this.counterService = counterService;
    this.response = response;
    this.cacheService = cacheService;
  }

  @GetMapping("${parameters.controllers.counter.getById}")
  public ResponseEntity<Res<Object>> getById(@Valid GetByIdIn params) {
    params.setResId(MDC.get("resId"));
    params.setPath(MDC.get("fullPathQuery"));

    this.cacheService.hexistOrIfNoneMatch(
        String.format("counterCache::getById::forMatch::%s", params.toHashKey()),
        MDC.get("ifNoneMatch"));
    GetByIdOut<List<Counters>> counters = counterService.getByUserId(params);
    if (counters.getData() == null || counters.getData().isEmpty()) {
      throw new ResponseEmptyException();
    }
    this.cacheService.hset(
        String.format("counterCache::getById::forMatch::%s", params.toHashKey()),
        counters.getEtag(),
        "");
    return response.formattedSuccess(
        counters.getData(), MediaType.APPLICATION_JSON, HttpStatus.OK.value(), counters.getEtag());
  }

  @PostMapping
  public ResponseEntity<Res<Object>> add(@Valid @RequestBody AddIn params) {
    params.setResId(MDC.get("resId"));

    States state = this.counterService.add(params);
    if (state != States.RESOLVE) {
      return response.formattedSuccess(
          new State(state.toString()), MediaType.APPLICATION_JSON, HttpStatus.ACCEPTED.value(), "");
    }

    params.setResId(null);
    return response.formattedSuccess(
        params, MediaType.APPLICATION_JSON, HttpStatus.CREATED.value(), "");
  }
}
