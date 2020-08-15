/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:31
 */
package com.example.ems.dto.network.controller.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;

import javax.validation.constraints.Pattern;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateOut {

	@Pattern(regexp = "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$", message = "User ID is not UUID")
	private String userId;

	@Pattern(regexp = "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$", message = "Response ID is not UUID")
	private String resId;

	public String toHashKey() {
		return DigestUtils.sha256Hex(toStringKey());
	}

	public String toStringKey() {
		return String.format("%s", userId);
	}

}
