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

import net.fabricmc.mapping.tree.TinyMappingFactory;
import net.fabricmc.mapping.tree.TinyTree;
import org.cadixdev.lorenz.io.MappingsReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface LorenzTiny {
	/**
	 * Create a {@link MappingsReader} from a pair of {@link TinyTree} using the to and from namespaces
	 *
	 * @param sourceMappings The source mappings
	 * @param targetMappings The target mappings
	 * @param fromNamespace The source namespace
	 * @param toNamespace The target namespace
	 * @return An instance of {@link MappingsReader}
	 */
	static MappingsReader readMappings(TinyTree sourceMappings, TinyTree targetMappings, String fromNamespace, String toNamespace) {
		return new MappingsJoiner(sourceMappings, targetMappings, fromNamespace, toNamespace);
	}

	/**
	 * Create a {@link MappingsReader} from a {@link TinyTree} using the to and from namespaces
	 *
	 * @param mappings The input mappings
	 * @param fromNamespace The source namespace
	 * @param toNamespace The target namespace
	 * @return An instance of {@link MappingsReader}
	 */
	static MappingsReader readMappings(TinyTree mappings, String fromNamespace, String toNamespace) {
		return readMappings(mappings, mappings, fromNamespace, toNamespace);
	}

	/**
	 * A helper method to read a {@link TinyTree} from a {@link Path}
	 * @param path the path to the tiny mappings
	 * @return An instance of {@link TinyTree}
	 */
	static TinyTree readTinyMappings(Path path) throws IOException {
		try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
			return TinyMappingFactory.loadWithDetection(bufferedReader);
		}
	}
}
