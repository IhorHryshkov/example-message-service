/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T02:30
 */
package com.example.ems.network.models.user;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
public class All {
	@Size(min = 6, max = 64, message = "Username have incorrect size")
	@Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Username name have incorrect symbols")
	private String username;

	@Pattern(regexp = "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$", message = "User ID is not UUID")
	private String id;

	public UUID getId() {
		return UUID.fromString(this.id);
	}

	public String toJSON() {
		return "{name:\"" + this.username + "\",id:" + this.id + "}";
	}
}
