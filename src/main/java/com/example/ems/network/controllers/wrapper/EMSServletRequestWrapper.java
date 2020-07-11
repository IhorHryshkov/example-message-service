/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-11T00:45
 */
package com.example.ems.network.controllers.wrapper;

import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class EMSServletRequestWrapper extends ContentCachingRequestWrapper {

	private UUID requestId;

	public EMSServletRequestWrapper(HttpServletRequest request) {
		super(request);
		requestId = UUID.randomUUID();
	}

	public UUID getRequestId() {
		return requestId;
	}
}
