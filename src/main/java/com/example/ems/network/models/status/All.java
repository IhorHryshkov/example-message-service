/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T02:30
 */
package com.example.ems.network.models.status;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class All {
	@Size(min = 3, max = 64, message = "Status name have incorrect size")
	@Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Status name have incorrect symbols")
	private String name;

	@Min(value = 1, message = "Status ID cannot be negative or 0")
	private Integer id;

	public String toJSON() {
		return "{name:\"" + this.name + "\",id:" + this.id + "}";
	}
}
