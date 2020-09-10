/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-15T01:41
 */
package com.example.ems.dto.mq;

import java.io.Serializable;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CallbackMQ<A> implements Serializable {
  @Size(min = 6, max = 64, message = "Queue name have incorrect size")
  @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Queue name have incorrect symbols")
  private String queueName;

  @Pattern(
      regexp =
          "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$",
      message = "Response ID is not UUID")
  private String resId;

  private A data;
}
