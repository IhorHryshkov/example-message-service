/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T02:30
 */
package com.example.ems.dto.network.controller.counter;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

@Data
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class GetByIdIn {
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
    return this.userId != null ? UUID.fromString(userId) : null;
  }

  public String toHashKey() {
    return DigestUtils.sha256Hex(toStringKey());
  }

  public String toStringKey() {
    return String.format("%s", userId);
  }
}
