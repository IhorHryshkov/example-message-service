/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:14
 */
package com.example.ems.network.controllers;

import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.status.AllIn;
import com.example.ems.dto.network.controller.status.AllOut;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.services.CacheService;
import com.example.ems.services.StatusService;
import com.example.ems.utils.network.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Status Controller", description = "Controller for processing status data")
@RequestMapping(path = "${parameters.controllers.status.rootPath}")
public class StatusController {

  private final StatusService statusService;
  private final Response<Object> response;
  private final CacheService cacheService;

  StatusController(
      StatusService statusService, CacheService cacheService, Response<Object> response) {
    this.statusService = statusService;
    this.response = response;
    this.cacheService = cacheService;
  }

  @GetMapping
  @Operation(
      tags = {"Status Controller"},
      summary = "Get statuses.",
      description = "Load all statuses or load statuses by query fields \"name\" and \"id\".")
  @Parameters(
      value = {
        @Parameter(
            name = "name",
            description = "Status name",
            example = "online",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    pattern = "^[A-Za-z0-9_-]+$",
                    minLength = 3,
                    maxLength = 64)),
        @Parameter(
            name = "id",
            description = "Status ID",
            example = "1",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "integer",
                    format = "int32",
                    minimum = "1",
                    maximum = "" + Integer.MAX_VALUE)),
        @Parameter(name = "params", hidden = true)
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Success",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Res.class),
                    examples = {
                      @ExampleObject(
                          name = "200",
                          description = "Success",
                          value =
                              "{\n"
                                  + "  \"resId\":\"b2141808-68de-4993-9b5b-8bd3d25ee650\",\n"
                                  + "  \"data\":[\n"
                                  + "    {\n"
                                  + "      \"id\":1403,\n"
                                  + "      \"name\":\"testName\",\n"
                                  + "      \"createdAt\":1599838469129,\n"
                                  + "      \"updatedAt\":1599838469129\n"
                                  + "    }\n"
                                  + "  ],\n"
                                  + "  \"timestamp\":1599838680920\n"
                                  + "}")
                    })),
        @ApiResponse(
            responseCode = "204",
            description = "No Content",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "304",
            description = "Not Modified",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "422",
            description = "Request body or query or path params data is incorrect",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Res.class),
                    examples = {
                      @ExampleObject(
                          name = "422",
                          description = "Request body or query or path params data is incorrect",
                          value =
                              "{\n"
                                  + "   \"timestamp\":1599431725364,\n"
                                  + "   \"resId\":\"78449aae-eac8-48b6-aa99-5c57bfee4d63\",\n"
                                  + "   \"error\":{\n"
                                  + "      \"code\":422,\n"
                                  + "      \"message\":\"Request body or query or path params data"
                                  + " is incorrect\",\n"
                                  + "      \"method\":\"GET\",\n"
                                  + "      \"endpoint\":\"/v1/status?name=#423)(234dsfds\"\n"
                                  + "   }\n"
                                  + "}")
                    })),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error, please try again later.",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Res.class),
                    examples = {
                      @ExampleObject(
                          name = "500",
                          description = "Internal server error, please try again later.",
                          value =
                              "{\n"
                                  + "   \"timestamp\":1599431725364,\n"
                                  + "   \"resId\":\"78449aae-eac8-48b6-aa99-5c57bfee4d63\",\n"
                                  + "   \"error\":{\n"
                                  + "      \"code\":500,\n"
                                  + "      \"message\":\"Internal server error, please try again"
                                  + " later.\",\n"
                                  + "      \"method\":\"GET\",\n"
                                  + "     "
                                  + " \"endpoint\":\"/v1/status\"\n"
                                  + "   }\n"
                                  + "}")
                    }))
      })
  public ResponseEntity<Res> all(@Valid AllIn params) {
    params.setResId(MDC.get("resId"));
    params.setPath(MDC.get("fullPathQuery"));
    this.cacheService.existOrIfNoneMatch(
        String.format("statusCache::all::forMatch::%s", MDC.get("ifNoneMatch")));
    AllOut<Status> statuses = this.statusService.all(params);
    if (statuses.getData() == null || statuses.getData().isEmpty()) {
      throw new ResponseEmptyException();
    }
    this.cacheService.setKeyForCheckWithTtlDivider(
        String.format("statusCache::all::forMatch::%s", statuses.getEtag()), 2);
    return response.formattedSuccess(
        statuses.getData(), MediaType.APPLICATION_JSON, HttpStatus.OK.value(), statuses.getEtag());
  }
}
