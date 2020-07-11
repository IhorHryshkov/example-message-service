/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:31
 */
package com.example.ems.network.models.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode
@ToString
public class Add {

	@Size(min = 6, max = 64, message = "Username have incorrect size")
	@Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Username name have incorrect symbols")
	private String username;
}
