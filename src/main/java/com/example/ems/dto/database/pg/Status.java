/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-06T21:56
 */
package com.example.ems.dto.database.pg;

import com.example.ems.utils.converters.pg.LongTimestampConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Status implements Serializable {

	@Id
	private Integer id;
	@Column(nullable = false)
	private String name;
	@Column(name = "created_at", nullable = false)
	@Convert(converter = LongTimestampConverter.class)
	private Long createdAt;
	@Column(name = "updated_at", nullable = false)
	@Convert(converter = LongTimestampConverter.class)
	private Long updatedAt;

	Status(String name) {
		this.name = name;
	}
}
