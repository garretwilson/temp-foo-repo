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

package com.globalmentor.io;

import java.io.File;

/**
 * A filter that selects files based upon whether they are directories.
 * 
 * @author Garret Wilson
 */
public class DirectoryFileFilter extends AbstractFileFilter {

	/** <code>true</code> if only directories will be accepted, else <code>false</code> for only files. */
	private final boolean acceptDirectoryStatus;

	/**
	 * Indicates whether only directories or only files will be accepted.
	 * @return <code>true</code> if only directories will be accepted, else <code>false</code> for only files.
	 */
	protected boolean getAcceptDirectoryStatus() {
		return acceptDirectoryStatus;
	}

	/** Default constructor, accepting only directories. */
	public DirectoryFileFilter() {
		this(true);
	}

	/**
	 * Directory status constructor.
	 * 
	 * @param acceptDirectoryStatus <code>true</code> if only directories will be accepted, else <code>false</code> for only files.
	 */
	public DirectoryFileFilter(final boolean acceptDirectoryStatus) {
		this.acceptDirectoryStatus = acceptDirectoryStatus;
	}

	/**
	 * {@inheritDoc} This version only accepts files based upon whether their directory status matches the status requested.
	 * @see File#isDirectory()
	 * @see #getAcceptDirectoryStatus()
	 */
	@Override
	public boolean accept(final File pathname) {
		return pathname.isDirectory() == getAcceptDirectoryStatus();
	}
}
