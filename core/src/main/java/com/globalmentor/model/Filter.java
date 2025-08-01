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

package com.globalmentor.model;

import java.util.function.Predicate;

/**
 * Indicates a class that determines whether a given object will pass through the filter or be filtered out by use of the {@link #isPass(Object)} method.
 * @author Garret Wilson
 * @param <T> The type of object being filtered.
 * @deprecated in favor of {@link java.util.function.Predicate}.
 */
@FunctionalInterface
@Deprecated(forRemoval = true)
public interface Filter<T> extends Predicate<T> {

	/**
	 * Determines whether a given object should pass through the filter or be filtered out.
	 * @param object The object to filter.
	 * @return <code>true</code> if the object should pass through the filter, else <code>false</code> if the object should be filtered out.
	 */
	public boolean isPass(final T object);

	/**
	 * {@inheritDoc}
	 * @implSpec This implementation delegates to {@link #isPass(Object)}.
	 */
	@Override
	default boolean test(final T t) {
		return isPass(t);
	}

}
