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

package com.globalmentor.collections;

import java.util.*;
import java.util.concurrent.locks.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.*;

/**
 * A thread-safe collection decorator that allows many readers but only one writer to access a collection at a time. For operations that iterate over live
 * collection data, a read or write lock should be acquired before the call to acquire the data and held until the data is consumed.
 * @param <E> The type of elements in the collection.
 * @author Garret Wilson
 */
public class DecoratorReadWriteLockCollection<E> extends ReadWriteLockDecorator implements ReadWriteLockCollection<E> {

	/** The collection this class decorates. */
	private final Collection<E> collection;

	/**
	 * Returns the collection this class decorates.
	 * @return The collection this class decorates.
	 */
	protected Collection<E> getCollection() {
		return collection;
	}

	/**
	 * Collection constructor with a default reentrant read/write lock.
	 * @param collection The collection this collection should decorate.
	 * @throws NullPointerException if the provided collection is <code>null</code>.
	 */
	public DecoratorReadWriteLockCollection(final Collection<E> collection) {
		this(collection, new ReentrantReadWriteLock()); //create the collection with a default lock
	}

	/**
	 * Collection and read/write lock constructor.
	 * @param collection The collection this collection should decorate.
	 * @param lock The lock for controlling access to the collection.
	 * @throws NullPointerException if the provided collection and/or lock is <code>null</code>.
	 */
	public DecoratorReadWriteLockCollection(final Collection<E> collection, final ReadWriteLock lock) {
		super(lock); //construct the parent class
		this.collection = requireNonNull(collection, "Collection cannot be null"); //save the collection
	}

	@Override
	public int size() {
		readLock().lock();
		try {
			return getCollection().size();
		} finally {
			readLock().unlock();
		}
	}

	@Override
	public boolean isEmpty() {
		readLock().lock();
		try {
			return getCollection().isEmpty();
		} finally {
			readLock().unlock();
		}
	}

	@Override
	public boolean contains(Object o) {
		readLock().lock();
		try {
			return getCollection().contains(o);
		} finally {
			readLock().unlock();
		}
	}

	@Override
	public Iterator<E> iterator() {
		readLock().lock();
		try {
			return getCollection().iterator();
		} finally {
			readLock().unlock();
		}
	}

	@Override
	public Object[] toArray() {
		readLock().lock();
		try {
			return getCollection().toArray();
		} finally {
			readLock().unlock();
		}
	}

	@Override
	public <T> T[] toArray(T[] a) {
		readLock().lock();
		try {
			return getCollection().toArray(a);
		} finally {
			readLock().unlock();
		}
	}

	// Modification Operations

	@Override
	public boolean add(E o) {
		writeLock().lock();
		try {
			return getCollection().add(o);
		} finally {
			writeLock().unlock();
		}
	}

	@Override
	public boolean remove(Object o) {
		writeLock().lock();
		try {
			return getCollection().remove(o);
		} finally {
			writeLock().unlock();
		}
	}

	// Bulk Operations

	@Override
	public boolean containsAll(Collection<?> c) {
		readLock().lock();
		try {
			return getCollection().containsAll(c);
		} finally {
			readLock().unlock();
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		writeLock().lock();
		try {
			return getCollection().addAll(c);
		} finally {
			writeLock().unlock();
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		writeLock().lock();
		try {
			return getCollection().removeAll(c);
		} finally {
			writeLock().unlock();
		}
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		writeLock().lock();
		try {
			return collection.removeIf(filter);
		} finally {
			writeLock().unlock();
		}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		writeLock().lock();
		try {
			return getCollection().retainAll(c);
		} finally {
			writeLock().unlock();
		}
	}

	@Override
	public void clear() {
		writeLock().lock();
		try {
			getCollection().clear();
		} finally {
			writeLock().unlock();
		}
	}

	// Comparison and hashing

	@Override
	public boolean equals(Object o) {
		readLock().lock();
		try {
			return getCollection().equals(o);
		} finally {
			readLock().unlock();
		}
	}

	@Override
	public int hashCode() {
		readLock().lock();
		try {
			return getCollection().hashCode();
		} finally {
			readLock().unlock();
		}
	}

	@Override
	public Spliterator<E> spliterator() {
		readLock().lock();
		try {
			return getCollection().spliterator();
		} finally {
			readLock().unlock();
		}
	}

	@Override
	public Stream<E> stream() {
		readLock().lock();
		try {
			return getCollection().stream();
		} finally {
			readLock().unlock();
		}
	}

	@Override
	public Stream<E> parallelStream() {
		readLock().lock();
		try {
			return getCollection().parallelStream();
		} finally {
			readLock().unlock();
		}
	}
}
