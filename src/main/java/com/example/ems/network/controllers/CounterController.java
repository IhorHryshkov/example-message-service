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
@Tag(name = "Counter Controller", description = "Controller for processing counters data of user")
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
  @Operation(
      tags = {"Counter Controller"},
      summary = "Get counters by User ID.",
      description = "Load all counters and details for specific user.")
  @Parameters(
      value = {
        @Parameter(
            name = "userId",
            description = "User ID",
            example = "f02e8ce7-162e-4f78-9508-99d8886a9e61",
            in = ParameterIn.PATH,
            required = true,
            schema = @Schema(type = "string", format = "uuid")),
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
                                  + "  \"resId\":\"960f9730-b0f4-49b8-adfc-8cedfea1db14\",\n"
                                  + "  \"data\":[\n"
                                  + "    {\n"
                                  + "      \"user\":{\n"
                                  + "        \"id\":\"f02e8ce7-162e-4f78-9508-99d8886a9e61\",\n"
                                  + "        \"username\":\"testUser\",\n"
                                  + "        \"meta\":null,\n"
                                  + "        \"createdAt\":1599828403580,\n"
                                  + "        \"updatedAt\":1599828403580,\n"
                                  + "        \"status\":{\n"
                                  + "          \"id\":1358,\n"
                                  + "          \"name\":\"testName\",\n"
                                  + "          \"createdAt\":1599828403564,\n"
                                  + "          \"updatedAt\":1599828403564\n"
                                  + "        }\n"
                                  + "      },\n"
                                  + "      \"type\":{\n"
                                  + "        \"id\":490,\n"
                                  + "        \"name\":\"testName\",\n"
                                  + "        \"createdAt\":1599828403572,\n"
                                  + "        \"updatedAt\":1599828403572\n"
                                  + "      },\n"
                                  + "      \"counts\":20,\n"
                                  + "      \"createdAt\":1599828403586,\n"
                                  + "      \"updatedAt\":1599828403740\n"
                                  + "    }\n"
                                  + "  ],\n"
                                  + "  \"timestamp\":1599829489630\n"
                                  + "}")
                    })),
        @ApiResponse(responseCode = "204", description = "No Content"),
        @ApiResponse(responseCode = "304", description = "Not Modified"),
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
                                  + "      \"endpoint\":\"/v1/counter/testUserId\"\n"
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
                                  + " \"endpoint\":\"/v1/counter/78449aae-eac8-48b6-aa99-5c57bfee4d63\"\n"
                                  + "   }\n"
                                  + "}")
                    }))
      })
  public ResponseEntity<Res> getById(@Valid GetByIdIn params) {
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
  @Operation(
      tags = {"Counter Controller"},
      summary = "Add counter by User ID and Type ID.",
      description = "Adding counts for specifics type and user.")
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
                                  + "   \"timestamp\":1599431725364,\n"
                                  + "   \"resId\":\"78449aae-eac8-48b6-aa99-5c57bfee4d63\",\n"
                                  + "   \"data\":{\n"
                                  + "     \"userId\":\"4e5d7d13-0b24-4529-bbe1-a68e50fa8121\",\n"
                                  + "     \"typeId\":3,\n"
                                  + "     \"count\":10\n"
                                  + "   }\n"
                                  + "}")
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "User ID not found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Res.class),
                    examples = {
                      @ExampleObject(
                          name = "400",
                          description = "User ID not found",
                          value =
                              "{\n"
                                  + "   \"timestamp\":1599431725364,\n"
                                  + "   \"resId\":\"78449aae-eac8-48b6-aa99-5c57bfee4d63\",\n"
                                  + "   \"error\":{\n"
                                  + "      \"code\":400,\n"
                                  + "      \"message\":\"User ID not found\",\n"
                                  + "      \"method\":\"POST\",\n"
                                  + "      \"endpoint\":\"/v1/counter\"\n"
                                  + "   }\n"
                                  + "}")
                    })),
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
                                  + "      \"method\":\"POST\",\n"
                                  + "      \"endpoint\":\"/v1/counter\"\n"
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
                                  + "      \"method\":\"POST\",\n"
                                  + "      \"endpoint\":\"/v1/counter\"\n"
                                  + "   }\n"
                                  + "}")
                    }))
      })
  public ResponseEntity<Res> add(@RequestBody @Valid AddIn params) {
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
