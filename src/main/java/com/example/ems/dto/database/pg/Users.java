/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T06:24
 */
package com.example.ems.dto.database.pg;

import com.example.ems.utils.converters.pg.LongTimestampConverter;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Users implements Serializable {
  @Id
  @Type(type = "pg-uuid")
  @Column(columnDefinition = "uuid")
  @GeneratedValue(generator = "OldOrNewIdGenerator")
  @GenericGenerator(
      name = "OldOrNewIdGenerator",
      strategy = "com.example.ems.utils.converters.pg.OldOrNewIdGenerator")
  private UUID id;

  @Column(nullable = false)
  private String username;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private String meta;

  @Column(name = "created_at", insertable = false)
  @Convert(converter = LongTimestampConverter.class)
  private Long createdAt;

  @Column(name = "updated_at", insertable = false)
  @Convert(converter = LongTimestampConverter.class)
  private Long updatedAt;

  @ManyToOne
  @JoinColumn(foreignKey = @ForeignKey(name = "fk_state_to_user"))
  private Status status;

  public Users(String username, Status status) {
    this.username = username;
    this.status = status;
  }
}
