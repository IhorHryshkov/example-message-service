/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T02:30
 */
package com.example.ems.dto.network.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@Schema(type = "object", name = "Res", title = "Res", description = "A consistent response object.")
public class Res {
  @Schema(name = "resId", description = "The response ID value", type = "string", format = "uuid")
  private String resId;

  @Schema(
      name = "data",
      description = "The response is some model data if response is not error",
      type = "object",
      nullable = true)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object data;

  @Schema(
      name = "error",
      description = "The response is some error data if response is not some model data",
      type = "object",
      nullable = true)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ResError error;

  @Schema(
      name = "timestamp",
      description = "The timestamp of response",
      type = "integer",
      format = "int64")
  private Long timestamp;
}
