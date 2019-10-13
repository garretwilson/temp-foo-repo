/*
 * Copyright © 2019 GlobalMentor, Inc. <http://www.globalmentor.com/>
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

package com.globalmentor.lex;

import static com.globalmentor.lex.CompoundTokenization.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.*;

/**
 * Tests of {@link CompoundTokenization}.
 * @author Garret Wilson
 */
public class CompoundTokenizationTest {

	/** The implementations tested by this class that support round-trip split+join. */
	private static final List<CompoundTokenization> ROUND_TRIP_IMPLEMENTATIONS = unmodifiableList(asList(CAMEL_CASE, KEBAB_CASE, SNAKE_CASE));

	/** A list of tokens to use for testing. */
	private static final List<String> TEST_TOKENS = unmodifiableList(
			asList("", "foobar", "fooBar", "FooBar", "foo-bar", "foo-Bar", "foo_bar", "foo_Bar", "x", "X", "CDlibrary", "CdLibrary", "CD-library", "CD_library",
					"userCDlibrary", "userCdLibrary", "user-CD-library", "user_CD_library", "URL", "URLconverter", "UrlConverter", "URL-converter", "URL_converter",
					"oldURLconverter", "oldUrlConverter", "old-URL-converter", "old_URL_converter", "oldURL", "old-URL", "old_URL"));

	/**
	 * @see CompoundTokenization#split(CharSequence)
	 * @see CompoundTokenization#join(Iterable)
	 * @see #ROUND_TRIP_IMPLEMENTATIONS
	 * @see #TEST_TOKENS
	 */
	@Test
	public void testSplitJoinRoundTripForAllImplementations() {
		for(final CompoundTokenization tokenization : ROUND_TRIP_IMPLEMENTATIONS) {
			for(final String token : TEST_TOKENS) {
				assertThat("(" + tokenization.getName() + ") " + token, tokenization.join(tokenization.split(token)), is(token));
			}
		}
	}

	//camelCase

	/**
	 * @see CompoundTokenization#CAMEL_CASE
	 * @see CompoundTokenization#split(CharSequence)
	 */
	@Test
	public void testCamelCaseSplit() {
		assertThat(CAMEL_CASE.split(""), is(empty()));
		assertThat(CAMEL_CASE.split("foobar"), is(asList("foobar")));
		assertThat(CAMEL_CASE.split("fooBar"), is(asList("foo", "bar")));
		assertThat(CAMEL_CASE.split("FooBar"), is(asList("Foo", "bar")));
		assertThat(CAMEL_CASE.split("foo-bar"), is(asList("foo-bar")));
		assertThat(CAMEL_CASE.split("foo-Bar"), is(asList("foo-", "bar")));
		assertThat(CAMEL_CASE.split("foo_bar"), is(asList("foo_bar")));
		assertThat(CAMEL_CASE.split("foo_Bar"), is(asList("foo_", "bar")));
		assertThat(CAMEL_CASE.split("x"), is(asList("x")));
		assertThat(CAMEL_CASE.split("X"), is(asList("X")));
		assertThat(CAMEL_CASE.split("CDlibrary"), is(asList("CDlibrary")));
		assertThat(CAMEL_CASE.split("CdLibrary"), is(asList("Cd", "library")));
		assertThat(CAMEL_CASE.split("userCDlibrary"), is(asList("user", "CDlibrary")));
		assertThat(CAMEL_CASE.split("userCdLibrary"), is(asList("user", "cd", "library")));
		assertThat(CAMEL_CASE.split("URL"), is(asList("URL")));
		assertThat(CAMEL_CASE.split("URLconverter"), is(asList("URLconverter")));
		assertThat(CAMEL_CASE.split("UrlConverter"), is(asList("Url", "converter")));
		assertThat(CAMEL_CASE.split("oldURLconverter"), is(asList("old", "URLconverter")));
		assertThat(CAMEL_CASE.split("oldUrlConverter"), is(asList("old", "url", "converter")));
		assertThat(CAMEL_CASE.split("oldURL"), is(asList("old", "URL")));
	}

	/**
	 * @see CompoundTokenization#CAMEL_CASE
	 * @see CompoundTokenization#join(Iterable)
	 */
	@Test
	public void testCamelCaseJoin() {
		assertThat(CAMEL_CASE.join(asList()), is(""));
		assertThat(CAMEL_CASE.join(asList("foobar")), is("foobar"));
		assertThat(CAMEL_CASE.join(asList("foo", "bar")), is("fooBar"));
		assertThat(CAMEL_CASE.join(asList("Foo", "bar")), is("FooBar"));
		assertThat(CAMEL_CASE.join(asList("foo-bar")), is("foo-bar"));
		assertThat(CAMEL_CASE.join(asList("foo-", "bar")), is("foo-Bar"));
		assertThat(CAMEL_CASE.join(asList("foo_bar")), is("foo_bar"));
		assertThat(CAMEL_CASE.join(asList("foo_", "bar")), is("foo_Bar"));
		assertThat(CAMEL_CASE.join(asList("x")), is("x"));
		assertThat(CAMEL_CASE.join(asList("X")), is("X"));
		assertThat(CAMEL_CASE.join(asList("CDlibrary")), is("CDlibrary"));
		assertThat(CAMEL_CASE.join(asList("cd", "library")), is("cdLibrary"));
		assertThat(CAMEL_CASE.join(asList("user", "CDlibrary")), is("userCDlibrary"));
		assertThat(CAMEL_CASE.join(asList("user", "cd", "library")), is("userCdLibrary"));
		assertThat(CAMEL_CASE.join(asList("URL")), is("URL"));
		assertThat(CAMEL_CASE.join(asList("URLconverter")), is("URLconverter"));
		assertThat(CAMEL_CASE.join(asList("url", "converter")), is("urlConverter"));
		assertThat(CAMEL_CASE.join(asList("old", "URLconverter")), is("oldURLconverter"));
		assertThat(CAMEL_CASE.join(asList("old", "url", "converter")), is("oldUrlConverter"));
		assertThat(CAMEL_CASE.join(asList("old", "URL")), is("oldURL"));
	}

	/**
	 * @see CompoundTokenization#CAMEL_CASE
	 * @see CompoundTokenization#toKebabCase(CharSequence)
	 */
	@Test
	public void testCamelCaseToKebabCase() {
		assertThat(CAMEL_CASE.toKebabCase(""), is(""));
		assertThat(CAMEL_CASE.toKebabCase("foobar"), is("foobar"));
		assertThat(CAMEL_CASE.toKebabCase("fooBar"), is("foo-bar"));
		assertThat(CAMEL_CASE.toKebabCase("FooBar"), is("Foo-bar"));
		assertThrows(IllegalArgumentException.class, () -> CAMEL_CASE.toKebabCase("foo-bar"));
		assertThrows(IllegalArgumentException.class, () -> CAMEL_CASE.toKebabCase("foo-Bar"));
		assertThat(CAMEL_CASE.toKebabCase("foo_bar"), is("foo_bar"));
		assertThat(CAMEL_CASE.toKebabCase("foo_Bar"), is("foo_-bar"));
		assertThat(CAMEL_CASE.toKebabCase("x"), is("x"));
		assertThat(CAMEL_CASE.toKebabCase("X"), is("X"));
		assertThat(CAMEL_CASE.toKebabCase("CDlibrary"), is("CDlibrary"));
		assertThat(CAMEL_CASE.toKebabCase("CdLibrary"), is("Cd-library"));
		assertThat(CAMEL_CASE.toKebabCase("userCDlibrary"), is("user-CDlibrary"));
		assertThat(CAMEL_CASE.toKebabCase("userCdLibrary"), is("user-cd-library"));
		assertThat(CAMEL_CASE.toKebabCase("URL"), is("URL"));
		assertThat(CAMEL_CASE.toKebabCase("URLconverter"), is("URLconverter"));
		assertThat(CAMEL_CASE.toKebabCase("UrlConverter"), is("Url-converter"));
		assertThat(CAMEL_CASE.toKebabCase("oldURLconverter"), is("old-URLconverter"));
		assertThat(CAMEL_CASE.toKebabCase("oldUrlConverter"), is("old-url-converter"));
		assertThat(CAMEL_CASE.toKebabCase("oldURL"), is("old-URL"));
	}

	/**
	 * @see CompoundTokenization#CAMEL_CASE
	 * @see CompoundTokenization#toSnakeCase(CharSequence)
	 */
	@Test
	public void testCamelCaseToSnakeCase() {
		assertThat(CAMEL_CASE.toSnakeCase(""), is(""));
		assertThat(CAMEL_CASE.toSnakeCase("foobar"), is("foobar"));
		assertThat(CAMEL_CASE.toSnakeCase("fooBar"), is("foo_bar"));
		assertThat(CAMEL_CASE.toSnakeCase("FooBar"), is("Foo_bar"));
		assertThat(CAMEL_CASE.toSnakeCase("foo-bar"), is("foo-bar"));
		assertThat(CAMEL_CASE.toSnakeCase("foo-Bar"), is("foo-_bar"));
		assertThrows(IllegalArgumentException.class, () -> CAMEL_CASE.toSnakeCase("foo_bar"));
		assertThrows(IllegalArgumentException.class, () -> CAMEL_CASE.toSnakeCase("foo_Bar"));
		assertThat(CAMEL_CASE.toSnakeCase("x"), is("x"));
		assertThat(CAMEL_CASE.toSnakeCase("X"), is("X"));
		assertThat(CAMEL_CASE.toSnakeCase("CDlibrary"), is("CDlibrary"));
		assertThat(CAMEL_CASE.toSnakeCase("CdLibrary"), is("Cd_library"));
		assertThat(CAMEL_CASE.toSnakeCase("userCDlibrary"), is("user_CDlibrary"));
		assertThat(CAMEL_CASE.toSnakeCase("userCdLibrary"), is("user_cd_library"));
		assertThat(CAMEL_CASE.toSnakeCase("URL"), is("URL"));
		assertThat(CAMEL_CASE.toSnakeCase("URLconverter"), is("URLconverter"));
		assertThat(CAMEL_CASE.toSnakeCase("UrlConverter"), is("Url_converter"));
		assertThat(CAMEL_CASE.toSnakeCase("oldURLconverter"), is("old_URLconverter"));
		assertThat(CAMEL_CASE.toSnakeCase("oldUrlConverter"), is("old_url_converter"));
		assertThat(CAMEL_CASE.toSnakeCase("oldURL"), is("old_URL"));
	}

	//PascalCase

	/**
	 * @see CompoundTokenization#PASCAL_CASE
	 * @see CompoundTokenization#split(CharSequence)
	 */
	@Test
	public void testPascalCaseSplit() {
		assertThat(PASCAL_CASE.split(""), is(empty()));
		assertThat(PASCAL_CASE.split("foobar"), is(asList("foobar")));
		assertThat(PASCAL_CASE.split("fooBar"), is(asList("foo", "bar")));
		assertThat(PASCAL_CASE.split("FooBar"), is(asList("foo", "bar")));
		assertThat(PASCAL_CASE.split("foo-bar"), is(asList("foo-bar")));
		assertThat(PASCAL_CASE.split("foo-Bar"), is(asList("foo-", "bar")));
		assertThat(PASCAL_CASE.split("foo_bar"), is(asList("foo_bar")));
		assertThat(PASCAL_CASE.split("foo_Bar"), is(asList("foo_", "bar")));
		assertThat(PASCAL_CASE.split("x"), is(asList("x")));
		assertThat(PASCAL_CASE.split("X"), is(asList("x")));
		assertThat(PASCAL_CASE.split("CDlibrary"), is(asList("CDlibrary")));
		assertThat(PASCAL_CASE.split("CdLibrary"), is(asList("cd", "library")));
		assertThat(PASCAL_CASE.split("userCDlibrary"), is(asList("user", "CDlibrary")));
		assertThat(PASCAL_CASE.split("userCdLibrary"), is(asList("user", "cd", "library")));
		assertThat(PASCAL_CASE.split("URL"), is(asList("URL")));
		assertThat(PASCAL_CASE.split("URLconverter"), is(asList("URLconverter")));
		assertThat(PASCAL_CASE.split("UrlConverter"), is(asList("url", "converter")));
		assertThat(PASCAL_CASE.split("oldURLconverter"), is(asList("old", "URLconverter")));
		assertThat(PASCAL_CASE.split("oldUrlConverter"), is(asList("old", "url", "converter")));
		assertThat(PASCAL_CASE.split("oldURL"), is(asList("old", "URL")));
	}

	/**
	 * @see CompoundTokenization#PASCAL_CASE
	 * @see CompoundTokenization#join(Iterable)
	 */
	@Test
	public void testPascalCaseJoin() {
		assertThat(PASCAL_CASE.join(asList()), is(""));
		assertThat(PASCAL_CASE.join(asList("foobar")), is("Foobar"));
		assertThat(PASCAL_CASE.join(asList("foo", "bar")), is("FooBar"));
		assertThat(PASCAL_CASE.join(asList("Foo", "bar")), is("FooBar"));
		assertThat(PASCAL_CASE.join(asList("foo-bar")), is("Foo-bar"));
		assertThat(PASCAL_CASE.join(asList("foo-", "bar")), is("Foo-Bar"));
		assertThat(PASCAL_CASE.join(asList("foo_bar")), is("Foo_bar"));
		assertThat(PASCAL_CASE.join(asList("foo_", "bar")), is("Foo_Bar"));
		assertThat(PASCAL_CASE.join(asList("x")), is("X"));
		assertThat(PASCAL_CASE.join(asList("X")), is("X"));
		assertThat(PASCAL_CASE.join(asList("CDlibrary")), is("CDlibrary"));
		assertThat(PASCAL_CASE.join(asList("cd", "library")), is("CdLibrary"));
		assertThat(PASCAL_CASE.join(asList("user", "CDlibrary")), is("UserCDlibrary"));
		assertThat(PASCAL_CASE.join(asList("user", "cd", "library")), is("UserCdLibrary"));
		assertThat(PASCAL_CASE.join(asList("URL")), is("URL"));
		assertThat(PASCAL_CASE.join(asList("URLconverter")), is("URLconverter"));
		assertThat(PASCAL_CASE.join(asList("url", "converter")), is("UrlConverter"));
		assertThat(PASCAL_CASE.join(asList("old", "URLconverter")), is("OldURLconverter"));
		assertThat(PASCAL_CASE.join(asList("old", "url", "converter")), is("OldUrlConverter"));
		assertThat(PASCAL_CASE.join(asList("old", "URL")), is("OldURL"));
	}

	/**
	 * @see CompoundTokenization#PASCAL_CASE
	 * @see CompoundTokenization#toKebabCase(CharSequence)
	 */
	@Test
	public void testPascalCaseToKebabCase() {
		assertThat(PASCAL_CASE.toKebabCase(""), is(""));
		assertThat(PASCAL_CASE.toKebabCase("foobar"), is("foobar"));
		assertThat(PASCAL_CASE.toKebabCase("fooBar"), is("foo-bar"));
		assertThat(PASCAL_CASE.toKebabCase("FooBar"), is("foo-bar"));
		assertThrows(IllegalArgumentException.class, () -> PASCAL_CASE.toKebabCase("foo-bar"));
		assertThrows(IllegalArgumentException.class, () -> PASCAL_CASE.toKebabCase("foo-Bar"));
		assertThat(PASCAL_CASE.toKebabCase("foo_bar"), is("foo_bar"));
		assertThat(PASCAL_CASE.toKebabCase("foo_Bar"), is("foo_-bar"));
		assertThat(PASCAL_CASE.toKebabCase("x"), is("x"));
		assertThat(PASCAL_CASE.toKebabCase("X"), is("x"));
		assertThat(PASCAL_CASE.toKebabCase("CDlibrary"), is("CDlibrary"));
		assertThat(PASCAL_CASE.toKebabCase("CdLibrary"), is("cd-library"));
		assertThat(PASCAL_CASE.toKebabCase("userCDlibrary"), is("user-CDlibrary"));
		assertThat(PASCAL_CASE.toKebabCase("userCdLibrary"), is("user-cd-library"));
		assertThat(PASCAL_CASE.toKebabCase("URL"), is("URL"));
		assertThat(PASCAL_CASE.toKebabCase("URLconverter"), is("URLconverter"));
		assertThat(PASCAL_CASE.toKebabCase("UrlConverter"), is("url-converter"));
		assertThat(PASCAL_CASE.toKebabCase("oldURLconverter"), is("old-URLconverter"));
		assertThat(PASCAL_CASE.toKebabCase("oldUrlConverter"), is("old-url-converter"));
		assertThat(PASCAL_CASE.toKebabCase("oldURL"), is("old-URL"));
	}

	/**
	 * @see CompoundTokenization#PASCAL_CASE
	 * @see CompoundTokenization#toSnakeCase(CharSequence)
	 */
	@Test
	public void testPascalCaseToSnakeCase() {
		assertThat(PASCAL_CASE.toSnakeCase(""), is(""));
		assertThat(PASCAL_CASE.toSnakeCase("foobar"), is("foobar"));
		assertThat(PASCAL_CASE.toSnakeCase("fooBar"), is("foo_bar"));
		assertThat(PASCAL_CASE.toSnakeCase("FooBar"), is("foo_bar"));
		assertThat(PASCAL_CASE.toSnakeCase("foo-bar"), is("foo-bar"));
		assertThat(PASCAL_CASE.toSnakeCase("foo-Bar"), is("foo-_bar"));
		assertThrows(IllegalArgumentException.class, () -> PASCAL_CASE.toSnakeCase("foo_bar"));
		assertThrows(IllegalArgumentException.class, () -> PASCAL_CASE.toSnakeCase("foo_Bar"));
		assertThat(PASCAL_CASE.toSnakeCase("x"), is("x"));
		assertThat(PASCAL_CASE.toSnakeCase("X"), is("x"));
		assertThat(PASCAL_CASE.toSnakeCase("CDlibrary"), is("CDlibrary"));
		assertThat(PASCAL_CASE.toSnakeCase("CdLibrary"), is("cd_library"));
		assertThat(PASCAL_CASE.toSnakeCase("userCDlibrary"), is("user_CDlibrary"));
		assertThat(PASCAL_CASE.toSnakeCase("userCdLibrary"), is("user_cd_library"));
		assertThat(PASCAL_CASE.toSnakeCase("URL"), is("URL"));
		assertThat(PASCAL_CASE.toSnakeCase("URLconverter"), is("URLconverter"));
		assertThat(PASCAL_CASE.toSnakeCase("UrlConverter"), is("url_converter"));
		assertThat(PASCAL_CASE.toSnakeCase("oldURLconverter"), is("old_URLconverter"));
		assertThat(PASCAL_CASE.toSnakeCase("oldUrlConverter"), is("old_url_converter"));
		assertThat(PASCAL_CASE.toSnakeCase("oldURL"), is("old_URL"));
	}

	//kebab-case

	/**
	 * @see CompoundTokenization#KEBAB_CASE
	 * @see CompoundTokenization#split(CharSequence)
	 */
	@Test
	public void testKebabCaseSplit() {
		assertThat(KEBAB_CASE.split(""), is(empty()));
		assertThat(KEBAB_CASE.split("foobar"), is(asList("foobar")));
		assertThat(KEBAB_CASE.split("fooBar"), is(asList("fooBar")));
		assertThat(KEBAB_CASE.split("FooBar"), is(asList("FooBar")));
		assertThat(KEBAB_CASE.split("foo-bar"), is(asList("foo", "bar")));
		assertThat(KEBAB_CASE.split("foo-Bar"), is(asList("foo", "Bar")));
		assertThat(KEBAB_CASE.split("foo_bar"), is(asList("foo_bar")));
		assertThat(KEBAB_CASE.split("foo_Bar"), is(asList("foo_Bar")));
		assertThat(KEBAB_CASE.split("x"), is(asList("x")));
		assertThat(KEBAB_CASE.split("X"), is(asList("X")));
	}

	/**
	 * @see CompoundTokenization#KEBAB_CASE
	 * @see CompoundTokenization#join(Iterable)
	 */
	@Test
	public void testKebabCaseJoin() {
		assertThat(KEBAB_CASE.join(asList()), is(""));
		assertThat(KEBAB_CASE.join(asList("foobar")), is("foobar"));
		assertThat(KEBAB_CASE.join(asList("foo", "bar")), is("foo-bar"));
		assertThat(KEBAB_CASE.join(asList("Foo", "bar")), is("Foo-bar"));
		assertThrows(IllegalArgumentException.class, () -> KEBAB_CASE.join(asList("foo-bar")));
		assertThrows(IllegalArgumentException.class, () -> KEBAB_CASE.join(asList("foo-", "bar")));
		assertThat(KEBAB_CASE.join(asList("foo_bar")), is("foo_bar"));
		assertThat(KEBAB_CASE.join(asList("foo_", "bar")), is("foo_-bar"));
		assertThat(KEBAB_CASE.join(asList("x")), is("x"));
		assertThat(KEBAB_CASE.join(asList("X")), is("X"));
	}

	/**
	 * @see CompoundTokenization#KEBAB_CASE
	 * @see CompoundTokenization#toCamelCase(CharSequence)
	 */
	@Test
	public void testKebabCaseToCamelCase() {
		assertThat(KEBAB_CASE.toCamelCase("foobar"), is("foobar"));
		assertThat(KEBAB_CASE.toCamelCase("fooBar"), is("fooBar"));
		assertThat(KEBAB_CASE.toCamelCase("FooBar"), is("FooBar"));
		assertThat(KEBAB_CASE.toCamelCase("foo-bar"), is("fooBar"));
		assertThat(KEBAB_CASE.toCamelCase("foo-Bar"), is("fooBar"));
		assertThat(KEBAB_CASE.toCamelCase("foo_bar"), is("foo_bar"));
		assertThat(KEBAB_CASE.toCamelCase("foo_Bar"), is("foo_Bar"));
		assertThat(KEBAB_CASE.toCamelCase("x"), is("x"));
		assertThat(KEBAB_CASE.toCamelCase("X"), is("X"));
		assertThat(KEBAB_CASE.toCamelCase("CD-library"), is("CDLibrary"));
		assertThat(KEBAB_CASE.toCamelCase("user-CD-library"), is("userCDLibrary"));
		assertThat(KEBAB_CASE.toCamelCase("URL"), is("URL"));
		assertThat(KEBAB_CASE.toCamelCase("URL-converter"), is("URLConverter"));
		assertThat(KEBAB_CASE.toCamelCase("old-URL-converter"), is("oldURLConverter"));
		assertThat(KEBAB_CASE.toCamelCase("old-URL"), is("oldURL"));
	}

	/**
	 * @see CompoundTokenization#KEBAB_CASE
	 * @see CompoundTokenization#toPascalCase(CharSequence)
	 */
	@Test
	public void testKebabCaseToPascalCase() {
		assertThat(KEBAB_CASE.toPascalCase("foobar"), is("Foobar"));
		assertThat(KEBAB_CASE.toPascalCase("fooBar"), is("FooBar"));
		assertThat(KEBAB_CASE.toPascalCase("FooBar"), is("FooBar"));
		assertThat(KEBAB_CASE.toPascalCase("foo-bar"), is("FooBar"));
		assertThat(KEBAB_CASE.toPascalCase("foo-Bar"), is("FooBar"));
		assertThat(KEBAB_CASE.toPascalCase("foo_bar"), is("Foo_bar"));
		assertThat(KEBAB_CASE.toPascalCase("foo_Bar"), is("Foo_Bar"));
		assertThat(KEBAB_CASE.toPascalCase("x"), is("X"));
		assertThat(KEBAB_CASE.toPascalCase("X"), is("X"));
		assertThat(KEBAB_CASE.toPascalCase("CD-library"), is("CDLibrary"));
		assertThat(KEBAB_CASE.toPascalCase("user-CD-library"), is("UserCDLibrary"));
		assertThat(KEBAB_CASE.toPascalCase("URL"), is("URL"));
		assertThat(KEBAB_CASE.toPascalCase("URL-converter"), is("URLConverter"));
		assertThat(KEBAB_CASE.toPascalCase("old-URL-converter"), is("OldURLConverter"));
		assertThat(KEBAB_CASE.toPascalCase("old-URL"), is("OldURL"));
	}

	/**
	 * @see CompoundTokenization#KEBAB_CASE
	 * @see CompoundTokenization#toKebabCase(CharSequence)
	 */
	@Test
	public void testKebabCaseToKebabCase() {
		assertThat(KEBAB_CASE.toKebabCase("foobar"), is("foobar"));
		assertThat(KEBAB_CASE.toKebabCase("fooBar"), is("fooBar"));
		assertThat(KEBAB_CASE.toKebabCase("FooBar"), is("FooBar"));
		assertThat(KEBAB_CASE.toKebabCase("foo-bar"), is("foo-bar"));
		assertThat(KEBAB_CASE.toKebabCase("foo-Bar"), is("foo-Bar"));
		assertThat(KEBAB_CASE.toKebabCase("foo_bar"), is("foo_bar"));
		assertThat(KEBAB_CASE.toKebabCase("foo_Bar"), is("foo_Bar"));
		assertThat(KEBAB_CASE.toKebabCase("x"), is("x"));
		assertThat(KEBAB_CASE.toKebabCase("X"), is("X"));
		assertThat(KEBAB_CASE.toKebabCase("CD-library"), is("CD-library"));
		assertThat(KEBAB_CASE.toKebabCase("user-CD-library"), is("user-CD-library"));
		assertThat(KEBAB_CASE.toKebabCase("URL"), is("URL"));
		assertThat(KEBAB_CASE.toKebabCase("URL-converter"), is("URL-converter"));
		assertThat(KEBAB_CASE.toKebabCase("old-URL-converter"), is("old-URL-converter"));
		assertThat(KEBAB_CASE.toKebabCase("old-URL"), is("old-URL"));
	}

	/**
	 * @see CompoundTokenization#KEBAB_CASE
	 * @see CompoundTokenization#toSnakeCase(CharSequence)
	 */
	@Test
	public void testKebabCaseToSnakeCase() {
		assertThat(KEBAB_CASE.toSnakeCase("foobar"), is("foobar"));
		assertThat(KEBAB_CASE.toSnakeCase("fooBar"), is("fooBar"));
		assertThat(KEBAB_CASE.toSnakeCase("FooBar"), is("FooBar"));
		assertThat(KEBAB_CASE.toSnakeCase("foo-bar"), is("foo_bar"));
		assertThat(KEBAB_CASE.toSnakeCase("foo-Bar"), is("foo_Bar"));
		assertThat(KEBAB_CASE.toSnakeCase("foo_bar"), is("foo_bar"));
		assertThat(KEBAB_CASE.toSnakeCase("foo_Bar"), is("foo_Bar"));
		assertThat(KEBAB_CASE.toSnakeCase("x"), is("x"));
		assertThat(KEBAB_CASE.toSnakeCase("X"), is("X"));
		assertThat(KEBAB_CASE.toSnakeCase("CD-library"), is("CD_library"));
		assertThat(KEBAB_CASE.toSnakeCase("user-CD-library"), is("user_CD_library"));
		assertThat(KEBAB_CASE.toSnakeCase("URL"), is("URL"));
		assertThat(KEBAB_CASE.toSnakeCase("URL-converter"), is("URL_converter"));
		assertThat(KEBAB_CASE.toSnakeCase("old-URL-converter"), is("old_URL_converter"));
		assertThat(KEBAB_CASE.toSnakeCase("old-URL"), is("old_URL"));
	}

	//snake-case

	/**
	 * @see CompoundTokenization#SNAKE_CASE
	 * @see CompoundTokenization#split(CharSequence)
	 */
	@Test
	public void testSnakeCaseSplit() {
		assertThat(SNAKE_CASE.split(""), is(empty()));
		assertThat(SNAKE_CASE.split("foobar"), is(asList("foobar")));
		assertThat(SNAKE_CASE.split("fooBar"), is(asList("fooBar")));
		assertThat(SNAKE_CASE.split("FooBar"), is(asList("FooBar")));
		assertThat(SNAKE_CASE.split("foo-bar"), is(asList("foo-bar")));
		assertThat(SNAKE_CASE.split("foo-Bar"), is(asList("foo-Bar")));
		assertThat(SNAKE_CASE.split("foo_bar"), is(asList("foo", "bar")));
		assertThat(SNAKE_CASE.split("foo_Bar"), is(asList("foo", "Bar")));
		assertThat(SNAKE_CASE.split("x"), is(asList("x")));
		assertThat(SNAKE_CASE.split("X"), is(asList("X")));
	}

	/**
	 * @see CompoundTokenization#SNAKE_CASE
	 * @see CompoundTokenization#join(Iterable)
	 */
	@Test
	public void testSnakeCaseJoin() {
		assertThat(SNAKE_CASE.join(asList()), is(""));
		assertThat(SNAKE_CASE.join(asList("foobar")), is("foobar"));
		assertThat(SNAKE_CASE.join(asList("foo", "bar")), is("foo_bar"));
		assertThat(SNAKE_CASE.join(asList("Foo", "bar")), is("Foo_bar"));
		assertThat(SNAKE_CASE.join(asList("foo-bar")), is("foo-bar"));
		assertThat(SNAKE_CASE.join(asList("foo-", "bar")), is("foo-_bar"));
		assertThrows(IllegalArgumentException.class, () -> SNAKE_CASE.join(asList("foo_bar")));
		assertThrows(IllegalArgumentException.class, () -> SNAKE_CASE.join(asList("foo_", "bar")));
		assertThat(SNAKE_CASE.join(asList("x")), is("x"));
		assertThat(SNAKE_CASE.join(asList("X")), is("X"));
	}

	/**
	 * @see CompoundTokenization#SNAKE_CASE
	 * @see CompoundTokenization#toCamelCase(CharSequence)
	 */
	@Test
	public void testSnakeCaseToCamelCase() {
		assertThat(SNAKE_CASE.toCamelCase("foobar"), is("foobar"));
		assertThat(SNAKE_CASE.toCamelCase("fooBar"), is("fooBar"));
		assertThat(SNAKE_CASE.toCamelCase("FooBar"), is("FooBar"));
		assertThat(SNAKE_CASE.toCamelCase("foo-bar"), is("foo-bar"));
		assertThat(SNAKE_CASE.toCamelCase("foo-Bar"), is("foo-Bar"));
		assertThat(SNAKE_CASE.toCamelCase("foo_bar"), is("fooBar"));
		assertThat(SNAKE_CASE.toCamelCase("foo_Bar"), is("fooBar"));
		assertThat(SNAKE_CASE.toCamelCase("x"), is("x"));
		assertThat(SNAKE_CASE.toCamelCase("X"), is("X"));
		assertThat(SNAKE_CASE.toCamelCase("CD_library"), is("CDLibrary"));
		assertThat(SNAKE_CASE.toCamelCase("user_CD_library"), is("userCDLibrary"));
		assertThat(SNAKE_CASE.toCamelCase("URL"), is("URL"));
		assertThat(SNAKE_CASE.toCamelCase("URL_converter"), is("URLConverter"));
		assertThat(SNAKE_CASE.toCamelCase("old_URL_converter"), is("oldURLConverter"));
		assertThat(SNAKE_CASE.toCamelCase("old_URL"), is("oldURL"));
	}

	/**
	 * @see CompoundTokenization#SNAKE_CASE
	 * @see CompoundTokenization#toPascalCase(CharSequence)
	 */
	@Test
	public void testSnakeCaseToPascalCase() {
		assertThat(SNAKE_CASE.toPascalCase("foobar"), is("Foobar"));
		assertThat(SNAKE_CASE.toPascalCase("fooBar"), is("FooBar"));
		assertThat(SNAKE_CASE.toPascalCase("FooBar"), is("FooBar"));
		assertThat(SNAKE_CASE.toPascalCase("foo-bar"), is("Foo-bar"));
		assertThat(SNAKE_CASE.toPascalCase("foo-Bar"), is("Foo-Bar"));
		assertThat(SNAKE_CASE.toPascalCase("foo_bar"), is("FooBar"));
		assertThat(SNAKE_CASE.toPascalCase("foo_Bar"), is("FooBar"));
		assertThat(SNAKE_CASE.toPascalCase("x"), is("X"));
		assertThat(SNAKE_CASE.toPascalCase("X"), is("X"));
		assertThat(SNAKE_CASE.toPascalCase("CD_library"), is("CDLibrary"));
		assertThat(SNAKE_CASE.toPascalCase("user_CD_library"), is("UserCDLibrary"));
		assertThat(SNAKE_CASE.toPascalCase("URL"), is("URL"));
		assertThat(SNAKE_CASE.toPascalCase("URL_converter"), is("URLConverter"));
		assertThat(SNAKE_CASE.toPascalCase("old_URL_converter"), is("OldURLConverter"));
		assertThat(SNAKE_CASE.toPascalCase("old_URL"), is("OldURL"));
	}

	/**
	 * @see CompoundTokenization#SNAKE_CASE
	 * @see CompoundTokenization#toKebabCase(CharSequence)
	 */
	@Test
	public void testSnakeCaseToKebabCase() {
		assertThat(SNAKE_CASE.toKebabCase("foobar"), is("foobar"));
		assertThat(SNAKE_CASE.toKebabCase("fooBar"), is("fooBar"));
		assertThat(SNAKE_CASE.toKebabCase("FooBar"), is("FooBar"));
		assertThat(SNAKE_CASE.toKebabCase("foo-bar"), is("foo-bar"));
		assertThat(SNAKE_CASE.toKebabCase("foo-Bar"), is("foo-Bar"));
		assertThat(SNAKE_CASE.toKebabCase("foo_bar"), is("foo-bar"));
		assertThat(SNAKE_CASE.toKebabCase("foo_Bar"), is("foo-Bar"));
		assertThat(SNAKE_CASE.toKebabCase("x"), is("x"));
		assertThat(SNAKE_CASE.toKebabCase("X"), is("X"));
		assertThat(SNAKE_CASE.toKebabCase("CD_library"), is("CD-library"));
		assertThat(SNAKE_CASE.toKebabCase("user_CD_library"), is("user-CD-library"));
		assertThat(SNAKE_CASE.toKebabCase("URL"), is("URL"));
		assertThat(SNAKE_CASE.toKebabCase("URL_converter"), is("URL-converter"));
		assertThat(SNAKE_CASE.toKebabCase("old_URL_converter"), is("old-URL-converter"));
		assertThat(SNAKE_CASE.toKebabCase("old_URL"), is("old-URL"));
	}

	/**
	 * @see CompoundTokenization#SNAKE_CASE
	 * @see CompoundTokenization#toSnakeCase(CharSequence)
	 */
	@Test
	public void testSnakeCaseToSnakeCase() {
		assertThat(SNAKE_CASE.toSnakeCase("foobar"), is("foobar"));
		assertThat(SNAKE_CASE.toSnakeCase("fooBar"), is("fooBar"));
		assertThat(SNAKE_CASE.toSnakeCase("FooBar"), is("FooBar"));
		assertThat(SNAKE_CASE.toSnakeCase("foo-bar"), is("foo-bar"));
		assertThat(SNAKE_CASE.toSnakeCase("foo-Bar"), is("foo-Bar"));
		assertThat(SNAKE_CASE.toSnakeCase("foo_bar"), is("foo_bar"));
		assertThat(SNAKE_CASE.toSnakeCase("foo_Bar"), is("foo_Bar"));
		assertThat(SNAKE_CASE.toSnakeCase("x"), is("x"));
		assertThat(SNAKE_CASE.toSnakeCase("X"), is("X"));
		assertThat(SNAKE_CASE.toSnakeCase("CD_library"), is("CD_library"));
		assertThat(SNAKE_CASE.toSnakeCase("user_CD_library"), is("user_CD_library"));
		assertThat(SNAKE_CASE.toSnakeCase("URL"), is("URL"));
		assertThat(SNAKE_CASE.toSnakeCase("URL_converter"), is("URL_converter"));
		assertThat(SNAKE_CASE.toSnakeCase("old_URL_converter"), is("old_URL_converter"));
		assertThat(SNAKE_CASE.toSnakeCase("old_URL"), is("old_URL"));
	}

}
