package com.example.ems.services.models.cache;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class AddIn {
    private String requestId;
    private Object data;
    private String path;
    private String hashName;
}
