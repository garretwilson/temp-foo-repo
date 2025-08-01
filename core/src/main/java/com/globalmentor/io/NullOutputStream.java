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

package com.globalmentor.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream that throws away its data.
 * @author Garret Wilson
 */
public class NullOutputStream extends OutputStream {

	/** Constructor. */
	public NullOutputStream() {
	}

	/**
	 * Writes the specified byte to this output stream. This version does nothing.
	 * @param b The byte to write.
	 * @throws IOException if an I/O error occurs.
	 */
	public void write(final int b) throws IOException {
	}

}
