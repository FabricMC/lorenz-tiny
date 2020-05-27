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

import net.fabricmc.mapping.tree.ClassDef;
import net.fabricmc.mapping.tree.FieldDef;
import net.fabricmc.mapping.tree.MethodDef;
import net.fabricmc.mapping.tree.TinyTree;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;

/**
 * A {@link MappingsReader mappings reader} for Fabric's Tiny
 * format.
 * 
 * @see TinyMappingFormat
 *
 * @author Jamie Mansfield
 * @since 2.0.0
 */
public class TinyMappingsReader extends MappingsReader {

	private final TinyTree tree;
	private final String from;
	private final String to;

	/**
	 * Creates a new mappings reader for Fabric's Tiny format, from a
	 * {@link TinyTree tiny tree}.
	 * <p>
	 * The resulting {@link MappingSet mapping set} will have mappings
	 * using the from namespace (as the obfuscated names in Lorenz) to
	 * the to namespace (as the de-obfuscated names in Lorenz).
	 *
	 * @param tree The Tiny mappings tree to create mappings from
	 * @param from The namespace to use in the tiny file, as Lorenz's
	 *             obfuscated names
	 * @param to   The namespace to use in the tiny file, as Lorenz's
	 *             de-obfuscated names
	 */
	public TinyMappingsReader(final TinyTree tree, final String from, final String to) {
		this.tree = tree;
		this.from = from;
		this.to = to;
	}

	@Override
	public MappingSet read(final MappingSet mappings) {
		for (final ClassDef klass : this.tree.getClasses()) {
			final ClassMapping<?, ?> mapping = mappings.getOrCreateClassMapping(klass.getName(this.from))
					.setDeobfuscatedName(klass.getName(this.to));

			for (final FieldDef field : klass.getFields()) {
				mapping.getOrCreateFieldMapping(field.getName(this.from), field.getDescriptor(this.from))
						.setDeobfuscatedName(field.getName(this.to));
			}

			for (final MethodDef method : klass.getMethods()) {
				mapping.getOrCreateMethodMapping(method.getName(this.from), method.getDescriptor(this.from))
						.setDeobfuscatedName(method.getName(this.to));
			}
		}

		return mappings;
	}

	@Override
	public void close() {
	}

}
