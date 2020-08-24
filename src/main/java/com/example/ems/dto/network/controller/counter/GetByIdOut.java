/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T02:30
 */
package com.example.ems.dto.network.controller.counter;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetByIdOut<A> implements Serializable {
  private String etag;
  private A data;
}
