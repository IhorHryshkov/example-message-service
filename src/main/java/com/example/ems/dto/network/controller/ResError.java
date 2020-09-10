package com.example.ems.dto.network.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Schema(
    type = "object",
    name = "ResError",
    title = "ResError",
    description = "A consistent response object for sending errors.")
public class ResError {
  @Schema(
      name = "code",
      description = "The response code value",
      type = "integer",
      format = "int64")
  private Integer code;

  @Schema(name = "message", description = "The response message", type = "string")
  private String message;

  @Schema(
      name = "method",
      description = "The response HTTP request method",
      type = "string",
      allowableValues = {
        "GET", "HEAD", "POST", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH"
      })
  private String method;

  @Schema(
      name = "message",
      description = "The response path endpoint",
      type = "string",
      format = "uri")
  private String endpoint;
}
