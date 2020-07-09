/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T06:24
 */
package com.example.ems.database.models;

import com.example.ems.database.models.ids.CountersIds;
import com.example.ems.utils.converters.LongTimestampConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Counters {

	@JsonIgnore
	@EmbeddedId
	private CountersIds keys;

	@MapsId("userId")
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private Users user;
	@MapsId("typeId")
	@ManyToOne
	@JoinColumn(name = "type_id", referencedColumnName = "id")
	private Types type;
	@Column(nullable = false)
	private String counts;
	@Column(name = "created_at", nullable = false)
	@Convert(converter = LongTimestampConverter.class)
	private Long createdAt;
	@Column(name = "updated_at", nullable = false)
	@Convert(converter = LongTimestampConverter.class)
	private Long updatedAt;
}
