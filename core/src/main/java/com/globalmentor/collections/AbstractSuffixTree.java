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

import java.util.*;

import static com.globalmentor.java.Objects.*;
import static java.util.Objects.*;

/**
 * An abstract base implementation of a suffix tree for a sequence of elements (most commonly characters).
 * @param <E> The type of suffix tree edge.
 * @author Garret Wilson
 */
public abstract class AbstractSuffixTree<E extends SuffixTree.Edge> implements SuffixTree {

	/** Whether the suffix tree is explicit, with every suffix ending on a leaf node. */
	private final boolean explicit;

	/** @return Whether the suffix tree is explicit, with every suffix ending on a leaf node. */
	public boolean isExplicit() {
		return explicit;
	}

	/** The nodes in the suffix tree. */
	private final List<Node> nodes = new ArrayList<Node>();

	/** The bit set that keeps track of which nodes are leaf nodes. */
	private final BitSet leafNodeIndexes = new BitSet();

	@Override
	public Iterable<Node> getNodes() {
		return java.util.Collections.unmodifiableList(nodes);
	}

	@Override
	public int getNodeCount() {
		return nodes.size();
	}

	@Override
	public Node getRootNode() {
		return getNode(0);
	}

	@Override
	public Node getNode(final int nodeIndex) {
		return nodes.get(nodeIndex);
	}

	/**
	 * Creates a new node and adds it to the internal list of nodes. This implementation delegates to {@link #createNode(int)}.
	 * @return The newly created node.
	 */
	protected final AbstractNode addNode() {
		final int nodeIndex = nodes.size();
		final AbstractNode node = createNode(nodeIndex);
		nodes.add(nodeIndex, node);
		return node;
	}

	/**
	 * Creates a new node. By default a node is considered a leaf node.
	 * @param index The index of the node to create.
	 * @return The index of the newly created node.
	 */
	protected abstract AbstractNode createNode(final int index);

	/**
	 * Creates a new edge and adds it to the tree. The given parent node will be set to be a branch node, and the given child node will have its parent node set
	 * to the given parent node.
	 * <p>
	 * This method delegates to {@link #createEdge(SuffixTree.Node, SuffixTree.Node, int, int)}.
	 * </p>
	 * @param parentNode The parent node representing the root end of the edge.
	 * @param childNode The child node representing the leaf end of the edge.
	 * @param start The position of the start element, inclusive.
	 * @param end The position of the end element, exclusive.
	 * @return The tree after it receives a new edge.
	 * @throws NullPointerException if the given parent node and/or child node is <code>null</code>.
	 * @throws IllegalArgumentException if the given end is less than the start.
	 * @throws IllegalStateException if there already exists an edge with the same parent node and first element.
	 * @throws ClassCastException if the given parent node is not an {@link AbstractNode}.
	 * @see AbstractNode#setParentNode(SuffixTree.Node)
	 * @see AbstractNode#setLeaf(boolean)
	 */
	protected final E addEdge(final Node parentNode, final Node childNode, final int start, final int end) {
		final E edge = createEdge(parentNode, childNode, start, end); //create a new edge
		addEdge(edge); //add the edge
		asInstance(parentNode, AbstractNode.class).orElseThrow(IllegalStateException::new).setLeaf(false); //TODO improve generics
		asInstance(childNode, AbstractNode.class).orElseThrow(IllegalStateException::new).setParentNode(parentNode); //TODO improve generics
		return edge; //return the edge
	}

	/**
	 * Creates a new edge.
	 * @param parentNode The parent node representing the root end of the edge.
	 * @param childNode The child node representing the leaf end of the edge.
	 * @param start The position of the start element, inclusive.
	 * @param end The position of the end element, exclusive.
	 * @return The tree after it receives a new edge.
	 * @throws NullPointerException if the given parent node and/or child node is <code>null</code>.
	 * @throws IllegalArgumentException if the given end is less than the start.
	 * @throws IllegalStateException if there already exists an edge with the same parent node and first element.
	 */
	protected abstract E createEdge(final Node parentNode, final Node childNode, final int start, final int end);

	/**
	 * Adds an edge to the tree.
	 * @param edge The edge to add.
	 * @throws NullPointerException if the given edge is <code>null</code>.
	 * @throws IllegalStateException if there already exists an edge with the same parent node and first element.
	 */
	protected abstract void addEdge(final E edge);

	/**
	 * Removes an edge from the tree. If the edge does not exist, no action occurs.
	 * @param edge The edge to remove.
	 * @throws NullPointerException if the given edge is <code>null</code>.
	 */
	protected abstract void removeEdge(final E edge);

	/**
	 * Splits an edge into two. The first, near edge will be of the given length; the second, far edge will be of the remaining length (that is, the length of the
	 * original edge minus the given length). A new node will be created as the mid-point between the original edge nodes, becoming the child node of the first
	 * edge and the parent node of the second edge.
	 * @param edge The edge to split.
	 * @param length The position at which to split the edge.
	 * @return The created node splitting the edge.
	 */
	protected AbstractNode splitEdge(final E edge, final int length) {
		removeEdge(edge); //remove the existing edge from our map, because we're going to create two edges to replace it
		final AbstractNode newNode = addNode(); //create a new node with which to split the edge into two
		final int split = edge.getStart() + length; //the element location at which to make the split
		addEdge(edge.getParentNode(), newNode, edge.getStart(), split); //the near edge will start at the same place as the old edge, but only go part way---to the new node
		addEdge(newNode, edge.getChildNode(), split, edge.getEnd()); //the far edge starts where the new near edge ends
		return newNode; //return the index of the new node
	}

	/**
	 * Constructor.
	 * @param explicit Whether the suffix tree is explicit, with every suffix ending on a leaf node.
	 */
	@SuppressWarnings("this-escape")
	protected AbstractSuffixTree(final boolean explicit) {
		this.explicit = explicit;
		addNode(); //create the root node
	}

	/**
	 * Represents a node in a suffix tree. Each node defaults to having no suffix node.
	 * 
	 * @author Garret Wilson
	 */
	protected abstract class AbstractNode implements SuffixTree.Node {

		private final int index;

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public boolean isLeaf() {
			return leafNodeIndexes.get(getIndex());
		}

		/**
		 * Sets whether this node is a leaf.
		 * @param leaf Whether this node is a leaf.
		 */
		protected void setLeaf(final boolean leaf) {
			leafNodeIndexes.set(getIndex(), leaf);
		}

		private Node parentNode = null;

		@Override
		public Node getParentNode() {
			return parentNode;
		}

		/**
		 * Sets the node indicating this node's parent.
		 * @param parentNode The node representing the parent of this node.
		 * @throws NullPointerException if the given node is <code>null</code>.
		 */
		protected void setParentNode(final Node parentNode) {
			this.parentNode = requireNonNull(parentNode);
		}

		private Node suffixNode = null;

		@Override
		public Node getSuffixNode() {
			return suffixNode;
		}

		/**
		 * Sets the node representing the next smaller suffix.
		 * @param suffixNode The node representing the next smaller suffix.
		 * @throws NullPointerException if the given node is <code>null</code>.
		 */
		protected void setSuffixNode(final Node suffixNode) {
			this.suffixNode = requireNonNull(suffixNode);
		}

		/**
		 * Index constructor. The node defaults to being a leaf node.
		 * @param index The index of the node.
		 */
		public AbstractNode(final int index) {
			this.index = index;
			setLeaf(true); //default to being a leaf node
		}

		/**
		 * {@inheritDoc} This version returns the node index.
		 * @see #getIndex()
		 */
		@Override
		public int hashCode() {
			return getIndex();
		}

		/**
		 * {@inheritDoc} This version compares node indexes.
		 * @see #getIndex()
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) {
				return true;
			}
			if(!(object instanceof Node)) {
				return false;
			}
			return getIndex() == ((Node)object).getIndex();
		}

		/** {@inheritDoc} This implementation returns a string in the form <code>(<var>index</var>)*</code>, where '*' indicates a leaf node. */
		@Override
		public String toString() {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append('(').append(getIndex()).append(')');
			if(isLeaf()) { //add a leaf designation if appropriate
				stringBuilder.append('*');
			}
			return stringBuilder.toString();
		}

	};

}
