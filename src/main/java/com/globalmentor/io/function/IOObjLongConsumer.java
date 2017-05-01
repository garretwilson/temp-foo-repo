/*
 * Copyright © 2017 GlobalMentor, Inc. <http://www.globalmentor.com/>
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
 */

package com.globalmentor.io.function;

import java.io.IOException;

import java.util.function.ObjLongConsumer;

/**
 * Represents an operation that accepts an object-valued and a {@code long}-valued argument, and returns no result. This is the {@code (reference, long)}
 * specialization of {@link IOBiConsumer}. Unlike most other functional interfaces, {@code ObjLongConsumer} is expected to operate via side-effects.
 *
 * <p>
 * This interface is similar to {@link ObjLongConsumer} except that it is allowed to throw an {@link IOException}.
 * </p>
 * 
 * @param <T> the type of the object argument to the operation
 *
 * @author Magno N A Cruz
 * @see IOBiConsumer
 */
@FunctionalInterface
public interface IOObjLongConsumer<T> {

	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param t the first input argument
	 * @param value the second input argument
	 * @throws IOException if there is an I/O error performing the operation
	 */
	void accept(T t, long value) throws IOException;
}
