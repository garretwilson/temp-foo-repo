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

package com.globalmentor.iso;

/**
 * ISO 639 Language Codes These codes are the shortest available of ISO 639-1 and ISO 639-2 codes, as outlined in
 * <a href="http://www.ietf.org/rfc/rfc4646.txt">RFC 4646: "Tags for the Identifying Languages"</a>.
 * @author Garret Wilson
 * @see <a href="http://www.loc.gov/standards/iso639-2/">ISO 639-2</a>
 * @see <a href="http://www.ietf.org/rfc/rfc4646.txt">RFC 4646</a>
 */
public final class ISO639 {

	private ISO639() {
	}

	/** Arabic */
	public static final String ARABIC_CODE = "ar";
	/** Farsi */
	public static final String FARSI_CODE = "fa";
	/** Hebrew */
	public static final String HEBREW_CODE = "he";
	/** The obsolete code for Hebrew, used by Java. */
	public static final String HEBREW_OBSOLETE_CODE = "iw";
	/** Urdu */
	public static final String URDU_CODE = "ur";

}
