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
import net.fabricmc.mapping.tree.Descriptored;
import net.fabricmc.mapping.tree.FieldDef;
import net.fabricmc.mapping.tree.MethodDef;
import net.fabricmc.mapping.tree.TinyTree;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.Mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

final class MappingsJoiner extends MappingsReader {
	private final TinyTree sourceMappings, targetMappings;
	private final String fromNamespace, toNamespace;

	/**
	 * Say A is the source mappings and B is the target mappings.
	 * It does not map from intermediary to named but rather maps from named-A to named-B, by matching intermediary names.
	 * It goes through all of the intermediary names of A, and for every such intermediary name, call it I,
	 * matches the named mapping of I in A, with the named mapping of I in B.
	 * As you might imagine, this requires intermediary mappings to be stable across all versions.
	 * Since we only use intermediary names (and not descriptors) to match, and intermediary names are unique,
	 * this will migrate methods that have had their signature changed too.
	 */
	MappingsJoiner(TinyTree sourceMappings, TinyTree targetMappings, String fromNamespace, String toNamespace) {
		this.sourceMappings = sourceMappings;
		this.targetMappings = targetMappings;
		this.fromNamespace = fromNamespace;
		this.toNamespace = toNamespace;
	}

	@Override
	public MappingSet read(MappingSet mappings) {
		Map<String, ClassDef> targetClasses = new HashMap<>();
		Map<String, FieldDef> targetFields = new HashMap<>();
		Map<String, MethodDef> targetMethods = new HashMap<>();

		for (ClassDef newClass : targetMappings.getClasses()) {
			targetClasses.put(newClass.getName(fromNamespace), newClass);

			for (FieldDef field : newClass.getFields()) {
				targetFields.put(field.getName(fromNamespace), field);
			}

			for (MethodDef method : newClass.getMethods()) {
				targetMethods.put(method.getName(fromNamespace), method);
			}
		}

		for (ClassDef oldClass : sourceMappings.getClasses()) {
			String namedMappingOfSourceMapping = oldClass.getName(toNamespace);
			String namedMappingOfTargetMapping = targetClasses.getOrDefault(oldClass.getName(fromNamespace), oldClass).getName(toNamespace);

			ClassMapping classMapping = mappings.getOrCreateClassMapping(namedMappingOfSourceMapping).setDeobfuscatedName(namedMappingOfTargetMapping);

			mapMembers(oldClass.getFields(), targetFields, classMapping::getOrCreateFieldMapping);
			mapMembers(oldClass.getMethods(), targetMethods, classMapping::getOrCreateMethodMapping);
		}

		return mappings;
	}

	private <T extends Descriptored> void mapMembers(Collection<T> oldMembers, Map<String, T> newMembers,
													 BiFunction<String, String, Mapping> mapper) {
		for (T oldMember : oldMembers) {
			String oldName = oldMember.getName(toNamespace);
			String oldDescriptor = oldMember.getDescriptor(toNamespace);
			// We only use the intermediary name (and not the descriptor) because every method has a unique intermediary name
			String newName = newMembers.getOrDefault(oldMember.getName(fromNamespace), oldMember).getName(toNamespace);

			mapper.apply(oldName, oldDescriptor).setDeobfuscatedName(newName);
		}
	}

	@Override
	public void close() {
	}
}
