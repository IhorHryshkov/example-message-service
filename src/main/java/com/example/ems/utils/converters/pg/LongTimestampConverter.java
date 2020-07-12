/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T03:13
 */
package com.example.ems.utils.converters.pg;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;

@Converter(autoApply = true)
public class LongTimestampConverter implements AttributeConverter<Long, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(Long attribute) {
		return attribute == null ? null : Timestamp.valueOf(Instant.ofEpochMilli(attribute).atZone(ZoneId.systemDefault()).toLocalDateTime());
	}

	@Override
	public Long convertToEntityAttribute(Timestamp dbData) {
		return dbData == null ? null : dbData.getTime();
	}
}
