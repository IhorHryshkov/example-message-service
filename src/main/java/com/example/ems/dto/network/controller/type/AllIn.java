/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T02:30
 */
package com.example.ems.dto.network.controller.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllIn {
	@Size(min = 3, max = 64, message = "Type name have incorrect size")
	@Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Type name have incorrect symbols")
	private String name;

	@Min(value = 1, message = "Type ID have incorrect value")
	@Max(value = Integer.MAX_VALUE, message = "Type ID have incorrect value")
	private Integer id;

	@Pattern(regexp = "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$",
	         message = "Response ID is not UUID")
	private String resId;

	private String path;

	public String toHashKey() {
		return DigestUtils.sha256Hex(toStringKey());
	}

	public String toStringKey() {
		return String.format("%s::%s::%s", name, id, path);
	}
}
