/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T06:24
 */
package com.example.ems.dto.database.pg;

import com.example.ems.dto.database.pg.ids.CountersIds;
import com.example.ems.utils.converters.pg.LongTimestampConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Counters implements Serializable {
//	@JsonIgnore
	@EmbeddedId
	private CountersIds keys;

//	@MapsId("userId")
//	@ManyToOne
//	@JoinColumn(name = "user_id", referencedColumnName = "id")
//	private Users user;
//	@MapsId("typeId")
//	@ManyToOne
//	@JoinColumn(name = "type_id", referencedColumnName = "id")
//	private Types type;
	@Column(name = "counts", precision = 18, scale = 0, nullable = false)
	private BigInteger counts;
	@Column(name = "created_at", insertable = false)
	@Convert(converter = LongTimestampConverter.class)
	private Long createdAt;
	@Column(name = "updated_at", insertable = false)
	@Convert(converter = LongTimestampConverter.class)
	private Long updatedAt;

	public Counters(CountersIds keys, BigInteger counts) {
		this.keys = keys;
		this.counts = counts;
	}
}
