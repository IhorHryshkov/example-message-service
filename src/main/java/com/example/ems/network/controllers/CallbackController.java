/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:14
 */
package com.example.ems.network.controllers;

import com.example.ems.dto.network.controller.Callback;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.services.CallbackService;
import com.example.ems.utils.network.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Callback Controller", description = "Controller for processing callback data")
@RequestMapping(path = "${parameters.controllers.callback.rootPath}")
public class CallbackController {

  private final CallbackService callbackService;
  private final Response<Object> response;

  CallbackController(CallbackService callbackService, Response<Object> response) {
    this.callbackService = callbackService;
    this.response = response;
  }

  @PostMapping
  @Operation(
      tags = {"Callback Controller"},
      summary = "Approve callback.",
      description = "Resolve web socket message if client successful receive it")
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
                                  + "      \"resId\":\"f02e8ce7-162e-4f78-9508-99d8886a9e61\"\n"
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
                                  + "      \"endpoint\":\"/v1/callback/approve\"\n"
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
                                  + "      \"endpoint\":\"/v1/callback/approve\"\n"
                                  + "   }\n"
                                  + "}")
                    }))
      })
  public ResponseEntity<Res> add(@RequestBody @Valid Callback params) {
    log.debug("Request callback: {}", params);

    this.callbackService.removeState(params.getResId());

    return response.formattedSuccess(params, MediaType.APPLICATION_JSON, HttpStatus.OK.value(), "");
  }
}
