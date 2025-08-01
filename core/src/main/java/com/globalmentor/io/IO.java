/*
 * Copyright © 1996-2008 GlobalMentor, Inc. <https://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globalmentor.io;

import static java.util.Objects.*;

import java.io.*;

import javax.annotation.*;

/**
 * I/O utilities.
 * @author Garret Wilson
 */
public final class IO {

	private IO() {
	}

	/**
	 * Converts an object to a {@link Closeable} instance so that it can be used with try-with-resources.
	 * @apiNote A more general auto-closeable interface would be {@link AutoCloseable}, although that interface throws more generalized exceptions; a similar
	 *          method for {@link AutoCloseable} is available at {@link com.globalmentor.java.Objects#toAutoCloseable(Object)}.
	 * @implSpec If the given object is an instance of {@link Closeable}, the object itself is returned; otherwise, a no-operation {@link Closeable} instance is
	 *           returned.
	 * @param object The object to convert to a {@link Closeable}.
	 * @return A {@link Closeable} instance that will ensure the object is closed if it implements {@link Closeable}.
	 * @see com.globalmentor.java.Objects#toAutoCloseable(Object)
	 */
	public static Closeable toCloseable(@Nonnull final Object object) {
		if(object instanceof Closeable) {
			return (Closeable)object;
		}
		requireNonNull(object); //if the object was auto-closeable above, we didn't need the null check
		return () -> { //no-op
		};
	}

}
