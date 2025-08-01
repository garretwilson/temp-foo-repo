/*
 * Copyright © 1996-2012 GlobalMentor, Inc. <https://www.globalmentor.com/>
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

import static com.globalmentor.collections.Lists.*;
import static com.globalmentor.java.Characters.*;
import static com.globalmentor.java.Conditions.*;
import static java.lang.Math.*;
import static java.lang.String.*;
import static java.nio.charset.StandardCharsets.*;
import static java.util.Objects.*;
import static java.util.stream.Collectors.*;

import java.nio.*;
import java.nio.charset.CharsetDecoder;
import java.text.Normalizer;
import java.util.*;
import java.util.function.*;

import javax.annotation.*;

import com.globalmentor.io.UTF8;
import com.globalmentor.text.Case;

/**
 * Various text manipulating functions. These methods work on objects that implement the {@link CharSequence} interface. To avoid creation of new strings, some
 * of these methods should be avoided in favor of their corresponding {@link StringBuilders} methods, which operate on {@link StringBuilder} objects.
 * @see StringBuilders
 * @author Garret Wilson
 */
public final class CharSequences {

	private CharSequences() {
	}

	/**
	 * Checks the given bounds of a character sequence.
	 * @param charSequence The character sequence against which the bounds should be checked.
	 * @param start The start to check, inclusive.
	 * @param end The end to check, exclusive.
	 * @return The given character sequence.
	 * @throws StringIndexOutOfBoundsException if <code>start</code> or <code>end</code> is negative or greater than <code>length()</code>, or <code>start</code>
	 *           is greater than <code>end</code>.
	 */
	public static CharSequence checkBounds(final CharSequence charSequence, final int start, final int end) {
		if(start < 0) {
			throw new StringIndexOutOfBoundsException(start);
		}
		if(end > charSequence.length()) {
			throw new StringIndexOutOfBoundsException(end);
		}
		if(start > end) {
			throw new StringIndexOutOfBoundsException(end - start);
		}
		return charSequence;
	}

	/**
	 * Ensures that the given character sequence has a minimum the specified number of characters.
	 * @param <T> The type of character sequence being used.
	 * @param charSequence The character sequence to check.
	 * @param minLength The minimum length required.
	 * @return The given character sequence.
	 * @throws NullPointerException if the given character sequence is <code>null</code>.
	 * @throws IllegalArgumentException if the length of the given character sequence is less than the indicated minimum length.
	 */
	public static <T extends CharSequence> T checkMinLength(final T charSequence, final int minLength) {
		if(charSequence.length() < minLength) { //if the length of the given characters sequence is less than required
			throw new IllegalArgumentException("Character sequence is not at least " + minLength + " characters long: " + charSequence);
		}
		return charSequence; //return the character sequence
	}

	/**
	 * Strategy for constraining that truncates at the start of a character sequence.
	 * @see #constrain(CharSequence, int, ToIntBiFunction, CharSequence)
	 */
	public static final ToIntBiFunction<CharSequence, CharSequence> CONSTRAIN_TRUNCATE_START = (characterSquence, __) -> 0;

	/**
	 * Strategy for constraining that truncates in the middle of a character sequence.
	 * @see #constrain(CharSequence, int, ToIntBiFunction, CharSequence)
	 */
	public static final ToIntBiFunction<CharSequence, CharSequence> CONSTRAIN_TRUNCATE_MIDDLE = (characterSequence,
			omissionSequence) -> (characterSequence.length() - omissionSequence.length() + 1) / 2;

	/**
	 * Strategy for constraining that truncates at the end of a character sequence.
	 * @see #constrain(CharSequence, int, ToIntBiFunction, CharSequence)
	 */
	public static final ToIntBiFunction<CharSequence, CharSequence> CONSTRAIN_TRUNCATE_END = (characterSequence, __) -> characterSequence.length() - 1;

	/**
	 * Ensures a sequence of characters is not longer than the given maximum, by truncating if necessary and replacing with an omission sequence.
	 * @apiNote Normally one of the existing truncate index strategies should be used.
	 * @implNote The returned character sequence is meant to be used and discarded; it may be mutable and may retain references to larger buffers. If the
	 *           character sequence is to be referenced for a longer time, it should be converted to a string using {@link CharSequence#toString()}.
	 * @param charSequence The character sequence to constrain.
	 * @param maxLength The maximum length to constrain; must not be negative.
	 * @param truncateIndexStrategy The strategy for determining the initial index to truncate, given a non-empty character sequence and a non-<code>null</code>
	 *          omission sequence which may be empty. The omission sequence is guaranteed to be less than the maximum length. The strategy may provide any valid
	 *          index within the original character sequence; this method will make the any further adjustments as necessary to constrain the string.
	 * @param omissionSequence The sequence (e.g. an ellipsis or three dots), which may be empty, to be inserted in place of any truncated characters. If the
	 *          omission sequence is longer than the maximum length, the omission sequence itself will be truncated arbitrarily.
	 * @return The label constrained to a certain length.
	 * @throws IllegalArgumentException if the maximum length is negative.
	 * @throws IndexOutOfBoundsException if the truncate index strategy returns an index not within the range of the original character sequence (end exclusive).
	 * @see #CONSTRAIN_TRUNCATE_START
	 * @see #CONSTRAIN_TRUNCATE_MIDDLE
	 * @see #CONSTRAIN_TRUNCATE_END
	 */
	public static CharSequence constrain(@Nonnull final CharSequence charSequence, @Nonnegative final int maxLength,
			@Nonnull final ToIntBiFunction<CharSequence, CharSequence> truncateIndexStrategy, @Nonnull final CharSequence omissionSequence) {
		requireNonNull(charSequence);
		checkArgumentNotNegative(maxLength);
		requireNonNull(truncateIndexStrategy);
		requireNonNull(omissionSequence);
		if(maxLength == 0) {
			return ""; //the only constrained string of zero length
		}
		final int length = charSequence.length();
		if(length <= maxLength) {
			return charSequence; //nothing to do; string already constrained
		}
		final int omissionSequenceLength = omissionSequence.length();
		if(omissionSequenceLength >= maxLength) { //if there is no way to retain any of the original string
			return omissionSequence.subSequence(0, maxLength);
		}
		assert omissionSequenceLength < maxLength;
		final int truncateIndex = min(checkIndexBounds(truncateIndexStrategy.applyAsInt(charSequence, omissionSequence), length),
				maxLength - omissionSequenceLength); //whatever truncate index is given, make sure it allows room for the omission sequence after it
		final int truncateLength = length - maxLength + omissionSequenceLength; //truncate enough to make the string the correct length, allowing for the omission sequence
		assert maxLength + truncateLength == length + omissionSequenceLength; //if we add the current length and the omission length together, truncating should get is right back to the max length exactly
		assert truncateIndex + omissionSequenceLength + (length - (truncateIndex + truncateLength)) == maxLength; //the beginning, the omission sequence, and the end should combine to have the max length exactly
		return new StringBuilder().append(charSequence, 0, truncateIndex).append(omissionSequence).append(charSequence, truncateIndex + truncateLength, length);
	}

	/**
	 * Determines if a character sequence contains the given character.
	 * @param charSequence The character sequence to be searched.
	 * @param character The character to check.
	 * @return <code>true</code> if the given character sequence contains the given character.
	 */
	public static boolean contains(final CharSequence charSequence, final char character) {
		return indexOf(charSequence, character) >= 0; //see if the given character is in the character sequence
	}

	/**
	 * Determines if a character sequence does not contain the given character.
	 * @param charSequence The character sequence to be searched.
	 * @param character The character to check.
	 * @return <code>true</code> if the given character sequence does not contain the given character.
	 */
	public static boolean notContains(final CharSequence charSequence, final char character) {
		return indexOf(charSequence, character) < 0; //see if the given character is in the character sequence
	}

	/**
	 * Determines if a character sequence contains any of the given characters.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @return <code>true</code> if the given character sequence contains one of the given characters.
	 */
	public static boolean contains(final CharSequence charSequence, final Characters characters) {
		return indexOf(charSequence, characters) >= 0; //see if any of the given characters are in the character sequence
	}

	/**
	 * Determines if a character sequence does not contain any of the given characters.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @return <code>true</code> if the given character sequence does not contain one of the given characters.
	 */
	public static boolean notContains(final CharSequence charSequence, final Characters characters) {
		return indexOf(charSequence, characters) < 0; //see if any of the given characters are in the character sequence
	}

	/**
	 * Determines if a character sequence contains only the given characters.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @return <code>true</code> if the given character sequence contains only the given characters.
	 */
	public static boolean containsOnly(final CharSequence charSequence, final Characters characters) {
		return indexNotOf(charSequence, characters) < 0; //see if any of the given characters are not in the character sequence
	}

	/**
	 * Determines if a character sequence does not contain only the given characters.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @return <code>true</code> if the given character sequence does not contain only the given characters.
	 */
	public static boolean notContainsOnly(final CharSequence charSequence, final Characters characters) {
		return indexNotOf(charSequence, characters) >= 0; //see if any of the given characters are not in the character sequence
	}

	/**
	 * Determines if the following character sequence contains a letter.
	 * @param charSequence The character sequence to search.
	 * @return <code>true</code> if the sequence has at least one letter.
	 */
	public static boolean containsLetter(final CharSequence charSequence) { //TODO maybe change this to indexOfLetterOrDigit
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each character in the string
			if(Character.isLetter(charSequence.charAt(i))) //if this is a letter
				return true; //we found a letter
		}
		return false; //we found no letters
	}

	/**
	 * Determines if the following character sequence contains a letter or a digit.
	 * @param charSequence The character sequence to search.
	 * @return <code>true</code> if the sequence has at least one letter or digit.
	 * @see Character#isLetterOrDigit(char)
	 */
	public static boolean containsLetterOrDigit(final CharSequence charSequence) { //TODO maybe change this to indexOfLetterOrDigit
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each character in the string
			if(Character.isLetterOrDigit(charSequence.charAt(i))) //if this is a letter or digit
				return true; //we found a letter or digit
		}
		return false; //we found no letters or digits
	}

	/**
	 * Determines if a character sequence contains Unicode whitespace. Whitespace is denoted by the "WS" bidi class in <code>UnicodeData.txt</code>. This method
	 * does not handle Unicode supplementary characters.
	 * @param charSequence The character sequence to be searched.
	 * @return <code>true</code> if the given character sequence contains whitespace.
	 * @see Character#isSpaceChar(char)
	 */
	public static boolean containsWhitespace(final CharSequence charSequence) {
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each character in the string
			if(Character.isSpaceChar(charSequence.charAt(i))) { //if this is whitespace
				return true; //we found whitespace
			}
		}
		return false; //we found no whitespace
	}

	/**
	 * Determines if a character sequence contains characters that are not Unicode whitespace (marked by "WS" in Unicode data). Whitespace is denoted by the "WS"
	 * bidi class in <code>UnicodeData.txt</code>. This method does not handle Unicode supplementary characters.
	 * @param charSequence The character sequence to be searched.
	 * @return <code>true</code> if the given character sequence contains non-whitespace.
	 * @see Character#isSpaceChar(char)
	 */
	public static boolean containsNonWhitespace(final CharSequence charSequence) {
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each character in the string
			if(!Character.isSpaceChar(charSequence.charAt(i))) { //if this is not whitespace
				return true; //we found non-whitespace
			}
		}
		return false; //we found no non-whitespace
	}

	/**
	 * Searches the given character sequence for one of the given tokens, separated by the given delimiters.
	 * @param charSequence The character sequence to search.
	 * @param delimiters The delimiters to skip.
	 * @param tokens The tokens for which to check.
	 * @return The <code>true</code> if one of the given tokens was found.
	 * @throws NullPointerException if the given character sequence, delimiters, and/or tokens is <code>null</code>.
	 */
	public static boolean containsToken(final CharSequence charSequence, final Characters delimiters, final CharSequence... tokens) {
		return getToken(charSequence, delimiters, tokens) != null;
	}

	/**
	 * Searches the given character sequence for one of the given tokens, separated by the given delimiters.
	 * @param charSequence The character sequence to search.
	 * @param delimiters The delimiters to skip.
	 * @param tokens The tokens for which to check.
	 * @return The token that was found, or <code>null</code> if no token was found.
	 * @throws NullPointerException if the given character sequence, delimiters, and/or tokens is <code>null</code>.
	 */
	public static CharSequence getToken(final CharSequence charSequence, final Characters delimiters, final CharSequence... tokens) {
		final int length = charSequence.length();
		for(int i = 0; i < length; ++i) { //look through the sequence
			if(delimiters.contains(charSequence.charAt(i))) { //skip delimiters
				continue;
			}
			for(final CharSequence token : tokens) { //look at each token
				final int tokenEnd = min(i + token.length(), length); //find where the end of the token would be
				if(equals(token, charSequence, i, tokenEnd) && (tokenEnd == length || delimiters.contains(charSequence.charAt(tokenEnd)))) { //if this token equals the characters starting at the current position and ends with a delimiter or the end of the sequence
					return token;
				}
			}
		}
		return null;
	}

	/**
	 * Determines if a character sequence contains characters that can be trimmed. Trimmed characters are denoted by the "WS" (whitespace) bidi class, the "Cc"
	 * (control) category, or the "Cf" (format) category in <code>UnicodeData.txt</code>. This method does not handle Unicode supplementary characters.
	 * @param charSequence The character sequence to be searched.
	 * @return <code>true</code> if the given character sequence contains trim characters.
	 * @see Character#isSpaceChar(char)
	 * @see Character#isISOControl(char)
	 * @see Character#getType(char)
	 */
	public static boolean containsTrim(final CharSequence charSequence) {
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each character in the string
			final char c = charSequence.charAt(i); //get the current character
			if(Character.isSpaceChar(c) || Character.isISOControl(c) || Character.getType(c) == Character.FORMAT) { //if this is whitespace, control, or format
				return true; //we found a trim character
			}
		}
		return false; //we found no trim character
	}

	/**
	 * Determines if a character sequence contains characters that are not characters that can be trimmed. This is useful for determining if a characer sequence
	 * actually contains useful data. Trimmed characters are denoted by the "WS" (whitespace) bidi class, the "Cc" (control) category, or the "Cf" (format)
	 * category in <code>UnicodeData.txt</code>. This method does not handle Unicode supplementary characters.
	 * @param charSequence The character sequence to be searched.
	 * @return <code>true</code> if the given character sequence contains non-trim characters.
	 * @see Character#isSpaceChar(char)
	 * @see Character#isISOControl(char)
	 * @see Character#getType(char)
	 */
	public static boolean containsNonTrim(final CharSequence charSequence) {
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each character in the string
			final char c = charSequence.charAt(i); //get the current character
			if(!Character.isSpaceChar(c) && !Character.isISOControl(c) && Character.getType(c) != Character.FORMAT) { //if this is not whitespace, control, or format
				return true; //we found a non-trim character
			}
		}
		return false; //we found no non-trim character
	}

	/**
	 * Counts the number of occurrences of a particular character in a character sequence.
	 * @param charSequence The character sequence to examine.
	 * @param character The character to count.
	 * @return The number of occurrences of the character in the character sequence.
	 */
	public static int count(final CharSequence charSequence, final char character) {
		return count(charSequence, character, 0); //count, starting at the beginning
	}

	/**
	 * Counts the number of occurrences of a particular character in a character sequence, starting at a specified index and searching forward.
	 * @param charSequence The character sequence to examine.
	 * @param character The character to count.
	 * @param index The index to start counting at.
	 * @return The number of occurrences of the character in the character sequence.
	 */
	public static int count(final CharSequence charSequence, final char character, final int index) {
		final int length = charSequence.length();
		int count = 0; //start out without knowing any occurrences
		for(int i = index; i < length; ++i) { //look at each character
			if(charSequence.charAt(i) == character) { //if this character matches the given characters
				++count; //show that we found one more occurrence characters
			}
		}
		return count; //return the total count
	}

	/**
	 * Determines if the character sequence ends with the given character.
	 * @param charSequence The character sequence to examine.
	 * @param character The character to compare.
	 * @return <code>true</code> if the last character of the character sequence matches the given character.
	 */
	public static boolean endsWith(final CharSequence charSequence, final char character) {
		//see if the character sequence has at least one character, and the last character matches our character
		final int length = charSequence.length();
		return length > 0 && charSequence.charAt(length - 1) == character;
	}

	/**
	 * Determines if the character sequence ends with one of the given characters.
	 * @param charSequence The character sequence to examine.
	 * @param characters The characters to compare.
	 * @return <code>true</code> if the last character of the character sequence matches one of the given characters.
	 * @see Characters#contains(char)
	 */
	public static boolean endsWith(final CharSequence charSequence, final Characters characters) {
		final int length = charSequence.length();
		return length > 0 && characters.contains(charSequence.charAt(length - 1));
	}

	/**
	 * Determines if the character sequence ends with the given string.
	 * @param charSequence The character sequence to examine.
	 * @param string The string to compare.
	 * @return <code>true</code> if the last characters of the character sequence match those of the given string.
	 */
	public static boolean endsWith(final CharSequence charSequence, final String string) {
		final int delta = charSequence.length() - string.length(); //find out the difference in length between the strings
		if(delta < 0) //if the substring is too long
			return false; //the substring is too big to start the character sequence
		for(int i = string.length() - 1; i >= 0; --i) { //look at each character of the string
			if(string.charAt(i) != charSequence.charAt(i + delta)) //if these characters don't match in the same position
				return false; //the string doens't match
		}
		return true; //the character sequence ends with the string
	}

	/**
	 * Determines if the character sequence ends with the given string without case sensitivity.
	 * @param charSequence The character sequence to examine.
	 * @param string The string to compare.
	 * @return <code>true</code> if the last characters of the character sequence match those of the given string, case insensitively.
	 */
	public static boolean endsWithIgnoreCase(final CharSequence charSequence, final String string) {
		final int delta = charSequence.length() - string.length(); //find out the difference in length between the strings
		if(delta < 0) //if the substring is too long
			return false; //the substring is too big to start the character sequence
		for(int i = string.length() - 1; i >= 0; --i) { //look at each character of the string
			if(Character.toUpperCase(string.charAt(i)) != Character.toUpperCase(charSequence.charAt(i + delta))) //if these characters don't match in the same position
				return false; //the string doens't match
		}
		return true; //the character sequence ends with the string
	}

	/**
	 * Escapes a given string by inserting an escape character before every restricted character, including any occurrence of the given escape character.
	 * @implSpec This implementation delegates to {@link StringBuilders#escape(StringBuilder, Characters, char)}.
	 * @param charSequence The data to escape.
	 * @param restricted The characters to be escaped; should not include the escape character.
	 * @param escape The character used to escape the restricted characters.
	 * @return A string containing the escaped data.
	 * @throws NullPointerException if the given character sequence is <code>null</code>.
	 */
	public static String escape(final CharSequence charSequence, final Characters restricted, final char escape) {
		if(!contains(charSequence, restricted)) { //if there are no restricted characters in the string (assuming that most strings won't need to be escaped, it's less expensive to check up-front before we start allocating and copying)
			return charSequence.toString(); //the string doesn't need to be escaped
		}
		return StringBuilders.escape(new StringBuilder(charSequence), restricted, escape).toString(); //make a string builder copy and escape its contents
	}

	/**
	 * Escapes the indicated characters in the character sequence using the supplied escape character. All characters are first encoded using UTF-8. Every invalid
	 * character is converted to its Unicode hex equivalent and prefixed with the given escape character. This method uses <em>lowercase</em> hexadecimal escape
	 * codes. Characters are assumed to be valid unless specified otherwise. The escape character, if encountered, is not escaped unless it specifically meets one
	 * of the specified criteria; this allows re-escaping strings that may contain escape characters produced under less-strict rules (e.g. a URI containing
	 * escaped restricted characters, but still containing non-ASCII characters).
	 * @param charSequence The data to escape.
	 * @param validCharacters The characters that should not be escaped and all others should be escaped, or <code>null</code> if characters should not be matched
	 *          against valid characters.
	 * @param invalidCharacters The characters that, if they appear, should be escaped, or <code>null</code> if characters should not be matched against invalid
	 *          characters.
	 * @param maxCharacter The character value that represents the highest non-escaped value.
	 * @param escapeChar The character to prefix the hex representation.
	 * @param escapeLength The number of characters to use for the hex representation.
	 * @return A string containing the escaped data.
	 * @throws IllegalArgumentException if neither valid nor invalid characters are given.
	 */
	public static String escapeHex(final CharSequence charSequence, final Characters validCharacters, final Characters invalidCharacters, final int maxCharacter,
			final char escapeChar, final int escapeLength) {
		return escapeHex(charSequence, validCharacters, invalidCharacters, maxCharacter, escapeChar, escapeLength, Case.LOWERCASE); //default to lowercase
	}

	/**
	 * Escapes the indicated characters in the character sequence using the supplied escape character. All characters are first encoded using UTF-8. Every invalid
	 * character is converted to its Unicode hex equivalent and prefixed with the given escape character. Characters are assumed to be valid unless specified
	 * otherwise. The escape character, if encountered, is not escaped unless it specifically meets one of the specified criteria; this allows re-escaping strings
	 * that may contain escape characters produced under less-strict rules (e.g. a URI containing escaped restricted characters, but still containing non-ASCII
	 * characters).
	 * @param charSequence The data to escape.
	 * @param validCharacters The characters that should not be escaped and all others should be escaped, or <code>null</code> if characters should not be matched
	 *          against valid characters.
	 * @param invalidCharacters The characters that, if they appear, should be escaped, or <code>null</code> if characters should not be matched against invalid
	 *          characters.
	 * @param maxCharacter The character value that represents the highest non-escaped value.
	 * @param escapeChar The character to prefix the hex representation.
	 * @param escapeLength The number of characters to use for the hex representation.
	 * @param hexCase Whether the hex characters should be lowercase or uppercase.
	 * @return A string containing the escaped data.
	 * @throws IllegalArgumentException if neither valid nor invalid characters are given.
	 */
	public static String escapeHex(final CharSequence charSequence, final Characters validCharacters, final Characters invalidCharacters, final int maxCharacter,
			final char escapeChar, final int escapeLength, final Case hexCase) {
		//put the string in a string builder and escape it; although inserting encoded sequences may seem inefficient,
		//	it should be noted that filling a string buffer with the entire string is more efficient than doing it one character at a time,
		//	that characters needed encoding are generally uncommon, and that any copying of the string characters during insertion is done
		//	via a native method, which should happen very quickly
		return StringBuilders.escapeHex(new StringBuilder(charSequence), validCharacters, invalidCharacters, maxCharacter, escapeChar, escapeLength, hexCase)
				.toString();
	}

	/**
	 * Decodes the escaped characters in the character sequence by converting the hex value after each occurrence of the escape character to the corresponding
	 * Unicode character using UTF-8.
	 * @param charSequence The data to unescape.
	 * @param escapeChar The character that prefixes the hex representation.
	 * @param escapeLength The number of characters used for the hex representation.
	 * @return A character sequence containing the unescaped data.
	 * @throws IllegalArgumentException if a given escape character is not followed by an escape sequence.
	 * @throws IllegalArgumentException if an encountered escape sequence is not valid UTF-8.
	 */
	public static CharSequence unescapeHex(final CharSequence charSequence, final char escapeChar, final int escapeLength) {
		CharBuffer charBuffer = null; //we'll only create a char buffer if we need to unescape something
		ByteBuffer byteBuffer = null; //we'll create this during a decoding sequence
		int encodedByteCount = 0; //we'll set this value at the start of each encoded byte sequence
		CharsetDecoder utf8CharsetDecoder = null; //we'll only create a charset decode if we need to
		final int charSequenceLength = charSequence.length(); //get the length of the character sequence
		for(int i = 0; i < charSequenceLength; ++i) { //look at each character in the character sequence
			final char c = charSequence.charAt(i); //get a reference to this character in the character sequence
			if(c == escapeChar) { //if this is the beginning of an escaped sequence
				if(charBuffer == null) { //initialize the character buffer if needed
					charBuffer = CharBuffer.allocate(charSequenceLength); //unescaping should *reduce* the number of characters, if anything
					if(i > 0) { //append all the characters up to this point, if any
						charBuffer.append(charSequence, 0, i);
					}
					assert byteBuffer == null; //if we are starting to decode, we shouldn't have a byte buffer yet
					byteBuffer = ByteBuffer.allocate(UTF8.MAX_ENCODED_BYTE_COUNT_LENGTH);
					assert utf8CharsetDecoder == null; //if we are starting to decode, we shouldn't have a decoder yet
					utf8CharsetDecoder = UTF_8.newDecoder();
				}
				if(i < charSequenceLength - escapeLength) { //if there's room for enough hex characters after it
					//TODO create integer parsing method that works with CharSequence to obviate conversion to string
					final String escapeSequence = charSequence.subSequence(i + 1, i + escapeLength + 1).toString(); //get the hex characters in the escape sequence							
					try {
						final int octet = Integer.parseInt(escapeSequence, 16); //convert the escape sequence to a single integer value
						if(byteBuffer.position() == 0) { //if this is the initial octet in the sequence
							encodedByteCount = UTF8.getEncodedByteCountFromInitialOctet(octet); //determine how many bytes to expect, throwing an exception if invalid
						}
						byteBuffer.put((byte)octet); //add the byte to the buffer
						i += 2; //skip the escape sequence (we'll go to the last character, and we'll be advanced one character when we go back to the start of the loop)
					} catch(NumberFormatException numberFormatException) { //if the characters weren't really hex characters
						throw new IllegalArgumentException(format("Invalid escape sequence %s at index %s in character sequence %s.", escapeSequence, i, charSequence));
					}
				} else { //if there is no room for an escape sequence at the end of the string
					throw new IllegalArgumentException(
							format("Invalid escape sequence %s at index %s in character sequence %s.", charSequence.subSequence(i + 1, charSequenceLength), i, charSequence));
				}
				if(byteBuffer != null && byteBuffer.position() == encodedByteCount) { //if we have completed a sequence of UTF-8 bytes
					assert encodedByteCount > 0; //we should have initialized the byte count at the start of the sequence
					assert utf8CharsetDecoder != null;
					assert charBuffer != null;
					byteBuffer.flip(); //prepare the byte buffer for reading
					if(utf8CharsetDecoder.decode(byteBuffer, charBuffer, true).isError()) { //decode the bytes
						throw new IllegalArgumentException(format("Illegal UTF-8 sequence found in character sequence."));
					}
					byteBuffer.clear(); //we've finished our byte sequence; prepare for another
				}

			} else { //if this is not an escaped character
				checkArgument(byteBuffer == null || byteBuffer.position() == 0, "Incomplete UTF-8 sequence found in character sequence.");
				if(charBuffer != null) { //if we have found decoded characters and are thus decoding the string
					charBuffer.append(c);
				}
			}

		}
		if(charBuffer != null) { //if there was anything to decode
			charBuffer.flip(); //prepare for reading the characters.
			return charBuffer.toString();
		}
		return charSequence; //there was nothing to decode; just return the original character sequence
	}

	/**
	 * Determines the first index of the given character.
	 * @param charSequence The character sequence to check.
	 * @param character The character to search for.
	 * @return The index of the first occurrence of the given character, or -1 if the character was not found.
	 */
	public static int indexOf(final CharSequence charSequence, final char character) {
		return indexOf(charSequence, character, 0); //search from the beginning
	}

	/**
	 * Determines the first index of the given character.
	 * @param charSequence The character sequence to check.
	 * @param character The character to search for.
	 * @return The index of the first occurrence of the given character, or the length of the character sequence if the character was not found.
	 */
	public static int indexOfLength(final CharSequence charSequence, final char character) {
		final int index = indexOf(charSequence, character); //perform the search
		return index >= 0 ? index : charSequence.length(); //return the length if we're out of characters
	}

	/**
	 * Determines the first index of the given character.
	 * @implSpec If the character sequence is a {@link String}, this method delegates to {@link String#indexOf(int, int)}.
	 * @param charSequence The character sequence to check.
	 * @param character The character to search for.
	 * @param index The first index to examine.
	 * @return The index of the first occurrence of the given character, or -1 if the character was not found.
	 */
	public static int indexOf(final CharSequence charSequence, final char character, final int index) {
		if(charSequence instanceof String) { //if the character sequence is a string
			return ((String)charSequence).indexOf(character, index); //delegate to the String version, which is much more efficient
		}
		final int length = charSequence.length();
		for(int i = index; i < length; ++i) { //look at each character
			if(charSequence.charAt(i) == character) { //if this character matches
				return i; //return the matching index
			}
		}
		return -1; //show that we couldn't find the character
	}

	/**
	 * Determines the first index of the given character.
	 * @implSpec If the character sequence is a {@link String}, this method delegates to {@link String#indexOf(int, int)}.
	 * @param charSequence The character sequence to check.
	 * @param character The character to search for.
	 * @param index The first index to examine.
	 * @return The index of the first occurrence of the given character, or the length of the character sequence if the character was not found.
	 */
	public static int indexOfLength(final CharSequence charSequence, final char character, int index) {
		index = indexOf(charSequence, character, index); //perform the search
		return index >= 0 ? index : charSequence.length(); //return the length if we're out of characters		
	}

	/**
	 * Searches a character sequence and returns the first index of any character of the given characters, starting at the beginning.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @return The index of the first occurrence of one of the supplied characters, or -1 if none were found.
	 */
	public static int indexOf(final CharSequence charSequence, final Characters characters) {
		return indexOf(charSequence, characters, 0); //look of the characters, starting at the beginning of the string
	}

	/**
	 * Searches a character sequence and returns the first index of any character of the given characters, starting at the beginning.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @return The index of the first occurrence of one of the supplied characters, or the length of the character sequence if none were found.
	 */
	public static int indexOfLength(final CharSequence charSequence, Characters characters) {
		final int index = indexOf(charSequence, characters); //perform the search
		return index >= 0 ? index : charSequence.length(); //return the length if we're out of characters
	}

	/**
	 * Searches a character sequence and returns the first index of any character of the given characters, starting at the given index.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @param index The index to search from.
	 * @return The index of the first occurrence of one of the supplied characters, or -1 if none were found.
	 */
	public static int indexOf(final CharSequence charSequence, final Characters characters, final int index) {
		for(int i = index; i < charSequence.length(); ++i) { //look at each character in the sequence
			if(characters.contains(charSequence.charAt(i))) { //if this character is in our character string
				return i; //return the index we're at
			}
		}
		return -1; //if we make it to here, we didn't find any of the characters
	}

	/**
	 * Searches a character sequence and returns the first index of any character of the given characters, starting at the given index.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @param index The index to search from.
	 * @return The index of the first occurrence of one of the supplied characters, or the length of the character sequence if none were found.
	 */
	public static int indexOfLength(final CharSequence charSequence, final Characters characters, int index) {
		index = indexOf(charSequence, characters, index); //perform the search
		return index >= 0 ? index : charSequence.length(); //return the length if we're out of characters
	}

	/**
	 * Determines the last index of the given character.
	 * @param charSequence The character sequence to check.
	 * @param character The character to search for.
	 * @return The index of the last occurrence of the given character, or -1 if the character was not found.
	 * @see String#lastIndexOf(int)
	 */
	public static int lastIndexOf(final CharSequence charSequence, final char character) {
		return lastIndexOf(charSequence, character, charSequence.length() - 1); //search from the end
	}

	/**
	 * Determines the last index of the given character.
	 * @implSpec If the character sequence is a {@link String}, this method delegates to {@link String#lastIndexOf(int, int)}.
	 * @param charSequence The character sequence to check.
	 * @param character The character to search for.
	 * @param index The last index to examine; if greater than or equal to the length of this character sequence, it has the same effect as if it were equal to
	 *          one less than the length of this character sequence, and the entire character sequence may be searched; if negative, it has the same effect as if
	 *          it were -1, and -1 is returned.
	 * @return The index of the last occurrence of the given character, or -1 if the character was not found.
	 * @see String#lastIndexOf(int, int)
	 */
	public static int lastIndexOf(final CharSequence charSequence, final char character, int index) { //TODO add support for supplementary code points here and throughout
		if(charSequence instanceof String) { //if the character sequence is a string
			return ((String)charSequence).lastIndexOf(character, index); //delegate to the String version, which is much more efficient
		}
		final int length = charSequence.length();
		if(index >= length) { //adjust the length as per the String.lastIndexOf() API
			index = length - 1;
		}
		for(int i = index; i >= 0; --i) { //look at each character
			if(charSequence.charAt(i) == character) { //if this character matches
				return i; //return the matching index
			}
		}
		return -1; //show that we couldn't find the character
	}

	/**
	 * Searches a character sequence and returns the last index of any character of the given characters.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @return The index of the last occurrence of one of the supplied characters, or -1 if none were found.
	 * @see String#lastIndexOf(int)
	 */
	public static int lastIndexOf(final CharSequence charSequence, final Characters characters) {
		return lastIndexOf(charSequence, characters, charSequence.length() - 1); //look of the characters, starting at the end of the string
	}

	/**
	 * Searches a character sequence and returns the last index of any character of the given characters, starting at the given index.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @param index The last index to examine; if greater than or equal to the length of this character sequence, it has the same effect as if it were equal to
	 *          one less than the length of this character sequence, and the entire character sequence may be searched; if negative, it has the same effect as if
	 *          it were -1, and -1 is returned.
	 * @return The index of the last occurrence of one of the supplied characters, or -1 if none were found.
	 * @see String#lastIndexOf(int, int)
	 */
	public static int lastIndexOf(final CharSequence charSequence, final Characters characters, int index) {
		final int length = charSequence.length();
		if(index >= length) { //adjust the length as per the String.lastIndexOf() API
			index = length - 1;
		}
		for(int i = index; i >= 0; --i) { //look at each character in the sequence
			if(characters.contains(charSequence.charAt(i))) { //if this character is in our character string
				return i; //return the index we're at
			}
		}
		return -1; //if we make it to here, we didn't find any of the characters
	}

	/**
	 * Determines the first index not of the given character.
	 * @param charSequence The character sequence to check.
	 * @param character The character to search for.
	 * @return The index of the first occurrence not of the given character, or -1 if only the character was not found.
	 */
	public static int indexNotOf(final CharSequence charSequence, final char character) {
		return indexNotOf(charSequence, character, 0); //search from the beginning
	}

	/**
	 * Determines the first index not of the given character.
	 * @param charSequence The character sequence to check.
	 * @param character The character to search for.
	 * @return The index of the first occurrence not of the given character, or the length of the character sequence if only the character was not found.
	 */
	public static int indexNotOfLength(final CharSequence charSequence, final char character) {
		final int index = indexNotOf(charSequence, character); //perform the search
		return index >= 0 ? index : charSequence.length(); //return the length if we're out of characters
	}

	/**
	 * Determines the first index not of the given character.
	 * @param charSequence The character sequence to check.
	 * @param character The character to search for.
	 * @param index The first index to examine.
	 * @return The index of the first occurrence not of the given character, or -1 if only the character was not found.
	 */
	public static int indexNotOf(final CharSequence charSequence, final char character, final int index) {
		final int length = charSequence.length();
		for(int i = index; i < length; ++i) { //look at each character
			if(charSequence.charAt(i) != character) { //if this character does not match
				return i; //return the matching index
			}
		}
		return -1;
	}

	/**
	 * Determines the first index not of the given character.
	 * @param charSequence The character sequence to check.
	 * @param character The character to search for.
	 * @param index The first index to examine.
	 * @return The index of the first occurrence not of the given character, or the length of the character sequence if only the character was not found.
	 */
	public static int indexNotOfLength(final CharSequence charSequence, final char character, int index) {
		index = indexNotOf(charSequence, character, index); //perform the search
		return index >= 0 ? index : charSequence.length(); //return the length if we're out of characters		
	}

	/**
	 * Searches a character sequence and returns the first index of any character not of the given characters, starting at the beginning.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @return The index of the first occurrence not of one of the supplied characters, or -1 if only the characters were found.
	 */
	public static int indexNotOf(final CharSequence charSequence, final Characters characters) {
		return indexNotOf(charSequence, characters, 0); //look of the characters, starting at the beginning of the string
	}

	/**
	 * Searches a character sequence and returns the first index of any character not of the given characters, starting at the beginning.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @return The index of the first occurrence not of one of the supplied characters, or the length of the character sequence if only the characters were found.
	 */
	public static int indexNotOfLength(final CharSequence charSequence, Characters characters) {
		final int index = indexNotOf(charSequence, characters); //perform the search
		return index >= 0 ? index : charSequence.length(); //return the length if we're out of characters
	}

	/**
	 * Searches a character sequence and returns the first index of any character not of the given characters, starting at the given index.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @param index The index to search from.
	 * @return The index of the first occurrence not of one of the supplied characters, or -1 if only the characters were found.
	 */
	public static int indexNotOf(final CharSequence charSequence, final Characters characters, final int index) {
		for(int i = index; i < charSequence.length(); ++i) { //look at each character in the sequence
			if(!characters.contains(charSequence.charAt(i))) { //if this character is not in the characters
				return i; //return the index we're at
			}
		}
		return -1;
	}

	/**
	 * Searches a character sequence and returns the first index of any character not of the given characters, starting at the given index.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @param index The index to search from.
	 * @return The index of the first occurrence not of one of the supplied characters, or the length of the character sequence if only the characters were found.
	 */
	public static int indexNotOfLength(final CharSequence charSequence, final Characters characters, int index) {
		index = indexNotOf(charSequence, characters, index); //perform the search
		return index >= 0 ? index : charSequence.length(); //return the length if we're out of characters
	}

	/**
	 * Determines the last index not of the given character.
	 * @param charSequence The character sequence to check.
	 * @param character The character to search for.
	 * @return The index of the last occurrence not of the given character, or -1 if only the character was not found.
	 * @see String#lastIndexOf(int)
	 */
	public static int lastIndexNotOf(final CharSequence charSequence, final char character) {
		return lastIndexNotOf(charSequence, character, charSequence.length() - 1); //search from the end
	}

	/**
	 * Determines the last index not of the given character.
	 * @param charSequence The character sequence to check.
	 * @param character The character to search for.
	 * @param index The last index to examine; if greater than or equal to the length of this character sequence, it has the same effect as if it were equal to
	 *          one less than the length of this character sequence, and the entire character sequence may be searched; if negative, it has the same effect as if
	 *          it were -1, and -1 is returned.
	 * @return The index of the last occurrence not of the given character, or -1 if only the character was not found.
	 * @see String#lastIndexOf(int, int)
	 */
	public static int lastIndexNotOf(final CharSequence charSequence, final char character, int index) { //TODO add support for supplementary code points here and throughout
		final int length = charSequence.length();
		if(index >= length) { //adjust the length as per the String.lastIndexOf() API
			index = length - 1;
		}
		for(int i = index; i >= 0; --i) { //look at each character
			if(charSequence.charAt(i) != character) { //if this character does not match
				return i;
			}
		}
		return -1;
	}

	/**
	 * Searches a character sequence and returns the last index of any character not of the given characters.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @return The index of the last occurrence not of one of the supplied characters, or -1 if only the characters were found.
	 * @see String#lastIndexOf(int)
	 */
	public static int lastIndexNotOf(final CharSequence charSequence, final Characters characters) {
		return lastIndexNotOf(charSequence, characters, charSequence.length() - 1); //look of the characters, starting at the end of the string
	}

	/**
	 * Searches a character sequence and returns the last index of any character not of the given characters, starting at the given index.
	 * @param charSequence The character sequence to be searched.
	 * @param characters The characters to check.
	 * @param index The last index to examine; if greater than or equal to the length of this character sequence, it has the same effect as if it were equal to
	 *          one less than the length of this character sequence, and the entire character sequence may be searched; if negative, it has the same effect as if
	 *          it were -1, and -1 is returned.
	 * @return The index of the last occurrence not of one of the supplied characters, or -1 if only the characters were found.
	 * @see String#lastIndexOf(int, int)
	 */
	public static int lastIndexNotOf(final CharSequence charSequence, final Characters characters, int index) {
		final int length = charSequence.length();
		if(index >= length) { //adjust the length as per the String.lastIndexOf() API
			index = length - 1;
		}
		for(int i = index; i >= 0; --i) { //look at each character in the sequence
			if(!characters.contains(charSequence.charAt(i))) { //if this character is not in our characters
				return i; //return the index we're at
			}
		}
		return -1;
	}

	/**
	 * Determines the longest common suffix of segments from a list of character sequences. For example if any combination of the strings
	 * <code>"www.example.com"</code>, <code>"test.example.com"</code>, and <code>"example.com"</code> are passed using the delimiter <code>'.'</code>, the common
	 * segment suffix <code>"example.com"</code> is returned.
	 * @apiNote This method does not make any checks to determine whether a segment is empty, e.g. <code>"foo..bar"</code>.
	 * @implNote The current implementation does not distinguish between sequences beginning and/or ending with the delimiter. That is <code>foo.bar</code> and
	 *           <code>foo.bar.</code> are considered to have the same common segment suffix <code>foo.bar</code>, for example.
	 * @param charSequences The list of character sequences to check.
	 * @param delimiter The delimiter to use in determining the segments.
	 * @return A string representing the longest common suffix of segments, which may not be present if there is no longest common segment suffix.
	 * @throws NullPointerException if the list is <code>null</code> or contains a <code>null</code> value.
	 */
	public static Optional<String> longestCommonSegmentSuffix(@Nonnull final List<? extends CharSequence> charSequences, final char delimiter) {
		final Characters delimiterCharacters = Characters.of(delimiter);
		final List<List<String>> segmentsLists = charSequences.stream().map(delimiterCharacters::split).collect(toList());
		final List<String> longestCommonSuffix = longestCommonSuffix(segmentsLists);
		if(longestCommonSuffix.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(String.join(String.valueOf(delimiter), longestCommonSuffix)); //TODO improve to better handle beginning ending delimiters
	}

	/**
	 * Determines if the character sequence consists of nothing but the following character.
	 * @param charSequence The character sequence to examine.
	 * @param c The character that could make up the entire sequence.
	 * @return <code>true</code> if there are no other characters but the specified character, <code>false</code> if there are other characters or if the string
	 *         is the empty string.
	 * @deprecated to be replaced with {@link #isEveryChar(CharSequence, Predicate)}, e.g. {@code isEveryChar(text, character -> character == c))}.
	 */
	@Deprecated(forRemoval = true)
	public static final boolean isAll(final CharSequence charSequence, final char c) {
		if(charSequence.length() == 0) //if this is an empty string
			return false; //there are no characters to check
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each character in the string
			if(charSequence.charAt(i) != c) //if this isn't the specified character
				return false; //show that the string contains other characters besides the one specified
		}
		return true; //if we make it to here, there weren't any characters other than the one specified
	}

	/**
	 * Determines if the character sequence consists of nothing but the given characters.
	 * @param charSequence The character sequence to examine.
	 * @param characters The characters that could make up the entire string, in any order.
	 * @return <code>true</code> if there are no other characters but the specified characters, <code>false</code> if there are other characters or if the
	 *         character sequence is empty.
	 * @deprecated in favor of {@link #isEveryChar(CharSequence, Predicate)}, e.g. {@code isEveryChar(text, characters::contains))}.
	 */
	@Deprecated(forRemoval = true)
	public static final boolean isAllChars(final CharSequence charSequence, final Characters characters) {
		if(charSequence.length() == 0) //if this is an empty string
			return false; //there are no characters to check
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each character in the string
			if(!characters.contains(charSequence.charAt(i))) //if this character isn't in the string
				return false; //show that the string contains other characters besides the ones specified
		}
		return true; //if we make it to here, there weren't any characters other than the ones specified
	}

	/**
	 * Tests every character of a sequence to see whether it meets a certain predicate.
	 * @apiNote Caution: This method may only give correct results if the characters in the sequence all lie within the Unicode BMP. If characters with higher
	 *          code points than the BMP are expected, {@link #isEveryCodePoint(CharSequence, IntPredicate)} should be used instead. This method may only be
	 *          safely used if the given predicate provides correct results when given the low and high parts of a surrogate pair as separate characters; for
	 *          example, a predicate that returned <code>true</code> if every character were in the ASCII range, which would correctly return <code>false</code>
	 *          if a low or high character of a surrogate pair were encountered.
	 * @param charSequence The character sequence to check.
	 * @param predicate The test to apply to each character.
	 * @return <code>true</code> if every character in the sequence complies with the given predicate, or if the sequence is empty.
	 * @see #isEveryCodePoint(CharSequence, IntPredicate)
	 */
	public static boolean isEveryChar(@Nonnull final CharSequence charSequence, @Nonnull final Predicate<Character> predicate) {
		for(int i = charSequence.length() - 1; i >= 0; i--) {
			if(!predicate.test(charSequence.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests every Unicode code point of a sequence to see whether it meets a certain predicate.
	 * @apiNote This method is to be preferred to {@link #isEveryChar(CharSequence, Predicate)} in almost all cases. {@link #isEveryChar(CharSequence, Predicate)}
	 *          may be slightly more efficient, but must only be used if the testing logic works with chars in the BMP.
	 * @param charSequence The character sequence to check.
	 * @param predicate The test to apply to each code point.
	 * @return <code>true</code> if every character in the sequence complies with the given predicate, or if the sequence is empty.
	 */
	public static boolean isEveryCodePoint(@Nonnull final CharSequence charSequence, @Nonnull IntPredicate predicate) {
		//Rather than checking to see if all code points match the predicate,
		//see if there is _not_ any code point that does _not_ match the predicate.
		return !charSequence.codePoints().filter(predicate.negate()).findAny().isPresent();
	}

	/**
	 * Determines whether a character sequence is capitalized. A character sequence is capitalized if it contains any characters and the first character is
	 * uppercase.
	 * @param charSequence The character sequence to examine.
	 * @return <code>true</code> if the character sequence is capitalized.
	 * @deprecated in favor of {@link #isEveryCodePoint(CharSequence, IntPredicate)}, e.g. {@code isEveryCodePoint(text, Character::isUpperCase)}.
	 */
	@Deprecated(forRemoval = true)
	public static final boolean isCapitalized(final CharSequence charSequence) {
		return charSequence.length() > 0 && Character.isUpperCase(charSequence.charAt(0)); //determine if the first character is capitalized
	}

	/**
	 * Determines whether a character sequence contains only Unicode digits.
	 * @param charSequence The character sequence to examine.
	 * @return <code>true</code> if all the characters in the sequence are digits.
	 * @deprecated in favor of {@link #isEveryCodePoint(CharSequence, IntPredicate)}, e.g. {@code isEveryCodePoint(text, Character::isDigit)}.
	 */
	@Deprecated(forRemoval = true)
	public static final boolean isDigits(final CharSequence charSequence) {
		if(charSequence.length() == 0) //if this is an empty string
			return false; //there are no characters to check
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each letter in the string
			if(!Character.isDigit(charSequence.charAt(i))) //if this isn't a digit
				return false; //show that the string doesn't contain only digits
		}
		return true; //if we make it to here, there weren't any non-digits in the string
	}

	/**
	 * Determines whether a character sequence contains only Unicode letters.
	 * @param charSequence The character sequence to examine.
	 * @return <code>true</code> if all the characters in the sequence are letters.
	 * @deprecated in favor of {@link #isEveryCodePoint(CharSequence, IntPredicate)}, e.g. {@code isEveryCodePoint(text, Character::isLetter)}.
	 */
	@Deprecated(forRemoval = true)
	public static final boolean isLetters(final CharSequence charSequence) {
		if(charSequence.length() == 0) //if this is an empty string
			return false; //there are no characters to check
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each letter in the string
			if(!Character.isLetter(charSequence.charAt(i))) //if this isn't a letter
				return false; //show that the string doesn't contain only letters
		}
		return true; //if we make it to here, there weren't any non-letters in the string
	}

	/**
	 * Determines whether a character sequence contains only Unicode letters and digits.
	 * @param charSequence The character sequence to examine.
	 * @return <code>true</code> if all the characters in the sequence are letters and digits.
	 * @deprecated in favor of {@link #isEveryCodePoint(CharSequence, IntPredicate)}, e.g. {@code isEveryCodePoint(text, Character::isLetterOrDigit)}.
	 */
	@Deprecated(forRemoval = true)
	public static final boolean isLettersDigits(final CharSequence charSequence) {
		if(charSequence.length() == 0) //if this is an empty string
			return false; //there are no characters to check
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each letter in the string
			final char character = charSequence.charAt(i); //get this character
			if(!Character.isLetter(character) && !Character.isDigit(character)) //if this is not a letter or a digit
				return false; //show that the string contains non-letter or non-digit characters
		}
		return true; //if we make it to here, there weren't any non-letters or non-digits in the string
	}

	/**
	 * Determines whether a character sequence contains only Unicode letters, digits, and the supplied extra characters.
	 * @param charSequence The character sequence to examine.
	 * @param characters Extra characters to allow.
	 * @return <code>true</code> if all the characters in the sequence are letters, digits, and/or allowed characters.
	 * @deprecated in favor of {@link #isEveryCodePoint(CharSequence, IntPredicate)} and {@link Characters}, e.g.
	 *             {@code isEveryCodePoint(text, c -> Character.isLetterOrDigit(c) || characters.contains(c)}.
	 */
	@Deprecated(forRemoval = true)
	public static final boolean isLettersDigitsCharacters(final CharSequence charSequence, final String characters) {
		if(charSequence.length() == 0) //if this is an empty string
			return false; //there are no characters to check
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each letter in the string
			final char character = charSequence.charAt(i); //get this character
			if(!Character.isLetter(character) && !Character.isDigit(character) && !contains(characters, character)) //if this is not a letter or a digit, and it's not in our extra character list
				return false; //show that the string contains something in none of our lists 
		}
		return true; //if we make it to here, there weren't any non-letters or non-digits in the string
	}

	/**
	 * Determines whether a character sequence contains only numbers and decimals or commas.
	 * @param charSequence The character sequence to examine.
	 * @return <code>true</code> if all the characters represent a number.
	 * @deprecated in favor of {@link #isEveryCodePoint(CharSequence, IntPredicate)} with a combination of {@link Characters} and/or
	 *             {@link Character#isLetterOrDigit(int)} as appropriate for the use case.
	 */
	@Deprecated(forRemoval = true)
	public static final boolean isNumber(final CharSequence charSequence) {
		if(charSequence.length() == 0) //if this is an empty string
			return false; //there are no characters to check
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each letter in the string
			final char c = charSequence.charAt(i); //get this character
			if(!Character.isDigit(c) && c != '.' && c != ',') //if this isn't a digit, a decimal, or a comma
				return false; //show that the string doesn't contain a number
		}
		return true; //if we make it to here, this is a number
	}

	/**
	 * Determines whether a character sequence contains only Roman numerals.
	 * @param charSequence The character sequence to examine.
	 * @return <code>true</code> if all the characters in the sequence are Roman numerals.
	 * @deprecated in favor of {@link #isEveryChar(CharSequence, Predicate)} or {@link #isEveryCodePoint(CharSequence, IntPredicate)} in conjunction with
	 *             {@link com.globalmentor.text.RomanNumerals}.
	 */
	@Deprecated(forRemoval = true)
	public static final boolean isRomanNumerals(final CharSequence charSequence) {
		if(charSequence.length() == 0) //if this is an empty string
			return false; //there are no characters to check
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each character in the string
			if(!isRomanNumeral(charSequence.charAt(i))) //if this isn't a Roman numeral
				return false; //show that the string doesn't contain only Roman numerals
		}
		return true; //if we make it to here, there weren't any characters in the string that were not Roman numerals
	}

	/**
	 * Determines whether all the letters in a character sequence are capital letters.
	 * @param charSequence The character sequence to examine.
	 * @return <code>true</code> if all the letters in the sequence are capitalized.
	 * @deprecated in favor of {@link #isEveryCodePoint(CharSequence, IntPredicate)}, e.g.
	 *             {@code isEveryCodePoint(text, c -> !Character.isLetter(c) || Character.isUpperCase(c))}.
	 */
	@Deprecated(forRemoval = true)
	public static final boolean isUpperCase(final CharSequence charSequence) {
		if(charSequence.length() == 0) //if this is an empty string
			return false; //there are no characters to check
		for(int i = charSequence.length() - 1; i >= 0; --i) { //look at each letter in the string
			final char character = charSequence.charAt(i); //get this character
			if(Character.isLetter(character) && !Character.isUpperCase(character)) //if this is a letter that is not uppercase
				return false; //show that the string contains non-uppercase characters
		}
		return true; //if we make it to here, there weren't any non-uppercase characters in the string
	}

	/**
	 * Concatenates the given character sequences with no delimiter between them.
	 * @param charSequences The character sequences to be concatenated.
	 * @return The string containing the concatenated character sequences.
	 * @throws NullPointerException if the given character sequences is <code>null</code>.
	 */
	public static CharSequence join(final CharSequence... charSequences) {
		return join(UNDEFINED_CHAR, charSequences); //join with no delimiter
	}

	/**
	 * Concatenates the given character sequences, separated by the given delimiter.
	 * @param delimiter The delimiter to be placed between each character sequence, or {@link Characters#UNDEFINED_CHAR} if no delimiter should be placed between
	 *          the character sequences.
	 * @param charSequences The character sequences to be concatenated.
	 * @return The string containing the concatenated character sequences.
	 * @throws NullPointerException if the given character sequences is <code>null</code>.
	 */
	public static CharSequence join(final char delimiter, final CharSequence... charSequences) {
		final int length = charSequences.length; //find out how many character sequences there are
		if(length > 1) { //if there are more than one character sequence
			CharSequence nonEmptyCharSequence = null; //see if we can short-circuit the process if there is only one non-empty character sequence
			for(final CharSequence charSequence : charSequences) {
				if(charSequence.length() > 0) { //if this character sequence has characters
					if(nonEmptyCharSequence != null) { //if we already found another character sequence with characters (i.e. there are at least two)
						nonEmptyCharSequence = null; //don't try anymore
						break; //stop searching
					}
					nonEmptyCharSequence = charSequence; //keep track of the first (and so far, only) character sequence with characters
				}
			}
			if(nonEmptyCharSequence != null) { //if there was one and only one character sequence with characters
				return nonEmptyCharSequence; //there's no use joining---just return the character sequence
			}
			return StringBuilders.append(new StringBuilder(), delimiter, charSequences); //join the character strings using a string builder
		} else if(length == 1) { //if there is only one character sequence
			return charSequences[0]; //return the one character sequence
		} else { //if there are no character sequences
			return ""; //return the empty string
		}
	}

	/**
	 * Removes all normalized Unicode marks such as accents from a string. For example:
	 * <ul>
	 * <li>'é' will be converted to 'e' (Unicode non-spacing marks)</li>
	 * <li>vowel signs in Hindi will be removed (Unicode spacing combining marks)</li>
	 * <li>circles around characters will be removed (Unicode enclosing marks)</li>
	 * </ul>
	 * @param charSequence The character sequence from which to remove marks.
	 * @return The normalized string with marks removed.
	 * @see <a href="http://stackoverflow.com/q/3322152/421049">Java - getting rid of accents and converting them to regular letters</a>
	 * @see #normalizeForSearch(CharSequence)
	 */
	public static String removeMarks(final CharSequence charSequence) {
		final String string = Normalizer.normalize(charSequence, Normalizer.Form.NFD); //perform canonical decomposition
		return string.replaceAll("\\p{M}", ""); //remove all resulting decomposed marks
	}

	/**
	 * Normalizes a string so that it can be used as a liberally matching lookup without regard to diacritics or case.
	 * <ul>
	 * <li>Decomposes characters such as ﬁ to fi.</li>
	 * <li>Removes all normalized Unicode marks such as accents.</li>
	 * <li>Converts the string to lowercase.</li>
	 * </ul>
	 * <p>This method converts to lowercase using {@link Locale#ROOT}. If you wish you supply a specific locale, use
	 * {@link #normalizeForSearch(CharSequence, Locale)}.</p>
	 * @param charSequence The character sequence from which to remove marks.
	 * @return The normalized string with marks removed.
	 * @see <a href="http://stackoverflow.com/q/3322152/421049">Java - getting rid of accents and converting them to regular letters</a>
	 */
	public static String normalizeForSearch(@Nonnull final CharSequence charSequence) {
		return normalizeForSearch(charSequence, Locale.ROOT);
	}

	/**
	 * Normalizes a string so that it can be used as a liberally matching lookup without regard to diacritics or case.
	 * <ul>
	 * <li>Decomposes characters such as ﬁ to fi.</li>
	 * <li>Removes all normalized Unicode marks such as accents.</li>
	 * <li>Converts the string to lowercase.</li>
	 * </ul>
	 * @param charSequence The character sequence from which to remove marks.
	 * @param locale The locale to use for normalization; specifically for converting to lowercase.
	 * @return The normalized string with marks removed.
	 * @see <a href="http://stackoverflow.com/q/3322152/421049">Java - getting rid of accents and converting them to regular letters</a>
	 */
	public static String normalizeForSearch(@Nonnull final CharSequence charSequence, @Nonnull final Locale locale) {
		final String string = Normalizer.normalize(charSequence, Normalizer.Form.NFKD); //perform compatibility decomposition
		return string.replaceAll("\\p{M}", "").toLowerCase(locale); //remove all resulting decomposed marks and convert to lowercase
	}

	/**
	 * Determines if the character sequence starts with the given character.
	 * @param charSequence The character sequence to examine.
	 * @param character The character to compare.
	 * @return <code>true</code> if the first character of the character sequence matches the given character.
	 */
	public static boolean startsWith(final CharSequence charSequence, final char character) {
		//see if the character sequence has at least one character, and the first character matches our character
		return charSequence.length() > 0 && charSequence.charAt(0) == character;
	}

	/**
	 * Determines if the character sequence starts with one of the given characters.
	 * @param charSequence The character sequence to examine.
	 * @param characters The characters to compare.
	 * @return <code>true</code> if the first character of the character sequence matches one of the given characters.
	 * @see Characters#contains(char)
	 */
	public static boolean startsWith(final CharSequence charSequence, final Characters characters) {
		return charSequence.length() > 0 && characters.contains(charSequence.charAt(0));
	}

	/**
	 * Determines if the character sequence starts with the given string.
	 * @param charSequence The character sequence to examine.
	 * @param string The string to compare.
	 * @return <code>true</code> if the first characters of the character sequence match those of the given string.
	 * @throws NullPointerException if the given string is <code>null</code>.
	 */
	public static boolean startsWith(final CharSequence charSequence, final CharSequence string) { //TODO refactor startsWith() and endsWith() into a generic method
		return startsWith(charSequence, 0, string);
	}

	/**
	 * Determines if the character sequence starts with the given string, starting at the given index.
	 * @param charSequence The character sequence to examine.
	 * @param index The index at which to search.
	 * @param string The string to compare.
	 * @return <code>true</code> if the first characters of the character sequence match those of the given string.
	 * @throws NullPointerException if the given string is <code>null</code>.
	 */
	public static boolean startsWith(final CharSequence charSequence, final int index, final CharSequence string) { //TODO refactor startsWith() and endsWith() into a generic method
		if(charSequence.length() - index < string.length()) { //if the substring is too long
			return false; //the substring is too big to start the character sequence
		}
		for(int i = string.length() - 1; i >= 0; --i) { //look at each character of the string
			if(string.charAt(i) != charSequence.charAt(index + i)) { //if these characters don't match in the same position
				return false; //the string doesn't match
			}
		}
		return true; //the character sequence starts with the string
	}

	/**
	 * Determines which if any of the given strings the character sequence starts with.
	 * @param <S> The type of the charSequence.
	 * @param charSequence The character sequence to examine.
	 * @param strings The string to compare.
	 * @return The string beginning the character sequence, or <code>null</code> if none of the strings start the character sequence.
	 * @throws NullPointerException if the collection of strings of any of the strings is <code>null</code>.
	 */
	public static <S extends CharSequence> S getStartsWith(final CharSequence charSequence, final Collection<S> strings) {
		return getStartsWith(charSequence, 0, strings);
	}

	/**
	 * Determines which if any of the given strings the character sequence starts with, starting at the given index.
	 * @param <S> The type of the charSequence.
	 * @param charSequence The character sequence to examine.
	 * @param index The index at which to search.
	 * @param strings The string to compare.
	 * @return The string beginning the character sequence, or <code>null</code> if none of the strings start the character sequence.
	 * @throws NullPointerException if the collection of strings of any of the strings is <code>null</code>.
	 */
	public static <S extends CharSequence> S getStartsWith(final CharSequence charSequence, final int index, final Collection<S> strings) {
		for(final S string : strings) {
			if(startsWith(charSequence, index, string)) {
				return string;
			}
		}
		return null;
	}

	/**
	 * Determines if the character sequence starts with one of the given characters.
	 * @param charSequence The character sequence to examine.
	 * @param characters The characters to compare.
	 * @return <code>true</code> if the first character of the character sequence matches one of those in the given string.
	 */
	public static boolean startsWithChar(final CharSequence charSequence, final Characters characters) {
		//see if the character sequence has at least one character, and the first character matches our character
		return charSequence.length() > 0 && characters.contains(charSequence.charAt(0));
	}

	/**
	 * Returns a character array containing the characters from the given character sequence.
	 * @param charSequence The character sequence from which to retrieve characters.
	 * @return A character array containing the characters from the given character sequence.
	 * @throws NullPointerException if the given character sequence is <code>null</code>.
	 */
	public static char[] toCharArray(final CharSequence charSequence) {
		if(charSequence instanceof String) { //if this is a String, don't invent the wheel
			return ((String)charSequence).toCharArray();
		}
		final int length = charSequence.length();
		final char[] chars = new char[length]; //create a new character array of the correct length
		for(int i = length - 1; i >= 0; --i) { //populate the character array
			chars[i] = charSequence.charAt(i);
		}
		return chars;
	}

	/**
	 * Truncates the end of the string beginning at the first occurrence of the given character. If the character sequence does not contain the truncate
	 * character, no action takes place.
	 * @param charSequence The character sequence to check.
	 * @param truncateChar The character indicating the part of the sequence to trim.
	 * @return A new character sequence with the specified character and following characters removed.
	 */
	public static CharSequence truncateAtFirst(final CharSequence charSequence, final char truncateChar) {
		final int index = indexOf(charSequence, truncateChar); //find the first occurrence of the truncate character
		return index >= 0 ? charSequence.subSequence(0, index) : charSequence; //truncate the character sequence if we can		
	}

	/**
	 * Truncates the end of the string beginning at the last occurrence of the given character. If the character sequence does not contain the truncate character,
	 * no action takes place.
	 * @param charSequence The character sequence to check.
	 * @param truncateChar The character indicating the part of the sequence to trim.
	 * @return A new character sequence with the last of the specified character and following characters removed.
	 */
	public static CharSequence truncateAtLast(final CharSequence charSequence, final char truncateChar) {
		final int index = lastIndexOf(charSequence, truncateChar); //find the last occurrence of the truncate character
		return index >= 0 ? charSequence.subSequence(0, index) : charSequence; //truncate the character sequence if we can		
	}

	/**
	 * Determines if the given character sequence is composed of the single given character. This method allows comparison of a character string with a character
	 * without creating a string for the character, for example.
	 * @param charSequence The character sequence to compare.
	 * @param character The character to compare with the character sequence.
	 * @return <code>true</code> if the character sequence is composed of one character and that character matches the given character.
	 */
	public static boolean equals(final CharSequence charSequence, final char character) {
		return charSequence.length() == 1 && charSequence.charAt(0) == character; //see if the character sequence has only one character, the given character
	}

	/**
	 * Compares the characters in one character sequence with characters in another character sequence.
	 * @param charSequence1 The character sequence to compare.
	 * @param charSequence2 The character sequence to compare with.
	 * @return <code>true</code> if the characters in the first character sequence equal the characters in the second character sequence.
	 */
	public static boolean equals(final CharSequence charSequence1, final CharSequence charSequence2) {
		if(charSequence1 == charSequence2) { //identity always implies equality
			return true;
		}
		return equals(charSequence1, charSequence2, 0);
	}

	/**
	 * Compares the characters in one character sequence with characters in another character sequence, starting at the given location to the end of the second
	 * character sequence.
	 * @param charSequence1 The character sequence to compare.
	 * @param charSequence2 The character sequence to compare with.
	 * @param start The starting location in the second character sequence, inclusive.
	 * @return <code>true</code> if the characters in the first character sequence equal the indicated characters in the second character sequence.
	 * @throws StringIndexOutOfBoundsException if <code>start</code> is negative or greater than the length of the second character sequence.
	 */
	public static boolean equals(final CharSequence charSequence1, final CharSequence charSequence2, final int start) {
		return equals(charSequence1, charSequence2, start, charSequence2.length());
	}

	/**
	 * Compares the characters in one character sequence with characters in another character sequence. If the given end of the second character sequence (the
	 * character sequence to which the first is being compared) is past the end, it is adjusted to be equal to the end of the second character sequence.
	 * @param charSequence1 The character sequence to compare.
	 * @param charSequence2 The character sequence to compare with.
	 * @param start The starting location in the second character sequence, inclusive.
	 * @param end The ending location in the second character sequence, exclusive.
	 * @return <code>true</code> if the characters in the first character sequence equal the indicated characters in the second character sequence.
	 * @throws StringIndexOutOfBoundsException if <code>start</code> or <code>end</code> is negative or greater than <code>length()</code>, or <code>start</code>
	 *           is greater than <code>end</code>, with the exception that if <code>end</code> is greater than the length of the second character sequence it will
	 *           be adjusted to equal the end.
	 */
	public static boolean equals(final CharSequence charSequence1, final CharSequence charSequence2, final int start, final int end) {
		return equals(charSequence1, 0, charSequence1.length(), charSequence2, start, end);
	}

	/**
	 * Compares characters in one character sequence with characters in another character sequence. If the given end of the second character sequence (the
	 * character sequence to which the first is being compared) is past the end, it is adjusted to be equal to the end of the second character sequence.
	 * @param charSequence1 The character sequence to compare.
	 * @param start1 The starting location in the first character sequence, inclusive.
	 * @param end1 The ending location in the first character sequence, exclusive.
	 * @param charSequence2 The character sequence to compare with.
	 * @param start2 The starting location in the second character sequence, inclusive.
	 * @param end2 The ending location in the second character sequence, exclusive.
	 * @return <code>true</code> if the indicated characters in the first character sequence equal the indicated characters in the second character sequence.
	 * @throws StringIndexOutOfBoundsException if <code>start</code> or <code>end</code> is negative or greater than <code>length()</code>, or <code>start</code>
	 *           is greater than <code>end</code>, with the exception that if <code>end2</code> is greater than the length of the second character sequence it
	 *           will be adjusted to equal the end.
	 */
	public static boolean equals(final CharSequence charSequence1, final int start1, final int end1, final CharSequence charSequence2, final int start2,
			int end2) {
		checkBounds(charSequence1, start1, end1);
		final int length2 = charSequence2.length();
		if(end2 > length2) { //adjust the second character sequence's end if needed
			end2 = length2;
		}
		checkBounds(charSequence2, start2, end2);
		if((end2 - start2) != (end1 - start1)) { //if the counts differ
			return false;
		}
		for(int i1 = start1, i2 = start2; i1 < end1; ++i1, ++i2) { //look at each character; we only need to check one end position because we already made sure the counts are the same
			if(charSequence1.charAt(i1) != charSequence2.charAt(i2)) { //if these characters don't match
				return false;
			}
		}
		return true; //everything matches		
	}

	/**
	 * Turns a null character sequence into an empty character sequence.
	 * @param charSequence The character sequence to examine, or <code>null</code>.
	 * @return The given character sequence, or an empty character sequence if the given character sequence is <code>null</code>;.
	 * @see #nullify(CharSequence)
	 */
	public static CharSequence denull(final CharSequence charSequence) {
		return charSequence != null ? charSequence : "";
	}

	/**
	 * Turns an empty character sequence into <code>null</code>.
	 * @param <CS> The type of the charSequence.
	 * @param charSequence The character sequence to examine, or <code>null</code>.
	 * @return The given character sequence, or <code>null</code> if the given character sequence has no characters or no character sequence was given.
	 * @see #denull(CharSequence)
	 */
	public static <CS extends CharSequence> CS nullify(final CS charSequence) {
		return charSequence != null && charSequence.length() > 0 ? charSequence : null;
	}

	/**
	 * Modifies text as needed to displayable text by e.g. converting control characters to visible characters representing the control characters.
	 * @apiNote This method does not fully sanitize text in any secure way to provide against injection attacks. It only makes a string more readable for
	 *          displaying to a user.
	 * @implSpec This implementation uses {@link Characters#toDisplay(char)}.
	 * @param text The text to display.
	 * @return A string form of character sequence that might be more appropriate for displaying to the user.
	 */
	public static CharSequence toDisplay(@Nonnull final CharSequence text) {
		//assume most character sequences don't need any characters replaced
		final int length = text.length();
		boolean unchanged = true;
		int i;
		for(i = 0; i < length && unchanged; i++) {
			final char c = text.charAt(i);
			unchanged = Characters.toDisplay(c) == c;
		}
		if(unchanged) {
			return text;
		}
		assert i > 0 : "A character could not have changed if the string were empty.";
		//copy characters and convert, starting at `i-1`, which was the first changed character 
		final StringBuilder stringBuilder = new StringBuilder(length);
		stringBuilder.append(text); //append all the text; may be more efficient than appending character by character
		for(i = i - 1; i < length; i++) {
			final char c = text.charAt(i);
			final char display = Characters.toDisplay(c);
			if(display != c) {
				stringBuilder.setCharAt(i, display);
			}
		}
		return stringBuilder;
	}
}
