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
import java.util.EventObject;

/**
 * An event related to a resource.
 * @implNote The resources referenced by the event are not serialized.
 * @author Garret Wilson
 */
public class ResourceEvent extends EventObject {

	private static final long serialVersionUID = 2L;

	/** The old parent resource reference URI, or <code>null</code> if not applicable. */
	private final URI oldParentResourceURI;

	/**
	 * Returns the old parent resource reference URI.
	 * @return The old parent resource reference URI, or <code>null</code> if not applicable.
	 */
	public URI getOldParentResourceURI() {
		return oldParentResourceURI;
	}

	/** A description of the old parent resource, or <code>null</code> if not applicable. */
	private transient final Resource oldParentResource;

	/**
	 * Returns a description of the old parent resource.
	 * @return A description of the old parent resource, or <code>null</code> if not applicable.
	 */
	public Resource getOldParentResource() {
		return oldParentResource;
	}

	/** The parent resource reference URI, or <code>null</code> if not applicable. */
	private final URI parentResourceURI;

	/**
	 * Returns the parent resource reference URI.
	 * @return The parent resource reference URI, or <code>null</code> if not applicable.
	 */
	public URI getParentResourceURI() {
		return parentResourceURI;
	}

	/** A description of the parent resource, or <code>null</code> if not applicable. */
	private transient final Resource parentResource;

	/**
	 * Returns a description of the parent resource.
	 * @return A description of the parent resource, or <code>null</code> if not applicable.
	 */
	public Resource getParentResource() {
		return parentResource;
	}

	/** The previous resource reference URI, or <code>null</code> if not applicable. */
	private final URI oldResourceURI;

	/**
	 * Returns the previous resource reference URI.
	 * @return The previous resource reference URI, or <code>null</code> if not applicable.
	 */
	public URI getOldResourceURI() {
		return oldResourceURI;
	}

	/** A description of the old resource, or <code>null</code> if not applicable. */
	private transient final Resource oldResource;

	/**
	 * Returns a description of the old resource.
	 * @return A description of the old resource, or <code>null</code> if not applicable.
	 */
	public Resource getOldResource() {
		return oldResource;
	}

	/** The resource reference URI, or <code>null</code> if not applicable. */
	private final URI resourceURI;

	/**
	 * Returns the resource reference URI.
	 * @return The resource reference URI, or <code>null</code> if not applicable.
	 */
	public URI getResourceURI() {
		return resourceURI;
	}

	/** A description of the resource, or <code>null</code> if there is no description. */
	private transient final Resource resource;

	/**
	 * Returns a description of the resource.
	 * @return A description of the resource, or <code>null</code> if there is no description.
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * Constructor that specifies the resource reference URI.
	 * @param source The object on which the event initially occurred.
	 * @param resourceURI The reference URI of the resource.
	 */
	public ResourceEvent(final Object source, final URI resourceURI) {
		this(source, null, resourceURI); //construct the class with no old resource URI
	}

	/**
	 * Constructor that specifies both an old and a current resource reference URI.
	 * @param source The object on which the event initially occurred.
	 * @param oldResourceURI The old reference URI of the resource.
	 * @param newResourceURI The new reference URI of the resource.
	 */
	public ResourceEvent(final Object source, final URI oldResourceURI, final URI newResourceURI) {
		this(source, null, null, oldResourceURI, null, null, null, newResourceURI, null); //construct the class with old and new URIs
	}

	/**
	 * Constructor that specifies the resource description.
	 * @param source The object on which the event initially occurred.
	 * @param resource A description of the resource, or <code>null</code> if there is no description.
	 */
	public ResourceEvent(final Object source, final Resource resource) {
		this(source, null, resource); //construct the class with no parent resource
	}

	/**
	 * Constructor that specifies the parent and resource description.
	 * @param source The object on which the event initially occurred.
	 * @param parentResource A description of the parent resource, or <code>null</code> if there is no parent resource.
	 * @param resource A description of the resource, or <code>null</code> if there is no description.
	 */
	public ResourceEvent(final Object source, final Resource parentResource, final Resource resource) {
		this(source, null, parentResource, resource); //construct the class with no old resource information
	}

	/**
	 * Constructor that specifies the parent and resource description, as well as an old resource URI.
	 * @param source The object on which the event initially occurred.
	 * @param oldResourceURI The old reference URI of the resource, or <code>null</code> if there is no old resource reference URI.
	 * @param parentResource A description of the parent resource, or <code>null</code> if there is no parent resource.
	 * @param resource A description of the resource, or <code>null</code> if there is no description.
	 */
	public ResourceEvent(final Object source, final URI oldResourceURI, final Resource parentResource, final Resource resource) {
		this(source, null, oldResourceURI, parentResource, resource); //construct the class with no old parent resource information
	}

	/**
	 * Constructor that specifies the old and new parent resource descriptions, the new resource description and the old resource reference URI.
	 * @param source The object on which the event initially occurred.
	 * @param oldParentResource A description of the old parent resource, or <code>null</code> if there is no old parent resource.
	 * @param oldResourceURI The old reference URI of the resource, or <code>null</code> if there is no old resource reference URI.
	 * @param parentResource A description of the parent resource, or <code>null</code> if there is no parent resource.
	 * @param resource A description of the resource, or <code>null</code> if there is no description.
	 */
	public ResourceEvent(final Object source, final Resource oldParentResource, final URI oldResourceURI, final Resource parentResource,
			final Resource resource) {
		this(source, oldParentResource != null ? oldParentResource.getURI() : null, oldParentResource, oldResourceURI, null,
				parentResource != null ? parentResource.getURI() : null, parentResource, resource.getURI(), resource); //construct the class with a resource and a reference URI, along with the old parent and the old reference URI
	}

	/**
	 * Constructor that specifies parent, old, and current resources.
	 * @param source The object on which the event initially occurred.
	 * @param oldParentResourceURI The reference URI of the old parent resource, or <code>null</code> if there is no old parent resource reference URI.
	 * @param oldParentResource The old parent resource, or <code>null</code> if there is no old parent resource.
	 * @param oldResourceURI The old reference URI of the resource, or <code>null</code> if there is no old resource reference URI.
	 * @param oldResource The old resource, or <code>null</code> if there is no old resource.
	 * @param parentResourceURI The reference URI of the parent resource, or <code>null</code> if there is no parent resource reference URI.
	 * @param parentResource The parent resource, or <code>null</code> if there is no parent resource.
	 * @param resourceURI The current reference URI of the resource.
	 * @param resource The current resource, or <code>null</code> if there is no resource.
	 */
	protected ResourceEvent(final Object source, final URI oldParentResourceURI, final Resource oldParentResource, final URI oldResourceURI,
			final Resource oldResource, final URI parentResourceURI, final Resource parentResource, final URI resourceURI, final Resource resource) {
		super(source); //construct the parent class
		this.oldParentResourceURI = oldParentResourceURI; //save the old parent resource URI
		this.oldParentResource = oldParentResource; //save the old parent resource
		this.oldResourceURI = oldResourceURI; //save the old resource URI
		this.oldResource = oldResource; //save the old resource
		this.parentResourceURI = parentResourceURI; //save the parent resource URI
		this.parentResource = parentResource; //save the parent resource
		this.resourceURI = resourceURI; //save the resource URI
		this.resource = resource; //save the resource description
	}

	/** @return A string representation of this resource event. */
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder(super.toString()).append(' ').append('[');
		stringBuilder.append(getOldParentResourceURI()).append('/').append(getOldResourceURI()).append(',').append(' '); //oldParentResourceURI/oldResourceURI
		stringBuilder.append(getParentResourceURI()).append('/').append(getResourceURI()).append(']'); //oldResourceURI/resourceURI
		return stringBuilder.toString(); //return the string we constructed
	}

}
