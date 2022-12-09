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

import net.fabricmc.mappingio.tree.MappingTree;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.MethodMapping;

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

	private final MappingTree tree;
	private final String from;
	private final String to;

	/**
	 * Creates a new mappings reader for Fabric's Tiny format, from a
	 * {@link MappingTree tiny tree}.
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
	 * @throws IllegalArgumentException if the {@code from} or {@code to} namespace is not present in the tiny tree
	 */
	public TinyMappingsReader(final MappingTree tree, final String from, final String to) {
		this.tree = tree;
		this.from = from;
		this.to = to;

		this.validateNamespace(tree, from);
		this.validateNamespace(tree, to);
	}

	@Override
	public MappingSet read(final MappingSet mappings) {
		for (final MappingTree.ClassMapping klass : this.tree.getClasses()) {
			final String classNameTo = klass.getName(this.to);
			final String classNameFrom = klass.getName(this.from);

			if (classNameTo == null || classNameFrom == null) continue;

			final ClassMapping<?, ?> mapping = mappings
					.getOrCreateClassMapping(classNameFrom)
					.setDeobfuscatedName(classNameTo);

			for (final MappingTree.FieldMapping field : klass.getFields()) {
				final String fieldNameTo = field.getName(this.to);
				final String fieldNameFrom = field.getName(this.from);

				if (fieldNameTo == null || fieldNameFrom == null) continue;

				mapping.getOrCreateFieldMapping(fieldNameFrom, field.getDesc(this.from))
						.setDeobfuscatedName(fieldNameTo);
			}

			for (final MappingTree.MethodMapping method : klass.getMethods()) {
				final String methodNameTo = method.getName(this.to);
				final String methodNameFrom = method.getName(this.from);

				if (methodNameTo == null || methodNameFrom == null) continue;

				final MethodMapping methodmapping = mapping
						.getOrCreateMethodMapping(methodNameFrom, method.getDesc(this.from))
						.setDeobfuscatedName(methodNameTo);

				for (final MappingTree.MethodArgMapping param : method.getArgs()) {
					methodmapping.getOrCreateParameterMapping(param.getArgPosition())
							.setDeobfuscatedName(param.getName(this.to));
				}
			}
		}

		return mappings;
	}

	@Override
	public void close() {
	}

	private void validateNamespace(MappingTree tree, String namespace) {
		if (!tree.getDstNamespaces().contains(namespace) && !tree.getSrcNamespace().equals(namespace)) {
			throw new IllegalArgumentException(String.format("Could not find namespace \"%s\" in provided tiny tree", namespace));
		}
	}

}
