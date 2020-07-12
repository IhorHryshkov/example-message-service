/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-11T00:45
 */
package com.example.ems.network.controllers.wrapper;

import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class EMSServletRequestWrapper extends ContentCachingRequestWrapper {

	private UUID requestId;
	private String fullPathQuery;

	public EMSServletRequestWrapper(HttpServletRequest request) {
		super(request);
		requestId = UUID.randomUUID();
		fullPathQuery = request.getQueryString() != null ? request.getRequestURI().concat("?").concat(request.getQueryString()) : request.getRequestURI();
		MDC.put("RID", requestId.toString());
	}

	public UUID getRequestId() {
		return requestId;
	}

	public String getFullPathQuery() {
		return fullPathQuery;
	}
}
