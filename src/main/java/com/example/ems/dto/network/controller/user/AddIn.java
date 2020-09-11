/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:31
 */
package com.example.ems.dto.network.controller.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    type = "object",
    name = "UserAddIn",
    title = "UserAdd",
    description = "A consistent body for add new user object.")
public class AddIn implements Serializable {
  @Schema(
      name = "username",
      description = "The username value",
      type = "string",
      required = true,
      example = "TesterPester")
  @NotNull(message = "Username is not null")
  @Size(min = 6, max = 64, message = "Username have incorrect size")
  @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Username name have incorrect symbols")
  private String username;

  @Schema(
      name = "resId",
      description = "The response ID value",
      type = "string",
      format = "uuid",
      hidden = true,
      example = "b480586b-5053-4ab9-a5b6-e7e75fcc5fed")
  @Pattern(
      regexp =
          "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$",
      message = "Response ID is not UUID")
  private String resId;

  public String toHashKey() {
    return DigestUtils.sha256Hex(toStringKey());
  }

  public String toStringKey() {
    return String.format("%s", username);
  }
}
