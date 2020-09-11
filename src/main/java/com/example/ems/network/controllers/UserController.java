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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@Tag(name = "User Controller", description = "Controller for processing user data")
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
  @Operation(
      tags = {"User Controller"},
      summary = "Get users.",
      description = "Load all users or load users by query fields \"username\" and \"userId\".")
  @Parameters(
      value = {
        @Parameter(
            name = "username",
            description = "User username",
            example = "TesterPester123",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    pattern = "^[A-Za-z0-9_-]+$",
                    minLength = 3,
                    maxLength = 64)),
        @Parameter(
            name = "userId",
            description = "User ID",
            example = "f02e8ce7-162e-4f78-9508-99d8886a9e61",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    format = "uuid",
                    pattern =
                        "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$")),
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
                                  + "  \"resId\":\"5f884c9c-19a9-4b5d-8110-0ad02f296ebf\",\n"
                                  + "  \"data\":[\n"
                                  + "    {\n"
                                  + "      \"id\":\"d512a983-6d41-4910-9d3e-7ddc1983df2f\",\n"
                                  + "      \"username\":\"testUser\",\n"
                                  + "      \"meta\":null,\n"
                                  + "      \"createdAt\":1599840451118,\n"
                                  + "      \"updatedAt\":1599840451118,\n"
                                  + "      \"status\":{\n"
                                  + "        \"id\":1448,\n"
                                  + "        \"name\":\"testName\",\n"
                                  + "        \"createdAt\":1599840451103,\n"
                                  + "        \"updatedAt\":1599840451103\n"
                                  + "      }\n"
                                  + "    }\n"
                                  + "  ],\n"
                                  + "  \"timestamp\":1599841001489\n"
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
                                  + "      \"endpoint\":\"/v1/user?username=#423)(234dsfds\"\n"
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
                                  + " \"endpoint\":\"/v1/user\"\n"
                                  + "   }\n"
                                  + "}")
                    }))
      })
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
  @Operation(
      tags = {"User Controller"},
      summary = "Update user status by User ID.",
      description = "Update status for specific user.")
  @Parameters(
      value = {
        @Parameter(
            name = "userId",
            description = "User ID",
            example = "f02e8ce7-162e-4f78-9508-99d8886a9e61",
            in = ParameterIn.PATH,
            required = true,
            schema =
                @Schema(
                    type = "string",
                    format = "uuid",
                    pattern =
                        "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$"))
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
                                  + "   \"timestamp\":1599431725364,\n"
                                  + "   \"resId\":\"78449aae-eac8-48b6-aa99-5c57bfee4d63\",\n"
                                  + "   \"data\":{\n"
                                  + "     \"userId\":\"4e5d7d13-0b24-4529-bbe1-a68e50fa8121\",\n"
                                  + "     \"statusId\":1\n"
                                  + "   }\n"
                                  + "}")
                    })),
        @ApiResponse(
            responseCode = "202",
            description = "Accepted",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Res.class),
                    examples = {
                      @ExampleObject(
                          name = "202",
                          description = "Accepted",
                          value =
                              "{\n"
                                  + "   \"timestamp\":1599431725364,\n"
                                  + "   \"resId\":\"78449aae-eac8-48b6-aa99-5c57bfee4d63\",\n"
                                  + "   \"data\":{\n"
                                  + "     \"state\":\"IN_PROGRESS\"\n"
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
                                  + "      \"method\":\"PUT\",\n"
                                  + "     "
                                  + " \"endpoint\":\"/v1/user/4e5d7d13-0b24-4529-bbe1-a68e50fa8121\"\n"
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
                                  + "      \"method\":\"PUT\",\n"
                                  + "      \"endpoint\":\"/v1/user/testPest\"\n"
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
                                  + "      \"method\":\"PUT\",\n"
                                  + "     "
                                  + " \"endpoint\":\"/v1/user/4e5d7d13-0b24-4529-bbe1-a68e50fa8121\"\n"
                                  + "   }\n"
                                  + "}")
                    }))
      })
  public ResponseEntity<Res> update(
      @PathVariable("userId")
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
  @Operation(
      tags = {"User Controller"},
      summary = "Add new user.",
      description = "Adding new user data.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Res.class),
                    examples = {
                      @ExampleObject(
                          name = "201",
                          description = "Created",
                          value =
                              "{\n"
                                  + "   \"timestamp\":1599431725364,\n"
                                  + "   \"resId\":\"78449aae-eac8-48b6-aa99-5c57bfee4d63\",\n"
                                  + "   \"data\":{\n"
                                  + "     \"userId\":\"4e5d7d13-0b24-4529-bbe1-a68e50fa8121\",\n"
                                  + "     \"statusId\":1\n"
                                  + "   }\n"
                                  + "}")
                    })),
        @ApiResponse(
            responseCode = "202",
            description = "Accepted",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Res.class),
                    examples = {
                      @ExampleObject(
                          name = "202",
                          description = "Accepted",
                          value =
                              "{\n"
                                  + "   \"timestamp\":1599431725364,\n"
                                  + "   \"resId\":\"78449aae-eac8-48b6-aa99-5c57bfee4d63\",\n"
                                  + "   \"data\":{\n"
                                  + "     \"state\":\"IN_PROGRESS\"\n"
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
                                  + "      \"endpoint\":\"/v1/user\"\n"
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
                                  + "      \"endpoint\":\"/v1/user\"\n"
                                  + "   }\n"
                                  + "}")
                    }))
      })
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
