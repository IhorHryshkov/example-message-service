/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-10T08:45
 */
package com.example.ems.dto.network.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
    name = "Callback",
    title = "Callback",
    description = "A consistent response object.")
public class Callback {

  @Schema(
      name = "resId",
      description = "The response ID value",
      type = "string",
      format = "uuid",
      required = true,
      example = "b480586b-5053-4ab9-a5b6-e7e75fcc5fed")
  @NotNull(message = "Response ID cannot be null")
  @Pattern(
      regexp =
          "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$",
      message = "Response ID is not UUID")
  private String resId;
}
