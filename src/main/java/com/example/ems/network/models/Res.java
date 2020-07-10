/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T02:30
 */
package com.example.ems.network.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Res<A> {
    private String requestId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private A data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ResError error;
    private Long timestamp;
}
