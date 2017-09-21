/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core

 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information on Heaton Research copyrights, licenses
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.prg.extension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all known EPL opcodes. Extension programs should add new opcodes here.
 * The FunctionFactory selects a subset of opcodes from here that will be run.
 *
 * An opcode is identified by its name, and the number of parameters it accepts.
 * It is okay to add an opcode multiple times, the new opcode replaces the
 * previous.
 *
 */
public enum EncogOpcodeRegistry {
	INSTANCE;

	/**
	 * Construct a lookup key for the hash map.
	 *
	 * @param functionName
	 *            The name of the opcode.
	 * @param argCount
	 *            The number of parameters this opcode accepts.
	 * @return Return the string key.
	 */
	public static String createKey(final String functionName, final int argCount) {
		return functionName + '`' + argCount;
	}

	/**
	 * A lookup for all of the opcodes.
	 */
	private final Map<String, ProgramExtensionTemplate> registry = new HashMap<>();

	/**
	 * Construct the opcode registry with all known opcodes. User programs can
	 * always add additional opcodes later.
	 */
	private EncogOpcodeRegistry() {
		this.add(StandardExtensions.EXTENSION_NOT_EQUAL);
		this.add(StandardExtensions.EXTENSION_NOT);
		this.add(StandardExtensions.EXTENSION_VAR_SUPPORT);
		this.add(StandardExtensions.EXTENSION_CONST_SUPPORT);
		this.add(StandardExtensions.EXTENSION_NEG);
		this.add(StandardExtensions.EXTENSION_ADD);
		this.add(StandardExtensions.EXTENSION_SUB);
		this.add(StandardExtensions.EXTENSION_MUL);
		this.add(StandardExtensions.EXTENSION_DIV);
		this.add(StandardExtensions.EXTENSION_POWER);
		this.add(StandardExtensions.EXTENSION_AND);
		this.add(StandardExtensions.EXTENSION_OR);
		this.add(StandardExtensions.EXTENSION_EQUAL);
		this.add(StandardExtensions.EXTENSION_GT);
		this.add(StandardExtensions.EXTENSION_LT);
		this.add(StandardExtensions.EXTENSION_GTE);
		this.add(StandardExtensions.EXTENSION_LTE);
		this.add(StandardExtensions.EXTENSION_ABS);
		this.add(StandardExtensions.EXTENSION_ACOS);
		this.add(StandardExtensions.EXTENSION_ASIN);
		this.add(StandardExtensions.EXTENSION_ATAN);
		this.add(StandardExtensions.EXTENSION_ATAN2);
		this.add(StandardExtensions.EXTENSION_CEIL);
		this.add(StandardExtensions.EXTENSION_COS);
		this.add(StandardExtensions.EXTENSION_COSH);
		this.add(StandardExtensions.EXTENSION_EXP);
		this.add(StandardExtensions.EXTENSION_FLOOR);
		this.add(StandardExtensions.EXTENSION_LOG);
		this.add(StandardExtensions.EXTENSION_LOG10);
		this.add(StandardExtensions.EXTENSION_MAX);
		this.add(StandardExtensions.EXTENSION_MIN);
		this.add(StandardExtensions.EXTENSION_PDIV);
		this.add(StandardExtensions.EXTENSION_POWFN);
		this.add(StandardExtensions.EXTENSION_RANDOM);
		this.add(StandardExtensions.EXTENSION_ROUND);
		this.add(StandardExtensions.EXTENSION_SIN);
		this.add(StandardExtensions.EXTENSION_SINH);
		this.add(StandardExtensions.EXTENSION_SQRT);
		this.add(StandardExtensions.EXTENSION_TAN);
		this.add(StandardExtensions.EXTENSION_TANH);
		this.add(StandardExtensions.EXTENSION_TODEG);
		this.add(StandardExtensions.EXTENSION_TORAD);
		this.add(StandardExtensions.EXTENSION_LENGTH);
		this.add(StandardExtensions.EXTENSION_FORMAT);
		this.add(StandardExtensions.EXTENSION_LEFT);
		this.add(StandardExtensions.EXTENSION_RIGHT);
		this.add(StandardExtensions.EXTENSION_CINT);
		this.add(StandardExtensions.EXTENSION_CFLOAT);
		this.add(StandardExtensions.EXTENSION_CSTR);
		this.add(StandardExtensions.EXTENSION_CBOOL);
		this.add(StandardExtensions.EXTENSION_IFF);
		this.add(StandardExtensions.EXTENSION_CLAMP);
	}

	/**
	 * Add an opcode. User programs should add opcodes here.
	 *
	 * @param ext
	 *            The opcode to add.
	 */
	public void add(final ProgramExtensionTemplate ext) {
		this.registry.put(EncogOpcodeRegistry.createKey(ext.getName(), ext.getChildNodeCount()), ext);
	}

	public Collection<ProgramExtensionTemplate> findAllOpcodes() {
		return this.registry.values();
	}

	/**
	 * Find the specified opcode.
	 *
	 * @param name
	 *            The name of the opcode.
	 * @param args
	 *            The number of arguments.
	 * @return The opcode if found, null otherwise.
	 */
	public ProgramExtensionTemplate findOpcode(final String name, final int args) {
		final String key = EncogOpcodeRegistry.createKey(name, args);
		if (this.registry.containsKey(key)) {
			return this.registry.get(key);
		} else {
			return null;
		}
	}

}
