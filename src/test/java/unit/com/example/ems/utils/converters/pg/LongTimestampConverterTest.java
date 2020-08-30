/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-30T07:40
 */
package unit.com.example.ems.utils.converters.pg;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ems.utils.converters.pg.LongTimestampConverter;
import java.sql.Timestamp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LongTimestampConverterTest {

  @InjectMocks private LongTimestampConverter longTimestampConverter;

  @Test
  void convertToDatabaseColumn() {
    Long timestamp = 1598762500636L;
    Timestamp timestampExpected = new Timestamp(1598762500636L);

    assertThat(longTimestampConverter.convertToDatabaseColumn(null))
        .as("SQL timestamp is null")
        .isNull();
    assertThat(longTimestampConverter.convertToDatabaseColumn(timestamp))
        .as("SQL timestamp is expected")
        .isEqualTo(timestampExpected);
  }

  @Test
  void convertToEntityAttribute() {
    Long timestampExpected = 1598762500636L;
    Timestamp timestamp = new Timestamp(1598762500636L);

    assertThat(longTimestampConverter.convertToEntityAttribute(null))
        .as("Long timestamp is null")
        .isNull();
    assertThat(longTimestampConverter.convertToEntityAttribute(timestamp))
        .as("Long timestamp is expected")
        .isEqualTo(timestampExpected);
  }
}
