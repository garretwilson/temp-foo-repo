/*
 * Copyright © 2011 GlobalMentor, Inc. <https://www.globalmentor.com/>
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

package com.globalmentor.collections;

/**
 * A general interface for a suffix tree for a sequence of elements (most commonly characters).
 * 
 * @author Garret Wilson
 */
public interface SuffixTree {

	/**
	 * Returns whether the suffix tree is explicit, with every suffix ending on a leaf node.
	 * @return Whether the suffix tree is explicit, with every suffix ending on a leaf node.
	 */
	public boolean isExplicit();

	/**
	 * Returns a read-only iterable of the nodes in the tree.
	 * @return A read-only iterable of the nodes in the tree.
	 */
	public Iterable<? extends Node> getNodes();

	/**
	 * Returns the number of nodes in the suffix tree.
	 * @return The number of nodes in the suffix tree.
	 */
	public int getNodeCount();

	/**
	 * Retrieves the root node of the tree. This is a convenience method to retrieve the node with index zero.
	 * @return The identified node.
	 */
	public Node getRootNode();

	/**
	 * Retrieves the identified node.
	 * @param nodeIndex The index of the node to retrieve.
	 * @return The identified node.
	 * @throws IndexOutOfBoundsException if the given node index does not identify a node in this suffix tree.
	 */
	public Node getNode(final int nodeIndex);

	/**
	 * Returns a read-only iterable of edges in the tree.
	 * @return A read-only iterable of edges in the tree.
	 */
	public Iterable<? extends Edge> getEdges();

	/**
	 * Represents a node in a suffix tree. Each node defaults to having no suffix node.
	 * 
	 * @author Garret Wilson
	 */
	public interface Node {

		/**
		 * Returns the index of the node.
		 * @return The index of the node.
		 */
		public int getIndex();

		/**
		 * Returns whether this node is a leaf node in the suffix tree.
		 * @return Whether this node is a leaf node in the suffix tree.
		 */
		public boolean isLeaf();

		/**
		 * Returns the parent node of this node.
		 * @return The parent node of this node, or <code>null</code> if this node has no parent node (i.e. it is the root node).
		 */
		public Node getParentNode();

		/**
		 * Returns the node representing the next smaller suffix.
		 * @return The node representing the next smaller suffix, or <code>null</code> if there is no known smaller suffix node.
		 */
		public Node getSuffixNode();

		/**
		 * Returns an iterable to the child edges of this node.
		 * @return An iterable to the child edges of this node.
		 */
		public Iterable<? extends Edge> getChildEdges();

	};

	/**
	 * Represents an edge between a parent node and a child node in a suffix tree. Some edges may be empty.
	 * 
	 * @author Garret Wilson
	 */
	public interface Edge {

		/**
		 * Returns the parent node representing the root end of the edge.
		 * @return The parent node representing the root end of the edge.
		 */
		public Node getParentNode();

		/**
		 * Returns the child node representing the leaf end of the edge.
		 * @return The child node representing the leaf end of the edge.
		 */
		public Node getChildNode();

		/**
		 * Returns the position of the start element, inclusive.
		 * @return The position of the start element, inclusive.
		 */
		public int getStart();

		/**
		 * Returns the position of the last element, exclusive.
		 * @return The position of the last element, exclusive.
		 */
		public int getEnd();

		/**
		 * Returns the length of the edge, i.e. <code><var>end</var>-<var>start</var></code>.
		 * @return The number of elements on the edge.
		 */
		public int getLength();

		/**
		 * Indicates whether this edge is empty and has no elements.
		 * @return <code>true</code> if this edge is empty and has no elements.
		 */
		public boolean isEmpty();

		/**
		 * Returns an iterable to the child edges of this edge's child node.
		 * @return An iterable to the child edges of this edge's child node.
		 */
		public Iterable<? extends Edge> getChildEdges();

	};

}
