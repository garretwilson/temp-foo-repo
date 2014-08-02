package com.globalmentor.collections;

import java.util.*;

import static com.globalmentor.java.Objects.*;

/**A map that wraps an existing map, providing access through the {@link Map} interface.
@param <K> The type of map key.
@param <V> The type of map value.
@author Garret Wilson
*/
public class MapDecorator<K, V> implements Map<K, V>
{

	/**The map this class decorates.*/
	protected final Map<K, V> map;

	/**Map constructor.
	@param map The map this map should decorate.
	@throws NullPointerException if the provided map is <code>null</code>.
	*/
	public MapDecorator(final Map<K, V> map)
	{
		this.map=checkInstance(map, "Map cannot be null");	//save the map
	}

  /**
   * Returns the number of key-value mappings in this map.  If the
   * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
   * <tt>Integer.MAX_VALUE</tt>.
   *
   * @return the number of key-value mappings in this map.
   */
  public int size() {return map.size();}

  /**
   * Returns <tt>true</tt> if this map contains no key-value mappings.
   *
   * @return <tt>true</tt> if this map contains no key-value mappings.
   */
  public boolean isEmpty() {return map.isEmpty();}

  /**
   * Returns <tt>true</tt> if this map contains a mapping for the specified
   * key.  More formally, returns <tt>true</tt> if and only if
   * this map contains a mapping for a key <tt>k</tt> such that
   * <tt>(key==null ? k==null : key.equals(k))</tt>.  (There can be
   * at most one such mapping.)
   *
   * @param key key whose presence in this map is to be tested.
   * @return <tt>true</tt> if this map contains a mapping for the specified
   *         key.
   * 
   * @throws ClassCastException if the key is of an inappropriate type for
   * 		  this map (optional).
   * @throws NullPointerException if the key is <tt>null</tt> and this map
   *            does not permit <tt>null</tt> keys (optional).
   */
  public boolean containsKey(Object key) {return map.containsKey(key);}

  /**
   * Returns <tt>true</tt> if this map maps one or more keys to the
   * specified value.  More formally, returns <tt>true</tt> if and only if
   * this map contains at least one mapping to a value <tt>v</tt> such that
   * <tt>(value==null ? v==null : value.equals(v))</tt>.  This operation
   * will probably require time linear in the map size for most
   * implementations of the <tt>Map</tt> interface.
   *
   * @param value value whose presence in this map is to be tested.
   * @return <tt>true</tt> if this map maps one or more keys to the
   *         specified value.
   * @throws ClassCastException if the value is of an inappropriate type for
   * 		  this map (optional).
   * @throws NullPointerException if the value is <tt>null</tt> and this map
   *            does not permit <tt>null</tt> values (optional).
   */
  public boolean containsValue(Object value) {return map.containsValue(value);}

  /**
   * Returns the value to which this map maps the specified key.  Returns
   * <tt>null</tt> if the map contains no mapping for this key.  A return
   * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
   * map contains no mapping for the key; it's also possible that the map
   * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
   * operation may be used to distinguish these two cases.
   *
   * <p>More formally, if this map contains a mapping from a key
   * <tt>k</tt> to a value <tt>v</tt> such that <tt>(key==null ? k==null :
   * key.equals(k))</tt>, then this method returns <tt>v</tt>; otherwise
   * it returns <tt>null</tt>.  (There can be at most one such mapping.)
   *
   * @param key key whose associated value is to be returned.
   * @return the value to which this map maps the specified key, or
   *	       <tt>null</tt> if the map contains no mapping for this key.
   * 
   * @throws ClassCastException if the key is of an inappropriate type for
   * 		  this map (optional).
   * @throws NullPointerException if the key is <tt>null</tt> and this map
   *		  does not permit <tt>null</tt> keys (optional).
   * 
   * @see #containsKey(Object)
   */
  public V get(Object key) {return map.get(key);}

  // Modification Operations

  /**
   * Associates the specified value with the specified key in this map
   * (optional operation).  If the map previously contained a mapping for
   * this key, the old value is replaced by the specified value.  (A map
   * <tt>m</tt> is said to contain a mapping for a key <tt>k</tt> if and only
   * if {@link #containsKey(Object) m.containsKey(k)} would return
   * <tt>true</tt>.)) 
   *
   * @param key key with which the specified value is to be associated.
   * @param value value to be associated with the specified key.
   * @return previous value associated with specified key, or <tt>null</tt>
   *	       if there was no mapping for key.  A <tt>null</tt> return can
   *	       also indicate that the map previously associated <tt>null</tt>
   *	       with the specified key, if the implementation supports
   *	       <tt>null</tt> values.
   * 
   * @throws UnsupportedOperationException if the <tt>put</tt> operation is
   *	          not supported by this map.
   * @throws ClassCastException if the class of the specified key or value
   * 	          prevents it from being stored in this map.
   * @throws IllegalArgumentException if some aspect of this key or value
   *	          prevents it from being stored in this map.
   * @throws NullPointerException if this map does not permit <tt>null</tt>
   *            keys or values, and the specified key or value is
   *            <tt>null</tt>.
   */
  public V put(K key, V value) {return map.put(key, value);}

  /**
   * Removes the mapping for this key from this map if it is present
   * (optional operation).   More formally, if this map contains a mapping
   * from key <tt>k</tt> to value <tt>v</tt> such that
   * <code>(key==null ?  k==null : key.equals(k))</code>, that mapping
   * is removed.  (The map can contain at most one such mapping.)
   *
   * <p>Returns the value to which the map previously associated the key, or
   * <tt>null</tt> if the map contained no mapping for this key.  (A
   * <tt>null</tt> return can also indicate that the map previously
   * associated <tt>null</tt> with the specified key if the implementation
   * supports <tt>null</tt> values.)  The map will not contain a mapping for
   * the specified  key once the call returns.
   *
   * @param key key whose mapping is to be removed from the map.
   * @return previous value associated with specified key, or <tt>null</tt>
   *	       if there was no mapping for key.
   *
   * @throws ClassCastException if the key is of an inappropriate type for
   * 		  this map (optional).
   * @throws NullPointerException if the key is <tt>null</tt> and this map
   *            does not permit <tt>null</tt> keys (optional).
   * @throws UnsupportedOperationException if the <tt>remove</tt> method is
   *         not supported by this map.
   */
  public V remove(Object key) {return map.remove(key);}


  // Bulk Operations

  /**
   * Copies all of the mappings from the specified map to this map
   * (optional operation).  The effect of this call is equivalent to that
   * of calling {@link #put(Object,Object) put(k, v)} on this map once
   * for each mapping from key <tt>k</tt> to value <tt>v</tt> in the 
   * specified map.  The behavior of this operation is unspecified if the
   * specified map is modified while the operation is in progress.
   *
   * @param t Mappings to be stored in this map.
   * 
   * @throws UnsupportedOperationException if the <tt>putAll</tt> method is
   * 		  not supported by this map.
   * 
   * @throws ClassCastException if the class of a key or value in the
   * 	          specified map prevents it from being stored in this map.
   * 
   * @throws IllegalArgumentException some aspect of a key or value in the
   *	          specified map prevents it from being stored in this map.
   * @throws NullPointerException if the specified map is <tt>null</tt>, or if
   *         this map does not permit <tt>null</tt> keys or values, and the
   *         specified map contains <tt>null</tt> keys or values.
   */
  public void putAll(Map<? extends K, ? extends V> t) {map.putAll(t);}

  /**
   * Removes all mappings from this map (optional operation).
   *
   * @throws UnsupportedOperationException clear is not supported by this
   * 		  map.
   */
  public void clear() {map.clear();}


  // Views

  /**
   * Returns a set view of the keys contained in this map.  The set is
   * backed by the map, so changes to the map are reflected in the set, and
   * vice-versa.  If the map is modified while an iteration over the set is
   * in progress (except through the iterator's own <tt>remove</tt>
   * operation), the results of the iteration are undefined.  The set
   * supports element removal, which removes the corresponding mapping from
   * the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
   * <tt>removeAll</tt> <tt>retainAll</tt>, and <tt>clear</tt> operations.
   * It does not support the add or <tt>addAll</tt> operations.
   *
   * @return a set view of the keys contained in this map.
   */
  public Set<K> keySet() {return map.keySet();}

  /**
   * Returns a collection view of the values contained in this map.  The
   * collection is backed by the map, so changes to the map are reflected in
   * the collection, and vice-versa.  If the map is modified while an
   * iteration over the collection is in progress (except through the
   * iterator's own <tt>remove</tt> operation), the results of the
   * iteration are undefined.  The collection supports element removal,
   * which removes the corresponding mapping from the map, via the
   * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
   * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations.
   * It does not support the add or <tt>addAll</tt> operations.
   *
   * @return a collection view of the values contained in this map.
   */
  public Collection<V> values() {return map.values();}

  /**
   * Returns a set view of the mappings contained in this map.  Each element
   * in the returned set is a {@link Map.Entry}.  The set is backed by the
   * map, so changes to the map are reflected in the set, and vice-versa.
   * If the map is modified while an iteration over the set is in progress
   * (except through the iterator's own <tt>remove</tt> operation, or through
   * the <tt>setValue</tt> operation on a map entry returned by the iterator)
   * the results of the iteration are undefined.  The set supports element
   * removal, which removes the corresponding mapping from the map, via the
   * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
   * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not support
   * the <tt>add</tt> or <tt>addAll</tt> operations.
   *
   * @return a set view of the mappings contained in this map.
   */
  public Set<Map.Entry<K, V>> entrySet() {return map.entrySet();}

	// Comparison and hashing

	/**
	 * Compares the specified object with this collection for equality. <p>
	 *
	 * While the <tt>Collection</tt> interface adds no stipulations to the
	 * general contract for the <tt>Object.equals</tt>, programmers who
	 * implement the <tt>Collection</tt> interface "directly" (in other words,
	 * create a class that is a <tt>Collection</tt> but is not a <tt>Set</tt>
	 * or a <tt>List</tt>) must exercise care if they choose to override the
	 * <tt>Object.equals</tt>.  It is not necessary to do so, and the simplest
	 * course of action is to rely on <tt>Object</tt>'s implementation, but
	 * the implementer may wish to implement a "value comparison" in place of
	 * the default "reference comparison."  (The <tt>List</tt> and
	 * <tt>Set</tt> interfaces mandate such value comparisons.)<p>
	 *
	 * The general contract for the <tt>Object.equals</tt> method states that
	 * equals must be symmetric (in other words, <tt>a.equals(b)</tt> if and
	 * only if <tt>b.equals(a)</tt>).  The contracts for <tt>List.equals</tt>
	 * and <tt>Set.equals</tt> state that lists are only equal to other lists,
	 * and sets to other sets.  Thus, a custom <tt>equals</tt> method for a
	 * collection class that implements neither the <tt>List</tt> nor
	 * <tt>Set</tt> interface must return <tt>false</tt> when this collection
	 * is compared to any list or set.  (By the same logic, it is not possible
	 * to write a class that correctly implements both the <tt>Set</tt> and
	 * <tt>List</tt> interfaces.)
	 *
	 * @param o Object to be compared for equality with this collection.
	 * @return <tt>true</tt> if the specified object is equal to this
	 * collection
	 * 
	 * @see Object#equals(Object)
	 * @see Set#equals(Object)
	 * @see List#equals(Object)
	 */
	public boolean equals(Object o) {return map.equals(o);}	

	/**
	 * Returns the hash code value for this collection.  While the
	 * <tt>Collection</tt> interface adds no stipulations to the general
	 * contract for the <tt>Object.hashCode</tt> method, programmers should
	 * take note that any class that overrides the <tt>Object.equals</tt>
	 * method must also override the <tt>Object.hashCode</tt> method in order
	 * to satisfy the general contract for the <tt>Object.hashCode</tt>method.
	 * In particular, <tt>c1.equals(c2)</tt> implies that
	 * <tt>c1.hashCode()==c2.hashCode()</tt>.
	 *
	 * @return the hash code value for this collection
	 * 
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 */
	public int hashCode() {return map.hashCode();}
}
