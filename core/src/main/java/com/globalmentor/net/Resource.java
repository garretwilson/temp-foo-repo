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

package com.globalmentor.net;

import java.net.URI;

/**
 * Represents a generic resource with an identifying URI.
 * @author Garret Wilson
 */
public interface Resource {

	/** The Java property name of a resource's URI. */
	public static final String URI_PROPERTY_NAME = "uri";

	/**
	 * Returns the resource identifier URI, or <code>null</code> if the identifier is not known.
	 * @return The resource identifier URI, or <code>null</code> if the identifier is not known.
	 */
	public URI getURI();

}
