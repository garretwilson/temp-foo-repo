/*
 * Copyright © 2023 GlobalMentor, Inc. <https://www.globalmentor.com/>
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

package com.globalmentor.io.function;

import static java.util.Arrays.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.*;

/**
 * Tests of {@link Functions}.
 * @author Garret Wilson
 */
public class FunctionsTest {

	/** {@link Functions#countingConsumer(java.util.function.BiConsumer)} */
	@Test
	void testCountingConsumer() {
		final List<Map.Entry<String, Long>> results = new ArrayList<>();
		asList("foo", "bar", "baz").forEach(Functions.countingConsumer((id, count) -> results.add(Map.entry(id, count))));
		assertThat(results, contains(Map.entry("foo", 1L), Map.entry("bar", 2L), Map.entry("baz", 3L)));
	}

	/** {@link Functions#countingConsumer(AtomicLong, java.util.function.BiConsumer) */
	@Test
	void testCountingConsumerWithProvidedCounter() {
		final AtomicLong counter = new AtomicLong(5);
		final List<Map.Entry<String, Long>> results = new ArrayList<>();
		asList("foo", "bar", "baz").forEach(Functions.countingConsumer(counter, (id, count) -> results.add(Map.entry(id, count))));
		assertThat(results, contains(Map.entry("foo", 6L), Map.entry("bar", 7L), Map.entry("baz", 8L)));
	}

	/** {@link Functions#countingConsumer(java.util.function.BiConsumer)} */
	@Test
	void testCountingConsumerEmpty() {
		final List<Map.Entry<String, Long>> results = new ArrayList<>();
		Collections.<String>emptySet().forEach(Functions.countingConsumer((id, count) -> results.add(Map.entry(id, count))));
		assertThat(results, is(empty()));
	}

}
