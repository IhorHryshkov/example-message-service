/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-06T21:56
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
public class Status implements Serializable {
  @Id
  @SequenceGenerator(name = "status_seq_gen", sequenceName = "status_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "status_seq_gen")
  private Integer id;

  @Column(nullable = false)
  private String name;

  @Column(name = "created_at", insertable = false)
  @Convert(converter = LongTimestampConverter.class)
  private Long createdAt;

  @Column(name = "updated_at", insertable = false)
  @Convert(converter = LongTimestampConverter.class)
  private Long updatedAt;

  public Status(String name) {
    this.name = name;
  }
}
