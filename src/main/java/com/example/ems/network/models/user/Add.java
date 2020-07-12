/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:31
 */
package com.example.ems.network.models.user;

import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Add {
	@Size(min = 6, max = 64, message = "Username have incorrect size")
	@Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Username name have incorrect symbols")
	private String username;

	@Pattern(regexp = "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$", message = "Request ID is not UUID")
	private String requestId;
}
