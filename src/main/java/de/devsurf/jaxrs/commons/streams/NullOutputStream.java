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
package de.devsurf.jaxrs.commons.streams;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An <code>OutputStream</code> that discards the actual data but just counts
 * the number of bytes written.
 * 
 * @see #getSize()
 * 
 * @author Daniel Manzke
 */
public final class NullOutputStream extends OutputStream {

	private int size;

	@Override
	public final void write(final byte[] b, final int off, final int len) throws IOException {
		this.size += len;
	}

	@Override
	public final void write(final byte[] b) throws IOException {
		this.size += b.length;
	}

	@Override
	public final void write(final int b) throws IOException {
		this.size++;
	}

	/**
	 * @return The number of bytes written into this stream.
	 */
	public final int getSize() {
		return this.size;
	}

}
