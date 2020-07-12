/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T06:23
 */
package com.example.ems.database.models;

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
public class Types implements Serializable {
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

	Types(String name) {
		this.name = name;
	}
}
