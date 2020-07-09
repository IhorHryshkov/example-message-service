/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:31
 */
package com.example.ems.network.models.status;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Data
public class Add {

	@Pattern(regexp = "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$", message = "User ID is not UUID")
	private String user_id;
	@Min(value = 1, message = "Status ID cannot be negative or 0")
	private Integer status_id;

	public String toJSON() {
		return "{user_id:\"" + this.user_id + "\", status_id:" + this.status_id + "}";
	}

}
