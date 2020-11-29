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
import java.util.ArrayList;
import java.util.List;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.MethodMapping;

/**
 * A {@link MappingsWriter mappings writer} for Fabric's legacy
 * (v1) Tiny format.
 *
 * @author Kyle Wood
 * @see TinyMappingFormat
 * @since 3.0.0
 */
public class TinyMappingsLegacyWriter extends AbstractTinyMappingsWriter {

	private final List<ClassMapping<?, ?>> classMappings = new ArrayList<>();
	private final List<FieldMapping> fieldMappings = new ArrayList<>();
	private final List<MethodMapping> methodMappings = new ArrayList<>();

	public TinyMappingsLegacyWriter(final Writer writer, final String from, final String to) {
		super(writer, from, to);
	}

	@Override
	public void write(final MappingSet mappings) {
		this.writeHeaderLine();

		mappings.getTopLevelClassMappings().forEach(this::collectMappings);

		this.classMappings.stream()
				.filter(ClassMapping::hasDeobfuscatedName)
				.sorted(this.getConfig().getClassMappingComparator())
				.forEach(this::writeClassMapping);

		this.fieldMappings.stream()
				.filter(FieldMapping::hasDeobfuscatedName)
				.sorted(this.getConfig().getFieldMappingComparator())
				.forEach(this::writeFieldMapping);

		this.methodMappings.stream()
				.filter(MethodMapping::hasDeobfuscatedName)
				.sorted(this.getConfig().getMethodMappingComparator())
				.forEach(this::writeMethodMapping);
	}

	private void writeHeaderLine() {
		this.writer.print("v1\t");
		this.writer.print(this.from);
		this.writer.print('\t');
		this.writer.print(this.to);
		this.writer.println();
	}

	private void collectMappings(final ClassMapping<?, ?> mapping) {
		this.classMappings.add(mapping);

		this.fieldMappings.addAll(mapping.getFieldMappings());
		this.methodMappings.addAll(mapping.getMethodMappings());
		mapping.getInnerClassMappings().forEach(this::collectMappings);
	}

	private void writeClassMapping(final ClassMapping<?, ?> mapping) {
		this.writer.print("CLASS\t");
		this.writer.print(mapping.getFullObfuscatedName());
		this.writer.print('\t');
		this.writer.println(mapping.getFullDeobfuscatedName());
	}

	private void writeFieldMapping(final FieldMapping mapping) {
		this.writer.print("FIELD\t");
		this.writer.print(mapping.getParent().getFullObfuscatedName());
		this.writer.print('\t');
		this.writer.print(this.requireType(mapping));
		this.writer.print('\t');
		this.writer.print(mapping.getObfuscatedName());
		this.writer.print('\t');
		this.writer.println(mapping.getDeobfuscatedName());
	}

	private void writeMethodMapping(final MethodMapping mapping) {
		this.writer.print("METHOD\t");
		this.writer.print(mapping.getParent().getFullObfuscatedName());
		this.writer.print('\t');
		this.writer.print(mapping.getObfuscatedDescriptor());
		this.writer.print('\t');
		this.writer.print(mapping.getObfuscatedName());
		this.writer.print('\t');
		this.writer.println(mapping.getDeobfuscatedName());
	}
}
