/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T02:30
 */
package com.example.ems.network.models.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class All {
	@Size(min = 6, max = 64, message = "Username have incorrect size")
	@Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Username name have incorrect symbols")
	private String username;

	@Pattern(regexp = "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$", message = "User ID is not UUID")
	private String userId;

	@Pattern(regexp = "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$", message = "Request ID is not UUID")
	private String requestId;

	private String path;

	private String ifNoneMatch;


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
