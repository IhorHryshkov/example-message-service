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
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

@Data
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    type = "object",
    name = "UpdateIn",
    title = "UserUpdate",
    description = "A consistent body for update of user status object.")
public class UpdateIn implements Serializable {
  @Schema(
      name = "userId",
      description = "The user ID value",
      type = "string",
      format = "uuid",
      hidden = true,
      example = "b480586b-5053-4ab9-a5b6-e7e75fcc5fed")
  @Pattern(
      regexp =
          "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$",
      message = "User ID is not UUID")
  private String userId;

  @Schema(
      name = "statusId",
      description = "The status ID value",
      type = "integer",
      format = "int32",
      required = true,
      example = "1")
  @NotNull(message = "Status ID is not null")
  @Min(value = 1, message = "Status ID cannot be negative or 0")
  private Integer statusId;

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

  public UUID getUserId() {
    return this.userId != null ? UUID.fromString(userId) : null;
  }

  public String toHashUserId() {
    return DigestUtils.sha256Hex(userId);
  }

  public String toHashKey() {
    return DigestUtils.sha256Hex(toStringKey());
  }

  public String toStringKey() {
    return String.format("%s::%s", userId, statusId);
  }
}
