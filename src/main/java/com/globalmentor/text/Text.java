/*
 * Copyright © 1996-2012 GlobalMentor, Inc. <http://www.globalmentor.com/>
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

package com.globalmentor.text;

import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.util.regex.Pattern;

import com.globalmentor.collections.comparators.SortOrder;
import com.globalmentor.java.Characters;
import com.globalmentor.net.ContentType;

import static com.globalmentor.java.CharSequences.*;
import static com.globalmentor.java.Characters.*;
import static com.globalmentor.java.Objects.*;

/**
 * Constants and utilities for text.
 * @author Garret Wilson
 */
public class Text {

	/** The MIME subtype of <code>text/plain</code>. */
	public static final String PLAIN_SUBTYPE = "plain";

	/** The content type for plain text: <code>text/plain</code>. */
	public static final ContentType PLAIN_CONTENT_TYPE = ContentType.create(ContentType.TEXT_PRIMARY_TYPE, PLAIN_SUBTYPE);

	/** The name extension for text files. */
	public static final String TXT_NAME_EXTENSION = "txt";

	/**
	 * The string representing the CR EOL character sequence.
	 * @see Characters#CARRIAGE_RETURN_CHAR
	 */
	public static final String CARRIAGE_RETURN_STRING = new StringBuilder().append(CARRIAGE_RETURN_CHAR).toString();

	/**
	 * The string representing the LF EOL character sequence.
	 * @see Characters#LINE_FEED_CHAR
	 */
	public static final String LINE_FEED_STRING = new StringBuilder().append(LINE_FEED_CHAR).toString();

	/**
	 * The pattern that can split a line based upon linefeeds.
	 * @see Characters#LINE_FEED_CHAR
	 */
	public static final Pattern LINE_FEED_PATTERN = Pattern.compile(LINE_FEED_STRING);

	/**
	 * The string representing the CRLF EOL sequence.
	 * @see Characters#CARRIAGE_RETURN_CHAR
	 * @see Characters#LINE_FEED_CHAR
	 */
	public static final String CRLF_STRING = CARRIAGE_RETURN_STRING + LINE_FEED_STRING;

	/**
	 * Compares two strings for order in ascending order using the specified collator. Returns a negative integer, zero, or a positive integer as the first
	 * argument is less than, equal to, or greater than the second. Identical strings are always considered equal. This method functions exactly as if the two
	 * stringss were compared using {@link Collator#compare(String, String)}, except:
	 * <ul>
	 * <li>Identical strings are recognized as such without delegating to the actual {@link Collator#compare(String, String)} method.</li>
	 * <li>This method allows <code>null</code> arguments, considering a <code>null</code> string to be lower than a non-<code>null</code> string.</li>
	 * </ul>
	 * This method matches the semantics of {@link Collator#compare(String, String)}, except that this method allows <code>null</code> arguments.
	 * @param string1 The first string to be compared, or <code>null</code> if the string is not available.
	 * @param string2 The second string to be compared, or <code>null</code> if the string is not available.
	 * @param collator The collator used to perform comparisons.
	 * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
	 * @throws NullPointerException if the given collator is <code>null</code>.
	 * @throws ClassCastException if the arguments' types prevent them from being compared.
	 * @see Collator#compare(String, String)
	 */
	public static int compare(final String string1, final String string2, final Collator collator) {
		return compare(string1, string2, collator, SortOrder.ASCENDING); //compare in ascending order
	}

	/**
	 * Compares two strings for order using the specified collator with the specified sort order. Returns a negative integer, zero, or a positive integer as the
	 * first argument is less than, equal to, or greater than the second. Identical strings are always considered equal. This method functions exactly as if the
	 * two strings were compared using {@link Collator#compare(String, String)}, except:
	 * <ul>
	 * <li>Identical strings are recognized as such without delegating to the actual {@link Collator#compare(String, String)} method.</li>
	 * <li>This method allows <code>null</code> arguments, considering a <code>null</code> string to be lower than a non-<code>null</code> string.</li>
	 * </ul>
	 * @param string1 The first string to be compared, or <code>null</code> if the string is not available.
	 * @param string2 The second string to be compared, or <code>null</code> if the string is not available.
	 * @param collator The collator used to perform comparisons.
	 * @param sortOrder The order in which to perform comparisons.
	 * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
	 * @throws NullPointerException if the given collator and/or sort order is <code>null</code>.
	 * @throws ClassCastException if the arguments' types prevent them from being compared.
	 * @see Collator#compare(String, String)
	 */
	public static int compare(final String string1, final String string2, final Collator collator, final SortOrder sortOrder) {
		if(string1 == string2) { //if the strings are identical
			return 0; //identical strings are always equal
		}
		if(string1 != null) { //if the first string is not null
			if(string2 != null) { //if the second string is not null
				return sortOrder == SortOrder.ASCENDING ? collator.compare(string1, string2) : collator.compare(string2, string1); //compare in the requested order
			} else { //if only the first string is not null
				return sortOrder == SortOrder.ASCENDING ? 1 : -1; //null strings should be sorted lower
			}
		} else { //if the first string is null
			assert string2 != null : "Both strings cannot be null, because we already checked for identity.";
			return sortOrder == SortOrder.ASCENDING ? -1 : 1; //null strings should be sorted lower
		}
	}

	/**
	 * Creates a control string according to ECMA-48, "Control Functions for Coded Character Sets", Section 5.6, "Control strings". A control string begins with
	 * the Start of String control character (U+0098) and ends with a String Terminator control character (U+009C). ECMA-48 publication is also approved as
	 * ISO/IEC 6429.
	 * @param string The string from which a control string will be created.
	 * @return An ECMA-48 control string with the given string as its content.
	 * @throws NullPointerException if the given string is <code>null</code>.
	 * @see <a href="http://www.ecma-international.org/publications/standards/Ecma-048.htm">ECMA-48: Control Functions for Coded Character Sets</a>
	 */
	public static final String createControlString(final String string) {
		return START_OF_STRING_CHAR + checkInstance(string, "String cannot be null.") + STRING_TERMINATOR_CHAR; //wrap the string with a SOS/ST pair
	}

	/**
	 * Determines if the given content type is one representing text in some form.
	 * <p>
	 * Text media types include:
	 * </p>
	 * <ul>
	 * <li><code>text/*</code></li>
	 * <li><code>application/xml</code></li>
	 * <li><code>application/*+xml</code></li>
	 * <li><code>application/xml-external-parsed-entity</code></li>
	 * <li><code>application/*+xml-external-parsed-entity</code> (not formally defined)</li>
	 * </ul>
	 * @param contentType The content type of a resource, or <code>null</code> for no content type.
	 * @return <code>true</code> if the given content type is one of several text media types.
	 */
	public static boolean isText(final ContentType contentType) {
		if(contentType != null) { //if a content type is given
			if(ContentType.TEXT_PRIMARY_TYPE.equals(contentType.getPrimaryType())) { //if this is "text/*"
				return true; //text/* is a text content type
			}
			//TODO improve; see if removing this causes problems in Guise; application/xml could be considered non-text xml; see www.grauw.nl/blog/entry/489
			if(ContentType.APPLICATION_PRIMARY_TYPE.equals(contentType.getPrimaryType())) { //if this is "application/*"
				final String subType = contentType.getSubType(); //get the subtype
				if("xml".equals(subType) //see if the subtype is "xml"
						|| contentType.hasSubTypeSuffix("xml")) {	//see if the subtype has an XML suffix
					return true;	//application/*+xml is considered text
				}
				if("xml-external-parsed-entity".equals(subType) //if the subtype is /xml-external-parsed-entity
						|| contentType.hasSubTypeSuffix("xml-external-parsed-entity")) {	//or if the subtype has an XML external parsed entity suffix
					return true;	//application/*+xml-external-parsed-entity is considered text
				}
			}
		}
		return false; //this is not a media type we recognize as being HTML
	}

	/**
	 * Re-encodes the given string to the new encoding (such as UTF-8), assuming the string was encoded from an array of bytes using the old encoding (e.g.
	 * ISO-8859-1).
	 * @param string The string to recode.
	 * @param oldEncoding The encoding used to create the string originally.
	 * @param newEncoding The new encoding to use when creating the string.
	 * @return The a string created from encoding the characters in the specified new encoding.
	 * @throws UnsupportedEncodingException Thrown if either the old encoding or the new encoding is not supported.
	 */
	public static String recode(final String string, final String oldEncoding, final String newEncoding) throws UnsupportedEncodingException {
		final byte[] bytes = string.getBytes(oldEncoding); //get the bytes of the string as they were before they were encoded
		return new String(bytes, newEncoding); //create a string from the bytes using the new encoding
	}

	/**
	 * Escapes a given string by inserting an escape character before every restricted character, including any occurrence of the given escape character.
	 * @param charSequence The data to escape.
	 * @param restricted The characters to be escaped; should not include the escape character.
	 * @param escape The character used to escape the restricted characters.
	 * @return A string containing the escaped data.
	 * @throws NullPointerException if the given character sequence is <code>null</code>.
	 */
	public static String escape(final CharSequence charSequence, final Characters restricted, final char escape) { //TODO consolidate either in CharSequences or Formatter
		if(!contains(charSequence, restricted)) { //if there are no restricted characters in the string (assuming that most strings won't need to be escaped, it's less expensive to check up-front before we start allocating and copying)
			return charSequence.toString(); //the string doesn't need to be escaped
		}
		return escape(new StringBuilder(charSequence), restricted, escape).toString(); //make a string builder copy and escape its contents
	}

	/**
	 * Escapes a given string builder by inserting an escape character before every restricted character, including any occurrence of the given escape character.
	 * @param stringBuilder The data to escape.
	 * @param restricted The characters to be escaped; should not include the escape character.
	 * @param escape The character used to escape the restricted characters.
	 * @return A string containing the escaped data.
	 * @throws NullPointerException if the given string builder is <code>null</code>.
	 */
	public static StringBuilder escape(final StringBuilder stringBuilder, final Characters restricted, final char escape) {
		return escape(stringBuilder, restricted, escape, true);
	}

	/**
	 * Escapes a given string builder by inserting an escape character before every restricted character, optionally including any occurrence of the given escape
	 * character.
	 * @param stringBuilder The data to escape.
	 * @param restricted The characters to be escaped; should not include the escape character.
	 * @param escape The character used to escape the restricted characters.
	 * @param escapeEscape <code>true</code> if the escape character should also be escaped.
	 * @return A string containing the escaped data.
	 * @throws NullPointerException if the given string builder is <code>null</code>.
	 */
	public static StringBuilder escape(final StringBuilder stringBuilder, final Characters restricted, final char escape, final boolean escapeEscape) {
		for(int characterIndex = stringBuilder.length() - 1; characterIndex >= 0; --characterIndex) { //work backwards; this keeps us from having a separate variable for the length, but it also makes it simpler to calculate the next position when we swap out characters
			final char c = stringBuilder.charAt(characterIndex); //get the current character
			if((escapeEscape && c == escape) || restricted.contains(c)) { //if we should encode this character (always encode the escape character)
				stringBuilder.insert(characterIndex, escape); //insert the escape character
			}
		}
		return stringBuilder; //return the encoded version of the string
	}

	/**
	 * Normalizes end-of-line sequences in the character sequence to the given . The following sequences are normalized to the provided EOL:
	 * <ul>
	 * <li><code>CR</code></li>
	 * <li><code>LF</code></li>
	 * <li><code>CRLF</code></li>
	 * </ul>
	 * @param charSequence The character sequence to normalize.
	 * @param eol The end of line characters to which to normalize the ends of lines.
	 * @return A character sequence with the ends of lines normalized to the given end of line characters.
	 * @throws NullPointerException if the given character sequence and/or EOL characters is <code>null</code>.
	 */
	public static CharSequence normalizeEOL(final CharSequence charSequence, final CharSequence eol) {
		final int length = charSequence.length(); //get the length of the string
		int currentIndex = 0; //start searching from the beginning
		int resultIndex;
		StringBuilder stringBuilder = null; //don't create a string builder unless we need to
		while(currentIndex < length) { //keep searching until we finish the string
			resultIndex = indexOfLength(charSequence, EOL_CHARACTERS, currentIndex); //perform the next search
			if(stringBuilder == null) { //if we don't yet have a string builder
				if(resultIndex == length) { //if there are no characters in the entire character sequence
					break; //there's no need to modify the character sequence
				}
				stringBuilder = new StringBuilder(); //create a new string builder
			}
			stringBuilder.append(charSequence, currentIndex, resultIndex); //add the characters that aren't EOL characters
			stringBuilder.append(eol); //append the EOL sequence
			int skipEOLCount = 1; //assume we'll just skip one character
			if(resultIndex < length) { //if we aren't out of characters, yet
				final char eolChar = charSequence.charAt(resultIndex); //get the EOL character we found
				if(eolChar == CARRIAGE_RETURN_CHAR) { //if this is a CR, see if it is a CRLF
					final int nextIndex = resultIndex + 1; //get the index of the next character
					if(nextIndex < length && charSequence.charAt(nextIndex) == LINE_FEED_CHAR) { //if the next character is an LF
						++skipEOLCount; //skip the next character
					}
				}
			}
			currentIndex = resultIndex + skipEOLCount; //skip the EOL characters
		}
		return stringBuilder != null ? stringBuilder.toString() : charSequence; //return the string we constructed, or the character sequence if there were no EOL character
	}
}