package com.example.ems.utils.network;

import com.example.ems.network.controllers.wrapper.EMSServletRequestWrapper;
import com.example.ems.network.models.Res;
import com.example.ems.network.models.ResError;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@Component
@NoArgsConstructor
@Slf4j
public class Response<A> {

	public ResponseEntity<Res<A>> formattedSuccess(A data, MediaType type, Integer status, String requestId) {
		Res<A> result = new Res<A>(requestId, data, null, Instant.now().toEpochMilli());
		log.trace("requestId: {}, response: {}", requestId, result);
		return ResponseEntity.status(status).contentType(type).body(result);
	}

	public ResponseEntity<Res<A>> formattedError(HttpServletRequest req, String message, MediaType type, Integer status) {
		String requestId = ((EMSServletRequestWrapper) req).getRequestId().toString();
		int newStatus = status != null && status > 0 ? status : 500;
		String requestURI = ((EMSServletRequestWrapper) req).getFullPathQuery();
		Res<A> result = new Res<A>(requestId, null, new ResError(newStatus, message, req.getMethod(), requestURI), Instant.now().toEpochMilli());
		log.warn("requestId: {}, response: {}", requestId, result);
		return ResponseEntity.status(newStatus).contentType(type).body(result);
	}
}
