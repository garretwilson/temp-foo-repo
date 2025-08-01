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

package com.globalmentor.security;

import java.util.Date;

/**
 * A generated value to prevent playback attacks in communication protocols. This interface forces several restrictions on a nonce, allowing recovery of several
 * information components.
 * @author Garret Wilson
 */
public interface Nonce {

	/**
	 * Returns the time represented by the nonce.
	 * @return The time represented by the nonce.
	 */
	public Date getTime();

	/**
	 * Returns the private key represented by the nonce.
	 * @return The private key represented by the nonce.
	 */
	public String getPrivateKey();

	/** @return A string representation of the nonce, suitable for serialization. */
	public String toString();

}
