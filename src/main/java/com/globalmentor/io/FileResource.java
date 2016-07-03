/*
 * Copyright © 1996-2008 GlobalMentor, Inc. <http://www.globalmentor.com/>
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

package com.globalmentor.io;

import java.io.File;
import java.net.URI;
import java.util.List;

import static java.util.Collections.*;
import static java.util.Objects.*;

import com.globalmentor.net.DefaultResource;

/**
 * A resource accessible by a file. The file may or may not have the same URI as the resource.
 * @author Garret Wilson
 */
public class FileResource extends DefaultResource {

	/** The file this resource represents. */
	private final File file;

	/** @return The file this resource represents. */
	public File getFile() {
		return file;
	}

	/**
	 * Constructs a resource with a file. The reference URI will be set to the reference URI of the file.
	 * @param file The file this resource represents.
	 * @throws NullPointerException if the given file or reference URI is <code>null</code>.
	 */
	public FileResource(final File file) {
		this(file, Files.toURI(file)); //construct the resource using the file's URI
	}

	/**
	 * Constructs a resource with a file and a reference URI.
	 * @param file The file this resource represents.
	 * @param referenceURI The reference URI for the new resource.
	 * @throws NullPointerException if the given file is <code>null</code>.
	 */
	public FileResource(final File file, final URI referenceURI) {
		super(referenceURI); //construct the parent class
		this.file = requireNonNull(file, "File cannot be null."); //save the file
	}

	/**
	 * Retrieves an list of child resources of this resource.
	 * @return A list of child resources.
	 */
	public List<FileResource> getChildResources() {
		return emptyList(); //TODO implement
	}

}
