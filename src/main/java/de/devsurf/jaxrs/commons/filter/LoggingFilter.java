/**
 * Copyright (C) 2010 Daniel Manzke <daniel.manzke@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.devsurf.jaxrs.commons.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * This implementation of {@link Filter} logs request and response.
 * 
 * @author Daniel Manzke
 */
public final class LoggingFilter implements Filter {

	@Override
	public void init(final FilterConfig config) throws ServletException {
		// Intentionally left blank.
	}

	@Override
	public final void destroy() {
		// Intentionally left blank.
	}

	@Override
	public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		/*
		 * This filter is only able to handle HTTP, so we bypass anything else.
		 */
		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			chain.doFilter(request, response);
			return;
		}

		this.doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private final void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException,
			ServletException {
		this.logHttpServletRequest(request);

		final ReadableHttpResponseWrapper wrapper = new ReadableHttpResponseWrapper(response);

		chain.doFilter(request, wrapper);

		this.logHttpServletResponse(wrapper);
	}

	@SuppressWarnings("unchecked")
	private final void logHttpServletRequest(final HttpServletRequest request) {
		System.out.print(String.format("%1$tF %1$tT --> %2$s %3$s %4$s", new Date(), request.getMethod(), request.getPathInfo(), request.getQueryString()));

		for (final String headerName : (List<String>) Collections.list(request.getHeaderNames()))
			for (final String headerValue : (List<String>) Collections.list(request.getHeaders(headerName)))
				System.out.print(String.format(" %s=%s", headerName, headerValue));

		System.out.println();
	}

	private final void logHttpServletResponse(final ReadableHttpResponseWrapper response) {
		System.out.print(String.format("%1$tF %1$tT <-- %2$d %3$s", new Date(), response.getStatusCode(), response.getStatusMessage()));

		for (final String headerName : response.getHeaderNames())
			for (final String headerValue : response.getHeaders(headerName))
				System.out.print(String.format(" %s=%s", headerName, headerValue));

		System.out.println();
	}

	public static final class ReadableHttpResponseWrapper extends HttpServletResponseWrapper {

		private int statusCode;

		private String statusMessage;

		private Map<String, Collection<String>> headers = new HashMap<String, Collection<String>>();
		
		public ReadableHttpResponseWrapper(final HttpServletResponse httpServletResponse) {
			super(httpServletResponse);
		}

		@Override
		public final void addDateHeader(final String name, final long date) {
			super.addDateHeader(name, date);

			final String value = new Date(date).toString();

			if (this.containsHeader(name))
				this.headers.get(name).add(value);
			else
				this.headers.put(name, new LinkedList<String>(Collections.singleton(value)));
		}

		@Override
		public final void addHeader(final String name, final String value) {
			super.addHeader(name, value);

			if (this.headers.containsKey(name))
				this.headers.get(name).add(value);
			else
				this.headers.put(name, new LinkedList<String>(Collections.singleton(value)));
		}

		@Override
		public final void addIntHeader(String name, int integer) {
			super.addIntHeader(name, integer);

			final String value = Integer.toString(integer);

			if (this.headers.containsKey(name))
				this.headers.get(name).add(value);
			else
				this.headers.put(name, new LinkedList<String>(Collections.singleton(value)));
		}

		@Override
		public final void setDateHeader(final String name, final long date) {
			super.setDateHeader(name, date);
			this.headers.put(name, new LinkedList<String>(Collections.singleton(new Date(date).toString())));
		}

		@Override
		public final void setHeader(final String name, final String value) {
			super.setHeader(name, value);
			this.headers.put(name, new LinkedList<String>(Collections.singleton(value)));
		}

		@Override
		public final void setIntHeader(final String name, final int value) {
			super.setIntHeader(name, value);
			this.headers.put(name, new LinkedList<String>(Collections.singleton(Integer.toString(value))));
		}

		@Override
		public final void setStatus(final int statusCode, final String statusMessage) {
			super.setStatus(statusCode, statusMessage);
			this.statusCode = statusCode;
			this.statusMessage = statusMessage;
		}

		@Override
		public final void setStatus(final int statusCode) {
			super.setStatus(statusCode);
			this.statusCode = statusCode;
		}

		@Override
		public final void sendError(final int statusCode) throws IOException {
			super.sendError(statusCode);
			this.statusCode = statusCode;
		}

		@Override
		public final void sendError(final int statusCode, final String statusMessage) throws IOException {
			super.sendError(statusCode, statusMessage);
			this.statusCode = statusCode;
			this.statusMessage = statusMessage;
		}

		public final String getStatusMessage() {
			return this.statusMessage;
		}

		public final int getStatusCode() {
			return this.statusCode;
		}

		public final Collection<String> getHeaderNames() {
			return this.headers.keySet();
		}

		public final Collection<String> getHeaders(final String name) {
			return this.headers.get(name);
		}
	}
}