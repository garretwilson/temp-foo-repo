/*
 * Copyright © 2016 GlobalMentor, Inc. <https://www.globalmentor.com/>
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

package com.globalmentor.java;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.ToIntBiFunction;
import java.util.stream.Stream;

import static com.github.npathai.hamcrestopt.OptionalMatchers.*;
import static com.globalmentor.java.CharSequences.*;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.jupiter.api.*;

import com.globalmentor.collections.ListsTest;

/**
 * Tests of {@link CharSequences}.
 * @author Garret Wilson
 */
public class CharSequencesTest {

	/** @see CharSequences#constrain(CharSequence, int, ToIntBiFunction, CharSequence) */
	@Test
	void testConstrain() {
		assertThat("Happy path truncating in the middle.", constrain("foobar", 5, (cs, os) -> cs.length() / 2, "…"), hasToString("foo…r"));
		assertThat("Zero max length always yields empty string.", constrain("foobar", 0, (cs, os) -> cs.length() / 2, "…"), hasToString(""));
		assertThat("String length equal to max length", constrain("foobar", 6, (cs, os) -> cs.length() / 2, "…"), hasToString("foobar"));
		assertThat("String already constrained", constrain("foobar", 10, (cs, os) -> cs.length() / 2, "…"), hasToString("foobar"));
		assertThat("Omission string length equal to max length.", constrain("foobar", 3, (cs, os) -> cs.length() / 2, "..."), hasToString("..."));
		assertThat("Omission string length less than max length.", constrain("foobar", 2, (cs, os) -> cs.length() / 2, "..."), hasToString(".."));
		assertThat("Truncate at the beginning.", constrain("foobar", 4, (cs, os) -> 0, "…"), hasToString("…bar"));
		assertThat("Truncate at the end.", constrain("foobar", 4, (cs, os) -> 5, "…"), hasToString("foo…"));
		assertThat("Truncate toward beginning.", constrain("foobar", 5, (cs, os) -> 1, "..."), hasToString("f...r"));
		assertThat("Truncate toward end with insufficient space.", constrain("foobar", 5, (cs, os) -> 4, "..."), hasToString("fo..."));
		assertThat("No omission string truncating at the beginning.", constrain("foobar", 4, (cs, os) -> 0, ""), hasToString("obar"));
		assertThat("No omission string truncating in the middle.", constrain("foobar", 4, (cs, os) -> cs.length() / 2, ""), hasToString("foor"));
		assertThat("No omission string truncating at the end.", constrain("foobar", 4, (cs, os) -> 5, ""), hasToString("foob"));
		assertThrows(IndexOutOfBoundsException.class, () -> constrain("foobar", 5, (cs, os) -> -1, "…"), "Truncate index strategy cannot return negative index.");
		assertThrows(IndexOutOfBoundsException.class, () -> constrain("foobar", 5, (cs, os) -> 6, "…"),
				"Truncate index strategy cannot return length of character sequence.");
		assertThrows(IndexOutOfBoundsException.class, () -> constrain("foobar", 5, (cs, os) -> 7, "…"),
				"Truncate index strategy cannot return greater than length of character sequence.");
	}

	/** @see CharSequences#CONSTRAIN_TRUNCATE_MIDDLE */
	@Test
	void testConstrainTruncateMiddle() {
		assertThat(CONSTRAIN_TRUNCATE_MIDDLE.applyAsInt("foo", ""), is(2));
		assertThat(CONSTRAIN_TRUNCATE_MIDDLE.applyAsInt("foo", "…"), is(1));
		assertThat(CONSTRAIN_TRUNCATE_MIDDLE.applyAsInt("foo", ".."), is(1));
		assertThat(CONSTRAIN_TRUNCATE_MIDDLE.applyAsInt("foo", "..."), is(0));
		assertThat(CONSTRAIN_TRUNCATE_MIDDLE.applyAsInt("food", "…"), is(2));
		assertThat(CONSTRAIN_TRUNCATE_MIDDLE.applyAsInt("foobar", ""), is(3));
		assertThat(CONSTRAIN_TRUNCATE_MIDDLE.applyAsInt("foobar", "…"), is(3));
		assertThat(CONSTRAIN_TRUNCATE_MIDDLE.applyAsInt("foobar", ".."), is(2));
		assertThat(CONSTRAIN_TRUNCATE_MIDDLE.applyAsInt("foobar", "..."), is(2));
		assertThat(CONSTRAIN_TRUNCATE_MIDDLE.applyAsInt("foobar", "...."), is(1));
	}

	/** @see CharSequences#unescapeHex(CharSequence, char, int) */
	@Test
	void testUnescapeHex() {
		final String input = "abc";
		//Four lengths of UTF-8 sequences:
		//$: 0x24
		//¢: 0xC2 0xA2
		//€: 0xE2 0x82 0xAC
		//😂 : 0xF0 0x9F 0x98 0x82 
		final Map<String, String> escapeSequences = Map.of("$", "^24", "¢", "^C2^A2", "€", "^E2^82^AC", "😂", "^F0^9F^98^82");

		assertThat(unescapeHex("", '^', 2), hasToString(""));
		assertThat(unescapeHex("a", '^', 2), hasToString("a"));
		assertThat(unescapeHex("ab", '^', 2), hasToString("ab"));
		assertThat(unescapeHex("abc", '^', 2), hasToString("abc"));

		escapeSequences.forEach((decoded, encoded) -> {

			assertThat(unescapeHex(encoded, '^', 2), hasToString(decoded));
			assertThat(unescapeHex("^58" + encoded, '^', 2), hasToString("X" + decoded));
			assertThat(unescapeHex(encoded + "^58", '^', 2), hasToString(decoded + "X"));

			//try the escape sequence in every position, including the last
			//e.g. "^24abc", "a^24bc", "ab^24c", "abc^24"
			for(int i = 0; i <= input.length(); i++) {
				final String test = Strings.insert(input, i, encoded);
				final String expected = Strings.insert(input, i, decoded);
				assertThat(unescapeHex(test, '^', 2), hasToString(expected));
				assertThat(unescapeHex("^58" + test, '^', 2), hasToString("X" + expected));
				assertThat(unescapeHex(test + "^58", '^', 2), hasToString(expected + "X"));
			}
		});
	}

	/**
	 * @see CharSequences#longestCommonSegmentSuffix(List, char)
	 * @see ListsTest#testLongestCommonSuffix()
	 */
	@Test
	void testLongestCommonSegmentSuffix() {
		assertThat(longestCommonSegmentSuffix(asList(), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("foo", ""), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("foo.", ""), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("", "bar"), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList(".", "bar"), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("foo.bar", ""), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("foo.bar.", ""), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("", "foo.bar"), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList(".", "foo.bar"), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("foo", "bar"), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("foo.", "bar"), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("foo.bar", "bar.foo"), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("foo.bar.", "bar.foo"), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("foo.bar", "bar.foo."), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("foo.bar.", "bar.foo."), '.'), isEmpty());
		assertThat(longestCommonSegmentSuffix(asList("bar", "bar"), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("bar.", "bar"), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("bar", "bar."), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("bar.", "bar."), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("foo.bar", "bar"), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("foo.bar.", "bar"), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("foo.bar", "bar."), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("foo.bar.", "bar."), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("bar", "foo.bar"), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("bar.", "foo.bar"), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("bar", "foo.bar."), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("bar.", "foo.bar."), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList(".bar.", "foo.bar."), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList(".bar", "foo.bar."), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("bar.", ".foo.bar."), '.'), isPresentAndIs("bar"));
		assertThat(longestCommonSegmentSuffix(asList("foo.bar", "foo.bar"), '.'), isPresentAndIs("foo.bar"));
		assertThat(longestCommonSegmentSuffix(asList("foo.bar.", "foo.bar"), '.'), isPresentAndIs("foo.bar"));
		assertThat(longestCommonSegmentSuffix(asList("foo.bar", "foo.bar."), '.'), isPresentAndIs("foo.bar"));
		assertThat(longestCommonSegmentSuffix(asList("foo.bar.", "foo.bar."), '.'), isPresentAndIs("foo.bar"));
		assertThat(longestCommonSegmentSuffix(asList("example.foo.bar", "test.foo.bar"), '.'), isPresentAndIs("foo.bar"));
		assertThat(longestCommonSegmentSuffix(asList("www.example.com", "example.com"), '.'), isPresentAndIs("example.com"));
		assertThat(longestCommonSegmentSuffix(asList("example.com", "www.example.com"), '.'), isPresentAndIs("example.com"));
		assertThat(longestCommonSegmentSuffix(asList("example.com", "www.example.com", "test.example.com"), '.'), isPresentAndIs("example.com"));
		assertThat(longestCommonSegmentSuffix(asList("www.example.com", "test.example.com"), '.'), isPresentAndIs("example.com"));
	}

	/** @see CharSequences#removeMarks(CharSequence) */
	@Test
	void testRemoveMarks() {
		assertThat(removeMarks("foo"), is("foo"));
		assertThat(removeMarks("touch\u00E9"), is("touche")); //touché precomposed
		assertThat(removeMarks("touch\u0065\u0301"), is("touche")); //touché decomposed
		assertThat(removeMarks("Æneas"), is("Æneas"));
		assertThat(removeMarks("ﬁ"), is("ﬁ")); //removing marks doesn't change ligatures
		assertThat(removeMarks("हिंदी"), is("हद")); //hindi->hd
		assertThat(removeMarks("x\u20DD"), is("x")); //enclosing circle
	}

	/** @see CharSequences#normalizeForSearch(CharSequence) */
	@Test
	void testNormalizeForSearch() {
		assertThat(normalizeForSearch("foo"), is("foo"));
		assertThat(normalizeForSearch("touch\u00E9"), is("touche")); //touché precomposed
		assertThat(normalizeForSearch("touch\u0065\u0301"), is("touche")); //touché decomposed
		assertThat(normalizeForSearch("Æneas"), is("æneas")); //TODO check into how we can wind up with "aeneas"
		assertThat(normalizeForSearch("ﬁ"), is("fi"));
		assertThat(normalizeForSearch("हिंदी"), is("हद")); //hindi->hd
		assertThat(normalizeForSearch("x\u20DD"), is("x")); //enclosing circle
	}

}
