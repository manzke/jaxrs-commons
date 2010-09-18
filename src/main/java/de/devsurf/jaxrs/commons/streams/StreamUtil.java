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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class StreamUtil {
    public static int BUFFER_SIZE = 65536;

    public static void copyInToOut(InputStream in, OutputStream out, int bufferSize,
	    boolean forceClose) throws IOException {
	BufferedInputStream bis = new BufferedInputStream(in, bufferSize);
	BufferedOutputStream bos = new BufferedOutputStream(out, bufferSize);

	try {
	    byte[] buffer = new byte[bufferSize];
	    for (;;) {
		int len = bis.read(buffer);
		if (len < 0)
		    break;
		bos.write(buffer, 0, len);
	    }
	    bos.flush();
	} finally {
	    if (forceClose) {
		try {
		    bis.close();
		} catch (Exception e) {
		    // ignore
		}
		try {
		    bos.close();
		} catch (Exception e) {
		    // ignore
		}
	    }
	}
    }

    public static void copyInToOut(Reader in, Writer out, int bufferSize, boolean forceClose)
	    throws IOException {
	BufferedWriter writer = new BufferedWriter(out, bufferSize);
	BufferedReader reader = new BufferedReader(in, bufferSize);

	try {
	    char[] buffer = new char[bufferSize];
	    for (;;) {
		int len = reader.read(buffer);
		if (len < 0)
		    break;
		writer.write(buffer, 0, len);
	    }
	    writer.flush();
	} finally {
	    if (forceClose) {
		try {
		    reader.close();
		} catch (Exception e) {
		    // ignore
		}
		try {
		    writer.close();
		} catch (Exception e) {
		    // ignore
		}
	    }
	}
    }

    public static byte[] append(byte[] array1, byte[] array2) {
	byte[] newArray = new byte[array1.length + array2.length];

	System.arraycopy(array1, 0, newArray, 0, array1.length);
	System.arraycopy(array2, 0, newArray, array1.length, array2.length);

	return newArray;
    }

    public static byte[] readBytes(InputStream in, int bufferSize, boolean forceClose)
	    throws Exception {
	ByteArrayOutputStream baos = new ByteArrayOutputStream(bufferSize);

	copyInToOut(in, baos, bufferSize, forceClose);
	return baos.toByteArray();
    }

    public static byte[] readBytes(InputStream in, boolean forceClose) throws Exception {
	InputStream is = new BufferedInputStream(in, BUFFER_SIZE);

	ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFFER_SIZE);
	BufferedOutputStream bos = new BufferedOutputStream(baos, BUFFER_SIZE);

	try {
	    byte[] buffer = new byte[BUFFER_SIZE];
	    for (;;) {
		int len = is.read(buffer);
		if (len < 0)
		    break;
		bos.write(buffer, 0, len);
	    }
	    bos.flush();
	} finally {
	    if (forceClose) {
		try {
		    is.close();
		} catch (Exception e) {
		    // ignore
		}
		try {
		    bos.close();
		} catch (Exception e) {
		    // ignore
		}
	    }
	}

	return baos.toByteArray();
    }

    public static boolean compare(InputStream content1, InputStream content2) throws IOException {
	// read and compare bytes pair-wise
	InputStream i1 = new BufferedInputStream(content1);
	InputStream i2 = new BufferedInputStream(content2);
	int b1, b2;
	do {
	    b1 = i1.read();
	    b2 = i2.read();
	} while (b1 == b2 && b1 != -1);
	i1.close();
	i2.close();
	// true only if end of file is reached
	return b1 == -1;
    }
}
