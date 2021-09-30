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

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link MappingsReader mappings reader} for reading mappings
 * from across two {@link MappingTree tiny trees}, using a namespace
 * present in both to match.
 *
 * @author Jamie Mansfield
 * @since 2.0.0
 */
public class TinyMappingsJoiner extends MappingsReader {

	private final MappingTree treeA;
	private final String from;
	private final String matchA;

	private final MappingTree treeB;
	private final String to;
	private final String matchB;

	public TinyMappingsJoiner(final MappingTree treeA, final String from, final String matchA,
	                          final MappingTree treeB, final String to, final String matchB) {
		this.treeA = treeA;
		this.from = from;
		this.matchA = matchA;
		this.treeB = treeB;
		this.to = to;
		this.matchB = matchB;
	}

	public TinyMappingsJoiner(final MappingTree treeA, final String from,
	                          final MappingTree treeB, final String to,
	                          final String match) {
		this(treeA, from, match, treeB, to, match);
	}

	@Override
	public MappingSet read(final MappingSet mappings) {
		// These maps have matched name -> definition from a
		final Map<String, MappingTree.ClassMapping> classes = new HashMap<>();
		final Map<String, MappingTree.FieldMapping> fields = new HashMap<>();
		final Map<String, MappingTree.MethodMapping> methods = new HashMap<>();

		for (final MappingTree.ClassMapping klass : this.treeB.getClasses()) {
			classes.put(klass.getName(this.matchA), klass);

			for (final MappingTree.FieldMapping field : klass.getFields()) {
				fields.put(field.getName(this.matchA), field);
			}

			for (final MappingTree.MethodMapping method : klass.getMethods()) {
				methods.put(method.getName(this.matchA), method);
			}
		}

		for (final MappingTree.ClassMapping classA : this.treeA.getClasses()) {
			final MappingTree.ClassMapping classB = classes.get(classA.getName(this.matchB));

			final ClassMapping<?, ?> klass = mappings.getOrCreateClassMapping(classA.getName(this.from));
			if (classB != null) {
				String deobfName = classB.getName(this.to);

				if (deobfName != null) {
					klass.setDeobfuscatedName(deobfName);
				}
			}

			for (final MappingTree.FieldMapping fieldA : classA.getFields()) {
				final MappingTree.FieldMapping fieldB = fields.get(fieldA.getName(this.matchB));

				if (fieldB != null) {
					String deobfName = fieldB.getName(this.to);

					if (deobfName != null) {
						klass.getOrCreateFieldMapping(fieldA.getName(this.from), fieldA.getDesc(this.from))
								.setDeobfuscatedName(deobfName);
					}
				}
			}

			for (final MappingTree.MethodMapping methodA : classA.getMethods()) {
				final MappingTree.MethodMapping methodB = methods.get(methodA.getName(this.matchB));

				if (methodB != null) {
					String deobfName = methodB.getName(this.to);

					if (deobfName != null) {
						klass.getOrCreateMethodMapping(methodA.getName(this.from), methodA.getDesc(this.from))
								.setDeobfuscatedName(deobfName);
					}
				}
			}
		}

		return mappings;
	}

	@Override
	public void close() {
	}

}
