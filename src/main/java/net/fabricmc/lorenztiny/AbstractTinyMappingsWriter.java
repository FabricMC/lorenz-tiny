/*
 * This file is part of lorenz-tiny, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.fabricmc.lorenztiny;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.Writer;
import org.cadixdev.bombe.type.FieldType;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.model.FieldMapping;

abstract class AbstractTinyMappingsWriter extends MappingsWriter {

	protected final PrintWriter writer;

	protected final String from;
	protected final String to;

	AbstractTinyMappingsWriter(final Writer writer, final String from, final String to) {
		this.writer = writer instanceof PrintWriter ?
				(PrintWriter) writer :
				writer instanceof BufferedWriter ?
						new PrintWriter(writer) :
						new PrintWriter(new BufferedWriter(writer));
		this.from = from;
		this.to = to;
	}

	protected FieldType requireType(final FieldMapping mapping) {
		return mapping.getType().orElseThrow(() ->
				new IllegalStateException("Field mapping does not have type information: " + mapping));
	}

	@Override
	public void close() {
		this.writer.close();
	}
}
