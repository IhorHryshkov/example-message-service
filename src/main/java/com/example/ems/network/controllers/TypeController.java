/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:14
 */
package com.example.ems.network.controllers;

import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.type.AllIn;
import com.example.ems.dto.network.controller.type.AllOut;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.services.CacheService;
import com.example.ems.services.TypeService;
import com.example.ems.utils.network.Response;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(path = "${parameters.controllers.type.rootPath}")
public class TypeController {

  private final TypeService typeService;
  private final Response<Object> response;
  private final CacheService cacheService;

  TypeController(TypeService typeService, CacheService cacheService, Response<Object> response) {
    this.typeService = typeService;
    this.response = response;
    this.cacheService = cacheService;
  }

  @GetMapping
  ResponseEntity<Res<Object>> all(AllIn params) {
    params.setResId(MDC.get("resId"));
    params.setPath(MDC.get("fullPathQuery"));
    this.cacheService.existOrIfNoneMatch(
        String.format("typeCache::all::forMatch::%s", MDC.get("ifNoneMatch")));
    AllOut<Types> types = this.typeService.all(params);
    if (types.getData() == null || types.getData().isEmpty()) {
      throw new ResponseEmptyException();
    }
    this.cacheService.setKeyForCheckWithTtlDivider(
        String.format("typeCache::all::forMatch::%s", types.getEtag()), 2);
    return response.formattedSuccess(
        types.getData(), MediaType.APPLICATION_JSON, HttpStatus.OK.value(), types.getEtag());
  }
}
