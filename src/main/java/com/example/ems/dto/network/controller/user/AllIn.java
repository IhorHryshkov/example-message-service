/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T02:30
 */
package com.example.ems.dto.network.controller.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;
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
public class AllIn {
  @Size(min = 3, max = 64, message = "Username have incorrect size")
  @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Username name have incorrect symbols")
  private String username;

  @Pattern(
      regexp =
          "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$",
      message = "User ID is not UUID")
  private String userId;

  @Pattern(
      regexp =
          "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$",
      message = "Response ID is not UUID")
  private String resId;

  private String path;

  public UUID getUserId() {
    return this.userId != null ? UUID.fromString(this.userId) : null;
  }

  public String toHashKey() {
    return DigestUtils.sha256Hex(toStringKey());
  }

  public String toStringKey() {
    return String.format("%s::%s::%s", username, userId, path);
  }
}
