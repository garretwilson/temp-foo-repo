/*
 * Copyright © 1996-2011 GlobalMentor, Inc. <https://www.globalmentor.com/>
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

package com.globalmentor.text;

import static com.globalmentor.java.CharSequences.*;
import static com.globalmentor.java.Conditions.*;
import static java.util.Objects.*;

import java.util.Optional;
import java.util.regex.*;

import javax.annotation.*;

import com.globalmentor.java.*;

/**
 * Constants and utility methods for regular expressions and related tasks.
 * @apiNote Many of these constants and utilities assist in forming regular expressions to use with {@link Pattern}.
 * @author Garret Wilson
 * @see Pattern
 */
public final class RegularExpression {

	private RegularExpression() {
	}

	/** The regular expression character that matches any character. */
	public static final char WILDCARD_CHAR = '.';

	/** The symbol for specifying zero or one repetitions. */
	public static final char ZERO_OR_ONE_CHAR = '?';

	/** The symbol for specifying zero or more repetitions. */
	public static final char ZERO_OR_MORE_CHAR = '*';

	/** The symbol for specifying one or more repetitions. */
	public static final char ONE_OR_MORE_CHAR = '+';

	/** The beginning character of a character class. */
	public static final char CHARACTER_CLASS_BEGIN = '[';

	/** The ending character of a character class. */
	public static final char CHARACTER_CLASS_END = ']';

	/** The character class negation character. */
	public static final char CHARACTER_CLASS_NEGATION_CHAR = '^';

	/** The character class range character. */
	public static final char CHARACTER_CLASS_RANGE_CHAR = '-';

	/** The character used for escaping regular expressions. */
	public static final char ESCAPE = '\\';

	/**
	 * The restricted characters that must be escaped in regular expressions at least in some places.
	 * @implNote This implementation is overly restrictive, including some closing grouping characters that may not strictly be necessary.
	 * @see <a href="https://stackoverflow.com/q/399078/421049">What special characters must be escaped in regular expressions?</a>
	 * @see <a href="https://www.regular-expressions.info/characters.html">regular-exprssions.info: Special Characters</a>
	 */
	public static final Characters RESTRICTED = Characters.of(ESCAPE, ZERO_OR_ONE_CHAR, ZERO_OR_MORE_CHAR, ONE_OR_MORE_CHAR, WILDCARD_CHAR,
			CHARACTER_CLASS_RANGE_CHAR, '(', ')', CHARACTER_CLASS_BEGIN, CHARACTER_CLASS_END, CHARACTER_CLASS_NEGATION_CHAR, '{', '}', '|');

	/**
	 * The restricted characters that must be escaped in regular expression character classes.
	 * @implNote The character class begin character {@value #CHARACTER_CLASS_BEGIN} must be escaped to prevent the creation of a nested character class.
	 * @see <a href="https://stackoverflow.com/q/5484084/421049">What literal characters should be escaped in a regex?</a>
	 * @see <a href="https://stackoverflow.com/a/19976308/421049">Does a dot have to be escaped in a character class (square brackets) of a regular
	 *      expression?</a>
	 * @see <a href="https://www.regular-expressions.info/refcharclass.html">Regular Expression Reference: Character Classes</a>
	 */
	public static final Characters CHARACTER_CLASS_RESTRICTED = Characters.of(ESCAPE, CHARACTER_CLASS_NEGATION_CHAR, CHARACTER_CLASS_RANGE_CHAR,
			CHARACTER_CLASS_BEGIN, CHARACTER_CLASS_END);

	//TODO add method for generating various quoted strings patterns rather than publicizing the quoted string constants below

	/** Regular expression requiring matching quotes and not recognizing any escaping. */
	static final String QUOTED_STRING = "\"[^\"]*\"";
	/** Regular expression requiring matching quotes, and allowing escaping of the quote using the backslash, as well as escaping the escape character itself. */
	static final String QUOTED_STRING_ALLOWING_ESCAPE_QUOTE = "\"(?:[^\\\\\"]++|\\\\\"|\\\\\\\\)*+\"";
	/** Regular expression requiring matching quotes, and allowing escaping of any character using the backslash. */
	static final String QUOTED_STRING_ALLOWING_ESCAPE_ANYTHING = "\"(?:[^\\\\\"]++|\\\\.)*+\"";

	/**
	 * Checks to make sure an argument matches a given regular expression pattern, returning the successful matcher.
	 * @apiNote This is a precondition check.
	 * @apiNote Unlike other precondition checks, this method returns a {@link Matcher} to allow subsequent checks on the input. If the original text is desired,
	 *          call {@link Matcher#group()} after a successful match, which will return group <code>0></code>, indicating the entire matched string.
	 * @param input The character sequence to be matched.
	 * @param pattern The regular expression pattern against which the input will be matched.
	 * @return A matcher that has successfully matched the given input.
	 * @throws IllegalArgumentException if the given input does not match the pattern.
	 */
	public static Matcher checkArgumentMatches(final CharSequence input, @Nonnull final Pattern pattern) {
		return checkArgumentMatches(input, pattern, "Input `%s` did not match pattern `%s`.", input, pattern);
	}

	/**
	 * Checks to make sure an argument matches a given regular expression pattern, returning the successful matcher.
	 * @apiNote This is a precondition check.
	 * @apiNote Unlike other precondition checks, this method returns a {@link Matcher} to allow subsequent checks on the input. If the original text is desired,
	 *          call {@link Matcher#group()} after a successful match, which will return group <code>0></code>, indicating the entire matched string.
	 * @param input The character sequence to be matched.
	 * @param pattern The regular expression pattern against which the input will be matched.
	 * @param description A description of the test to be used when generating an exception, optionally formatted with arguments, or <code>null</code> for no
	 *          description.
	 * @param arguments The arguments to be applied when formatting, or an empty array if the message should not be formatted.
	 * @return A matcher that has successfully matched the given input.
	 * @throws IllegalArgumentException if the given input does not match the pattern.
	 * @throws NullPointerException if the given arguments is <code>null</code>.
	 * @throws IllegalArgumentException if the description is an invalid pattern, or if an argument in the arguments array is not of the type expected by the
	 *           format element(s) that use it.
	 * @see String#format(String, Object...)
	 */
	public static Matcher checkArgumentMatches(final CharSequence input, @Nonnull final Pattern pattern, @Nullable String description,
			@Nonnull final Object... arguments) {
		final Matcher matcher = pattern.matcher(requireNonNull(input));
		checkArgument(matcher.matches(), description, arguments);
		return matcher;
	}

	/**
	 * Creates a regular expression character class (e.g. "[abc]") from the given characters (e.g. 'a', 'b', and 'c').
	 * @implSpec This implementation delegates to {@link #characterClassOf(Characters)}.
	 * @implNote The current implementation includes control characters literally rather than escaping them.
	 * @param characters The characters to be included in the character class.
	 * @return The new character class including the given characters.
	 * @throws NullPointerException if the given characters is <code>null</code>.
	 * @see #escapePatternString(String)
	 */
	public static String characterClassOf(final char... characters) {
		return characterClassOf(Characters.of(characters));
	}

	/**
	 * Creates a regular expression negative character class (e.g. "[^abc]") from the given characters (e.g. 'a', 'b', and 'c').
	 * @implSpec This implementation delegates to {@link #characterClassNotOf(Characters)}.
	 * @implNote The current implementation includes control characters literally rather than escaping them.
	 * @param characters The characters to be included in the character class.
	 * @return The new character class including the given characters.
	 * @throws NullPointerException if the given characters is <code>null</code>.
	 * @see #escapePatternString(String)
	 */
	public static String characterClassNotOf(final char... characters) {
		return characterClassNotOf(Characters.of(characters));
	}

	/**
	 * Creates a regular expression character class (e.g. "[abc]") from the given characters (e.g. 'a', 'b', and 'c').
	 * @implNote The current implementation includes control characters literally rather than escaping them.
	 * @param characters The characters to be included in the character class.
	 * @return The new character class including the given characters.
	 * @throws NullPointerException if the given characters is <code>null</code>.
	 */
	public static String characterClassOf(final Characters characters) {
		return createCharacterClass(characters, false);
	}

	/**
	 * Creates a regular expression negative character class (e.g. "[^abc]") from the given characters (e.g. 'a', 'b', and 'c').
	 * @implNote The current implementation includes control characters literally rather than escaping them.
	 * @param characters The characters to be included in the character class.
	 * @return The new character class including the given characters.
	 * @throws NullPointerException if the given characters is <code>null</code>.
	 */
	public static String characterClassNotOf(final Characters characters) {
		return createCharacterClass(characters, true);
	}

	/**
	 * Creates a regular expression character class (e.g. "[abc]") from the given characters (e.g. 'a', 'b', and 'c'), optionally with negation.
	 * @implNote The current implementation includes control characters literally rather than escaping them.
	 * @param characters The characters to be included in the character class.
	 * @return The new character class including the given characters.
	 * @throws NullPointerException if the given characters is <code>null</code>.
	 * @see #CHARACTER_CLASS_RESTRICTED
	 * @see #ESCAPE
	 */
	private static String createCharacterClass(final Characters characters, final boolean not) {
		final StringBuilder stringBuilder = new StringBuilder().append(CHARACTER_CLASS_BEGIN);
		if(not) {
			stringBuilder.append(CHARACTER_CLASS_NEGATION_CHAR); //^
		}
		return stringBuilder.append(escape(characters.toString(), CHARACTER_CLASS_RESTRICTED, ESCAPE)).append(CHARACTER_CLASS_END).toString();
	}

	/**
	 * Escapes restricted characters meant to appear in a pattern.
	 * @param patternString The string to appear in a pattern.
	 * @return The pattern string with restricted characters escaped.
	 * @see #RESTRICTED
	 * @see #ESCAPE
	 */
	public static String escapePatternString(final String patternString) {
		return escape(patternString, RESTRICTED, ESCAPE); //escape the string
	}

	/**
	 * Matches an input against a pattern and, if the input matchers, returns the matcher.
	 * @apiNote This is a convenience method to call {@link Pattern#matcher(CharSequence)} and {@link Matcher#matches()}.
	 * @param pattern The pattern against which the character sequence will be matched.
	 * @param input The character sequence to be matched.
	 * @return A new matcher for the matched pattern, which will not be present if the pattern did not match.
	 */
	public static Optional<Matcher> findMatch(@Nonnull final Pattern pattern, final CharSequence input) {
		final Matcher matcher = pattern.matcher(input);
		return matcher.matches() ? Optional.of(matcher) : Optional.empty();
	}

	/**
	 * A utility that encapsulates a set of capturing groups for a regular expression. Each instance is a group that can be matched.
	 * @apiNote The following is the envisioned use. Note that this pattern names the instance <code>XXXRegEx</code> to encapsulate all regular expression
	 *          information (even though technically it is a {@link NumberedCapturingGroup}), and names each group instance with a <code>_GROUP</code> suffix for
	 *          clarity. (A {@link NamedCapturingGroup} may wish to leave off this suffix if more concise matching group names are desired.) : <pre>{@code
	 *
	 * enum FooBarRegEx implements RegularExpressions.NumberedCapturingGroup {
	 *   FOO_GROUP, BAR_GROUP;
	 *
	 *   public static final Pattern PATTERN = Pattern.compile("(foo)(bar)?");
	 * }
	 * …
	 * Matcher matcher = FooBarRegEx.PATTERN.matcher("foo");
	 * assert matcher.matches();
	 * Optional<String> foundFoo = FooBarRegEx.FOO.findIn(matcher); //`Optional.of("foo")`
	 * Optional<String> foundBar = FooBarRegEx.BAR.findIn(matcher); //`Optional.empty()`
	 * }</pre>
	 * @author Garret Wilson
	 */
	public interface CapturingGroup extends EnumLike {

		/**
		 * Finds the input subsequence captured by this group in the given matcher. The way this group identifies the captured group in the match depends on the
		 * implementation of this class.
		 * @param matcher The matcher to check.
		 * @return The subsequence captured by this group in the match, which will be empty if this group did not capture part of the input.
		 * @throws IllegalStateException If no match has yet been attempted, or if the previous match operation failed.
		 */
		public Optional<String> findIn(@Nonnull final Matcher matcher);

	}

	/** A regular expression group definition that finds captured groups based upon the group's order in the pattern. */
	public interface NumberedCapturingGroup extends CapturingGroup {

		/**
		 * {@inheritDoc}
		 * @implSpec This implementation finds the captured group using the one-based equivalent of this group's ordinal.
		 * @see #ordinal()
		 * @see Matcher#group(int)
		 * @throws IndexOutOfBoundsException If there is no capturing group in the pattern with the one-based equivalent of this group's ordinal.
		 */
		@Override
		default Optional<String> findIn(@Nonnull final Matcher matcher) {
			return Optional.ofNullable(matcher.group(ordinal() + 1));
		}

	}

	/** A regular expression group definition that finds captured groups based upon the group's name in the pattern. */
	public interface NamedCapturingGroup extends CapturingGroup {

		/**
		 * {@inheritDoc}
		 * @implSpec This implementation finds the captured group using the this group's name.
		 * @see #name()
		 * @see Matcher#group(String)
		 * @throws IndexOutOfBoundsException If there is no capturing group in the pattern with the this group's name.
		 */
		@Override
		default Optional<String> findIn(@Nonnull final Matcher matcher) {
			return Optional.ofNullable(matcher.group(ordinal() + 1));
		}

	}

}
