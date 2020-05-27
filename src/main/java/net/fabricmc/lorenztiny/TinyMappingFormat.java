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

import net.fabricmc.mapping.reader.v2.MappingParseException;
import net.fabricmc.mapping.tree.TinyMappingFactory;
import net.fabricmc.mapping.tree.TinyTree;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingFormat;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.model.Mapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A helper class for working with Tiny mappings with Lorenz,
 * in lieu of having a {@link MappingFormat mapping format}.
 *
 * @author Jamie Mansfield
 * @since 2.0.0
 */
public enum TinyMappingFormat {

	/**
	 * Reads tiny mappings with new standard ({@code "tiny"} header).
	 *
	 * @see TinyMappingFactory#load(BufferedReader)
	 */
	STANDARD {
		@Override
		protected TinyTree load(final BufferedReader reader) throws IOException, MappingParseException {
			return TinyMappingFactory.load(reader);
		}
	},

	/**
	 * Reads tiny mappings with legacy format ({@code "v1"} header).
	 *
	 * @see TinyMappingFactory#loadLegacy(BufferedReader)
	 */
	LEGACY {
		@Override
		protected TinyTree load(final BufferedReader reader) throws IOException, MappingParseException {
			return TinyMappingFactory.loadLegacy(reader);
		}
	},

	/**
	 * Reads tiny mappings of either the standard (new) or legacy format.
	 *
	 * @see TinyMappingFactory#loadWithDetection(BufferedReader)
	 */
	DETECT {
		@Override
		protected TinyTree load(final BufferedReader reader) throws IOException, MappingParseException {
			return TinyMappingFactory.loadWithDetection(reader);
		}
	},
	;

	protected abstract TinyTree load(final BufferedReader reader) throws IOException, MappingParseException;

	/**
	 * Creates a new {@link MappingsReader mappings reader} for the
	 * mappings contained by the path supplied.
	 * <p>
	 * The resulting {@link Mapping mappings} will use the from namespace
	 * (as the obfuscated names in Lorenz) to the to namespace (as the
	 * de-obfuscated names in Lorenz).
	 *
	 * @param path The path to the Tiny mappings
	 * @param from The namespace to use in the tiny file, as Lorenz's
	 *             obfuscated names
	 * @param to   The namespace to use in the tiny file, as Lorenz's
	 *             de-obfuscated names
	 * @return The mappings reader
	 * @throws IOException if an I/O error occurs opening the file
	 */
	public MappingsReader createReader(final Path path,
	                                   final String from, final String to) throws IOException, MappingParseException {
		try (final BufferedReader reader = Files.newBufferedReader(path)) {
			return new TinyMappingsReader(this.load(reader), from, to);
		}
	}

	/**
	 * Reads Tiny mappings from the given path, into the given {@link MappingSet mapping set}.
	 * <p>
	 * The resulting {@link Mapping mappings} will use the from namespace
	 * (as the obfuscated names in Lorenz) to the to namespace (as the
	 * de-obfuscated names in Lorenz).
	 *
	 * @param mappings The mapping set to read to
	 * @param path The path to the Tiny mappings
	 * @param from The namespace to use in the tiny file, as Lorenz's
	 *             obfuscated names
	 * @param to   The namespace to use in the tiny file, as Lorenz's
	 *             de-obfuscated names
	 * @return The given mapping set
	 * @throws IOException if an I/O error occurs opening the file
	 */
	public MappingSet read(final MappingSet mappings, final Path path,
	                       final String from, final String to) throws IOException, MappingParseException {
		try (final MappingsReader reader = this.createReader(path, from, to)) {
			reader.read(mappings);
		}
		return mappings;
	}

	/**
	 * Reads Tiny mappings from the given path, into a new {@link MappingSet mapping set}.
	 * <p>
	 * The resulting {@link Mapping mappings} will use the from namespace
	 * (as the obfuscated names in Lorenz) to the to namespace (as the
	 * de-obfuscated names in Lorenz).
	 *
	 * @param path The path to the Tiny mappings
	 * @param from The namespace to use in the tiny file, as Lorenz's
	 *             obfuscated names
	 * @param to   The namespace to use in the tiny file, as Lorenz's
	 *             de-obfuscated names
	 * @return The mapping set
	 * @throws IOException if an I/O error occurs opening the file
	 */
	public MappingSet read(final Path path,
	                       final String from, final String to) throws IOException, MappingParseException {
		return this.read(MappingSet.create(), path, from, to);
	}

}
