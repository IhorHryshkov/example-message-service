/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-15T01:41
 */
package com.example.ems.dto.mq;

import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.network.controller.counter.AddIn;
import java.io.Serializable;
import lombok.*;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CounterMQ implements Serializable {
  private Users user;
  private AddIn add;
}
