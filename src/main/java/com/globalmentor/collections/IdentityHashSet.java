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

package com.globalmentor.collections;

import java.util.*;

/**
 * A set that is backed by an identity hash map.
 * <p>
 * <strong>This class is <em>not</em> a general-purpose {@link Set} implementation! While this class implements the {@link Set} interface, it intentionally
 * violates {@link Set}'s general contract, which mandates the use of the {@link Object#equals(Object)} method when comparing objects. This class is designed
 * for use only in the rare cases wherein reference-equality semantics are required.</strong>
 * </p>
 * <p>
 * This class is based upon {@link HashSet} 1.28, 01/23/03 by Josh Bloch, Copyright 2003 Sun Microsystems, Inc.
 * </p>
 * @author Garret Wilson
 * @see java.util.HashSet
 * @see java.util.IdentityHashMap
 */
public class IdentityHashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable {

	private transient IdentityHashMap<E, Object> map;

	// Dummy value to associate with an Object in the backing Map
	private static final Object PRESENT = new Object();

	/**
	 * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has default initial capacity (16) and load factor (0.75).
	 */
	public IdentityHashSet() {
		map = new IdentityHashMap<E, Object>();
	}

	/**
	 * Constructs a new set containing the elements in the specified collection. The <tt>HashMap</tt> is created with default load factor (0.75) and an initial
	 * capacity sufficient to contain the elements in the specified collection.
	 *
	 * @param c the collection whose elements are to be placed into this set.
	 * @throws NullPointerException if the specified collection is null.
	 */
	public IdentityHashSet(Collection<E> c) {
		map = new IdentityHashMap<E, Object>(Math.max((int)(c.size() / .75f) + 1, 16));
		addAll(c);
	}

	/**
	 * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has the specified initial capacity and default load factor, which is <tt>0.75</tt>.
	 *
	 * @param initialCapacity the initial capacity of the hash table.
	 * @throws IllegalArgumentException if the initial capacity is less than zero.
	 */
	public IdentityHashSet(int initialCapacity) {
		map = new IdentityHashMap<E, Object>(initialCapacity);
	}

	/**
	 * Returns an iterator over the elements in this set. The elements are returned in no particular order.
	 *
	 * @return an Iterator over the elements in this set.
	 * @see ConcurrentModificationException
	 */
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	/**
	 * Returns the number of elements in this set (its cardinality).
	 *
	 * @return the number of elements in this set (its cardinality).
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Returns <tt>true</tt> if this set contains no elements.
	 *
	 * @return <tt>true</tt> if this set contains no elements.
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this set contains the specified element.
	 *
	 * @param o element whose presence in this set is to be tested.
	 * @return <tt>true</tt> if this set contains the specified element.
	 */
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	/**
	 * Adds the specified element to this set if it is not already present.
	 *
	 * @param o element to be added to this set.
	 * @return <tt>true</tt> if the set did not already contain the specified element.
	 */
	public boolean add(E o) {
		return map.put(o, PRESENT) == null;
	}

	/**
	 * Removes the specified element from this set if it is present.
	 *
	 * @param o object to be removed from this set, if present.
	 * @return <tt>true</tt> if the set contained the specified element.
	 */
	public boolean remove(Object o) {
		return map.remove(o) == PRESENT;
	}

	/**
	 * Removes all of the elements from this set.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * Returns a shallow copy of this <tt>HashSet</tt> instance: the elements themselves are not cloned.
	 *
	 * @return a shallow copy of this set.
	 */
	public Object clone() {
		try {
			IdentityHashSet<E> newSet = (IdentityHashSet<E>)super.clone();
			newSet.map = (IdentityHashMap<E, Object>)map.clone();
			return newSet;
		} catch(CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
}
