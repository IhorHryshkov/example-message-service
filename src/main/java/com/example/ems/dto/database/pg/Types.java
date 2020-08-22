/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T06:23
 */
package com.example.ems.dto.database.pg;

import com.example.ems.utils.converters.pg.LongTimestampConverter;
import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Types implements Serializable {
  @Id
  @SequenceGenerator(name = "types_seq_gen", sequenceName = "types_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "types_seq_gen")
  private Integer id;

  @Column(nullable = false)
  private String name;

  @Column(name = "created_at", insertable = false)
  @Convert(converter = LongTimestampConverter.class)
  private Long createdAt;

  @Column(name = "updated_at", insertable = false)
  @Convert(converter = LongTimestampConverter.class)
  private Long updatedAt;

  Types(String name) {
    this.name = name;
  }
}
