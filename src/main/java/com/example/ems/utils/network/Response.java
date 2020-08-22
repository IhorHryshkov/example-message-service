package com.example.ems.utils.network;

import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.ResError;
import java.time.Instant;
import javax.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Slf4j
public class Response<A> {

  public ResponseEntity<Res<A>> formattedSuccess(
      A data, MediaType type, Integer status, String etag) {
    Res<A> result = new Res<A>(MDC.get("resId"), data, null, Instant.now().toEpochMilli());
    log.trace("response: {}", result);
    return ResponseEntity.status(status).contentType(type).eTag(etag).body(result);
  }

  public ResponseEntity<Res<A>> formattedError(
      HttpServletRequest req, String message, MediaType type, Integer status) {
    int newStatus = status != null && status > 0 ? status : 500;
    String requestURI = MDC.get("fullPathQuery");
    Res<A> result =
        new Res<A>(
            MDC.get("resId"),
            null,
            new ResError(newStatus, message, req.getMethod(), requestURI),
            Instant.now().toEpochMilli());
    if (message == null) log.info("response: {}", result);
    else log.warn("response: {}", result);
    return ResponseEntity.status(newStatus).contentType(type).body(result);
  }
}
