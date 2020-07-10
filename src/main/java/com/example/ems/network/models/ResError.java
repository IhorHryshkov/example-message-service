package com.example.ems.network.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ResError {
    private Integer code;
    private String message;
    private String method;
    private String endpoint;
}
