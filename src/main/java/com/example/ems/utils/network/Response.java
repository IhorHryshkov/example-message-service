package com.example.ems.utils.network;

import com.example.ems.network.models.ResError;
import com.example.ems.network.models.Res;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@Component
@NoArgsConstructor
@Slf4j
public class Response<A> {

    public ResponseEntity<Res<A>> formattedSuccess(A data, MediaType type, HttpStatus status, String requestId) {
        Res<A> result = new Res<A>(requestId, data, null, Instant.now().toEpochMilli());
        log.trace("Response: {}, requestId: {}", result, requestId);
        return ResponseEntity.status(status).contentType(type).body(result);
    }

    public ResponseEntity<Res<A>> formattedError(HttpServletRequest req, String message, MediaType type, HttpStatus status, String requestId) {
        int newStatus = status != null && status.value() > 0 ? status.value() : 500;
        Res<A> result = new Res<A>(requestId, null, new ResError(newStatus, message, req.getMethod(), req.getPathInfo()), Instant.now().toEpochMilli());
        log.warn("Response: {}, requestId: {}", result, requestId);
        return ResponseEntity.status(newStatus).contentType(type).body(result);
    }
}
