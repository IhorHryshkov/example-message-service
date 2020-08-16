/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:31
 */
package com.example.ems.dto.network.controller.counter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.UUID;

@Data
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddIn implements Serializable {

	@Pattern(regexp = "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$",
	         message = "User ID is not UUID")
	private String  userId;
	@Min(value = 1, message = "Status ID cannot be negative or 0")
	private Integer statusId;

	private Long count;

	@Pattern(regexp = "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$",
	         message = "Response ID is not UUID")
	private String resId;

	public UUID getUserId() {
		return this.userId != null ? UUID.fromString(userId) : null;
	}

	public String toHashKey() {
		return DigestUtils.sha256Hex(toStringKey());
	}

	public String toHashUserId() {
		return DigestUtils.sha256Hex(userId);
	}

	public String toStringKey() {
		return String.format("%s::%s::%d", userId, statusId, count);
	}

}
