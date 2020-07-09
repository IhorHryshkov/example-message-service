/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T02:30
 */
package com.example.ems.network.models.counter;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.UUID;

@Data
public class GetById {
	@Pattern(regexp = "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$", message = "User ID is not UUID")
	private String userId;

	public UUID getUserId() {
		return UUID.fromString(this.userId);
	}

	public String toJSON() {
		return "{userId:\"" + this.userId + "\"}";
	}
}
