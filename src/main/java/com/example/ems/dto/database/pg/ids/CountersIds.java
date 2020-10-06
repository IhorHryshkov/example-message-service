/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T06:50
 */
package com.example.ems.dto.database.pg.ids;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Embeddable
public class CountersIds implements Serializable {
  @Type(type = "pg-uuid")
  @Column(columnDefinition = "uuid", name = "user_id", insertable = false, updatable = false)
  private UUID userId;

  @Column(name = "type_id", insertable = false, updatable = false)
  private Integer typeId;
}