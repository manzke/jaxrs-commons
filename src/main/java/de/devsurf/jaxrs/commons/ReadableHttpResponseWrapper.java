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
package de.devsurf.jaxrs.commons;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Daniel Manzke
 *
 */
public final class ReadableHttpResponseWrapper extends HttpServletResponseWrapper {

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