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

	public EMSServletRequestWrapper(HttpServletRequest request) {
		super(request);

		UUID resId = UUID.randomUUID();
		String fullPathQuery = request.getQueryString() != null ? request.getRequestURI().concat("?").concat(request.getQueryString()) : request.getRequestURI();

		String ifNoneMatch = request.getHeader("if-none-match");
		if (ifNoneMatch != null && !ifNoneMatch.isEmpty()) {
			MDC.put("ifNoneMatch", ifNoneMatch.replaceAll("\"", ""));
		}

		MDC.put("resId", resId.toString());
		MDC.put("fullPathQuery", fullPathQuery);
	}
}
