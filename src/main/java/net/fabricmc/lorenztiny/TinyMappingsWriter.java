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

import java.io.Writer;
import java.util.Comparator;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.MethodParameterMapping;

/**
 * A {@link org.cadixdev.lorenz.io.MappingsWriter mappings writer} for Fabric's v2
 * Tiny format.
 *
 * @author Kyle Wood
 * @see TinyMappingFormat
 * @since 3.0.0
 */
public class TinyMappingsWriter extends AbstractTinyMappingsWriter {

	public TinyMappingsWriter(final Writer writer, final String from, final String to) {
		super(writer, from, to);
	}

	@Override
	public void write(final MappingSet mappings) {
		this.writeHeaderLine();

		mappings.getTopLevelClassMappings().stream()
				.filter(ClassMapping::hasMappings)
				.sorted(this.getConfig().getClassMappingComparator())
				.forEach(this::writeClassMapping);
	}

	private void writeClassMapping(final ClassMapping<?, ?> mapping) {
		this.writer.print("c\t");
		this.writer.print(mapping.getFullObfuscatedName());
		this.writer.print('\t');
		this.writer.println(mapping.getFullDeobfuscatedName());

		mapping.getMethodMappings().stream()
				.filter(MethodMapping::hasMappings)
				.sorted(this.getConfig().getMethodMappingComparator())
				.forEach(this::writeMethodMapping);

		mapping.getFieldMappings().stream()
				.filter(FieldMapping::hasDeobfuscatedName)
				.sorted(this.getConfig().getFieldMappingComparator())
				.forEach(this::writeFieldMapping);

		mapping.getInnerClassMappings().stream()
				.filter(ClassMapping::hasMappings)
				.sorted(this.getConfig().getClassMappingComparator())
				.forEach(this::writeClassMapping);
	}

	private void writeMethodMapping(final MethodMapping mapping) {
		this.writer.print("\tm\t");
		this.writer.print(mapping.getObfuscatedDescriptor());
		this.writer.print('\t');
		this.writer.print(mapping.getObfuscatedName());
		this.writer.print('\t');
		this.writer.println(mapping.getDeobfuscatedName());

		mapping.getParameterMappings().stream()
				.filter(MethodParameterMapping::hasDeobfuscatedName)
				.sorted(Comparator.comparingInt(MethodParameterMapping::getIndex))
				.forEach(this::writeParamMapping);
	}

	private void writeParamMapping(final MethodParameterMapping mapping) {
		this.writer.print("\t\tp\t");
		this.writer.print(mapping.getIndex());
		this.writer.print("\t\t");
		this.writer.println(mapping.getDeobfuscatedName());
	}

	private void writeFieldMapping(final FieldMapping mapping) {
		this.writer.print("\tf\t");
		this.writer.print(this.requireType(mapping));
		this.writer.print('\t');
		this.writer.print(mapping.getObfuscatedName());
		this.writer.print('\t');
		this.writer.println(mapping.getDeobfuscatedName());
	}

	private void writeHeaderLine() {
		this.writer.print("tiny\t2\t0\t");
		this.writer.print(this.from);
		this.writer.print('\t');
		this.writer.print(this.to);
		this.writer.println();
	}
}
