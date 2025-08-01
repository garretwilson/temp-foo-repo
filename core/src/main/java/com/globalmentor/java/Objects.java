/*
 * Copyright © 1996-2013 GlobalMentor, Inc. <https://www.globalmentor.com/>
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

package com.globalmentor.java;

import java.lang.reflect.*;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.*;

import static java.util.Objects.*;

import static com.globalmentor.java.Classes.*;
import static com.globalmentor.java.Conditions.*;
import static com.globalmentor.java.Java.*;

/**
 * Various utilities to manipulate Java objects.
 * @author Garret Wilson
 */
public class Objects {

	/** A shared object array that contains no elements. */
	public static final Object[] NO_OBJECTS = new Object[0];

	/** This class cannot be publicly instantiated. */
	private Objects() {
	}

	/**
	 * Checks to see if a given variable is of the correct type and if not, throws a <code>ClassCastException</code>.
	 * @param <T> The type of variable to check.
	 * @param variable The variable to check, or <code>null</code>.
	 * @param type The type to verify.
	 * @return The given variable.
	 * @throws ClassCastException if the given variable is not <code>null</code> and not an instance of type <var>type</var>.
	 */
	public static <T> T checkType(final Object variable, final Class<T> type) {
		return checkType(variable, type, null); //check for type with no description
	}

	/**
	 * Checks to see if a given variable is of the correct type and if not, throws a <code>ClassCastException</code>.
	 * @param <T> The type of variable to check.
	 * @param variable The variable to check, or <code>null</code>.
	 * @param type The type to verify.
	 * @param description A description of the variable to be used when generating an exception, or <code>null</code> for no description.
	 * @return The given variable.
	 * @throws ClassCastException if the given variable is not <code>null</code> and not an instance of type <var>type</var>.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T checkType(final Object variable, final Class<T> type, final String description) {
		if(variable != null && !type.isInstance(variable)) { //if the variable is not null but is of a different type
			throw new ClassCastException(description);
		}
		return (T)variable; //return the variable
	}

	/**
	 * Clones an object that supports cloning.
	 * @param <T> The type of the object.
	 * @param object The object that supports cloning through use of the {@link CloneSupported} interface.
	 * @return The cloned object.
	 * @throws IllegalStateException if the object's {@link CloneSupported#clone()} method throws a {@link CloneNotSupportedException}.
	 * @see CloneSupported#clone()
	 */
	@SuppressWarnings("unchecked")
	public static <T extends CloneSupported> T clone(final T object) {
		try {
			return (T)object.clone();
		} catch(final CloneNotSupportedException cloneNotSupportedException) {
			throw unexpected(cloneNotSupportedException);
		}
	}

	/**
	 * Compares two objects for order, taking into account <code>null</code>. If both objects are <code>null</code> they are considered equal. If only one object
	 * is <code>null</code>, comparison will be performed based upon whether <code>null</code> is considered higher or lower. Otherwise, the second object is
	 * compared to the first using the first object's {@link Comparable#compareTo(Object)} method.
	 * @param <T> The type of the object.
	 * @param object1 The first object to be compared.
	 * @param object2 The second object to be compared.
	 * @param nullBias A negative or positive integer indicating if <code>null</code> should be considered less than or greater than non-<code>null</code>
	 *          objects.
	 * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 */
	public static <T extends Comparable<? super T>> int compare(final T object1, final T object2, final int nullBias) {
		if(object1 == null) { //if the first object is null
			return object2 == null ? 0 : nullBias; //if both objects are null, they are considered equal; otherwise, send back whatever null should be
		} else { //if the first object is not null
			return object2 != null ? object1.compareTo(object2) : -nullBias; //if both objects are non-null, they can be compared; otherwise, the second object is the only one null, and gets the negation of the null bias
		}
	}

	/**
	 * Convenience method that returns the given object if and only if it is an instance of the given class. This method is equivalent to
	 * <code><var>object</var> instanceof <var>Type</var> ? Optional.of((Type)object) : Optional.empty();</code>.
	 * @param <T> The type of object given.
	 * @param <I> The type of object instance to check for.
	 * @param object The object to examine.
	 * @param instanceClass The class of which the object may be an instance.
	 * @return The object if it is an instance of the given class.
	 * @see #asInstance(Class)
	 */
	public static <T, I extends T> Optional<I> asInstance(final T object, final Class<I> instanceClass) {
		return instanceClass.isInstance(object) ? Optional.of(instanceClass.cast(object)) : Optional.<I>empty();
	}

	/**
	 * Convenience method that returns a function that casts some object to a given class if and only if it is an instance of that class.
	 * @apiNote This method may be used to cast an {@link Optional} value using {@code optional.flatMap(asInstance(instanceClass)}. In this way it is equivalent
	 *          to {@code optional.filter(instanceClass::isInstance).map(instanceClass::cast)}.
	 * @implSpec This implementation returns a function that delegates to {@link #asInstance(Object, Class)}.
	 * @param <T> The type of object given.
	 * @param <I> The type of object instance to check for.
	 * @param instanceClass The class of which the object may be an instance.
	 * @return The a function that returns the object if it is an instance of the given class; otherwise an empty {@link Optional}.
	 * @see #asInstance(Object, Class)
	 */
	public static <T, I extends T> Function<T, Optional<I>> asInstance(final Class<I> instanceClass) {
		return object -> asInstance(object, instanceClass);
	}

	/**
	 * Convenience method that returns a function that casts some object to a given class if and only if it is an instance of that class, returning a potentially
	 * empty stream.
	 * @apiNote This method may be used to cast {@link Stream} values using {@code stream.flatMap(asInstances(instanceClass)}. In this way it is equivalent to
	 *          {@code stream.filter(instanceClass::isInstance).map(instanceClass::cast)}.
	 * @implSpec This implementation returns a function that delegates to {@link #asInstance(Object, Class)}.
	 * @param <T> The type of object given.
	 * @param <I> The type of object instance to check for.
	 * @param instanceClass The class of which the object may be an instance.
	 * @return The a function that returns the object if it is an instance of the given class; otherwise an empty {@link Stream}.
	 * @see #asInstance(Object, Class)
	 */
	public static <T, I extends T> Function<T, Stream<I>> asInstances(final Class<I> instanceClass) {
		return object -> asInstance(object, instanceClass).stream();
	}

	/**
	 * Returns the first object that is an instance of {@link Object} (i.e. that is not <code>null</code>).
	 * @param <T> The type of the objects.
	 * @param objects The objects to investigate.
	 * @return The first object that is not <code>null</code>, or <code>null</code> if all objects are <code>null</code>.
	 */
	@SafeVarargs
	public static <T> T getInstance(final T... objects) {
		for(final T object : objects) { //look at all the references
			if(object != null) { //if the object isn't null (it is faster to just check for null rather than to delegate to the class-based getInstance() version
				return object; //return the object
			}
		}
		return null; //we couldn't find such an instance
	}

	/**
	 * Returns the first object that is an instance of the given object type.
	 * @param <T> The type of the objects.
	 * @param <C> The subtype of the given objects.
	 * @param objectClass The class of the type of object to return
	 * @param objects The objects to investigate.
	 * @return The first object that is the instance of the given type, or <code>null</code> if no object is an instance of the indicated type.
	 * @throws NullPointerException if the given object class is <code>null</code>.
	 */
	@SafeVarargs
	public static <T, C extends T> C getInstance(final Class<C> objectClass, final T... objects) {
		for(final T object : objects) { //look at all the references
			if(objectClass.isInstance(object)) { //if this object is an instance of the given class
				return objectClass.cast(object); //cast and return the object
			}
		}
		return null; //we couldn't find such an instance
	}

	/**
	 * Returns either and object or a default instance if the object is <code>null</code>.
	 * <p>
	 * This is equivalent to the JavaScript statement <code>var x = object || defaultInstance;</code>
	 * </p>
	 * @param <T> The type of the object.
	 * @param object The object to examine.
	 * @param defaultInstance The default instance to return if the object is <code>null</code>.
	 * @return The object, or the default instance of the object is <code>null</code>.
	 * @throws NullPointerException if the given default instance is <code>null</code>.
	 */
	public static <T> T toInstance(final T object, final T defaultInstance) {
		return object != null ? object : requireNonNull(defaultInstance);
	}

	/**
	 * Returns the property of an object based upon a given property name. A property getter in the form "get<var>PropertyName</var>" takes precedence over a
	 * property getter in the form "is<var>PropertyName</var>" having a {@link Boolean#TYPE}.
	 * @param object The object the property of which to retrieve.
	 * @param propertyName The name of the property to retrieve.
	 * @return The value of the given property.
	 * @throws NullPointerException if the given object is <code>null</code>.
	 * @throws NoSuchMethodException if the given object has no method with the name "get<var>PropertyName</var>", or the name "is<var>PropertyName</var>" having
	 *           a {@link Boolean#TYPE}.
	 * @throws IllegalAccessException if the getter method enforces Java language access control and the getter method is inaccessible.
	 * @throws InvocationTargetException if the getter method throws an exception.
	 * @throws ExceptionInInitializerError if the initialization provoked by the getter method fails.
	 * @deprecated To be removed in favor of a separate library.
	 */
	@Deprecated(forRemoval = true)
	public static Object getProperty(final Object object, final String propertyName)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		final Method getterMethod = getGetterMethod(object.getClass(), propertyName); //get the getter property, if there is one
		if(getterMethod != null) { //if there is a getter method
			return getterMethod.invoke(object); //invoke the getter method and return the value
		} else { //if there is no getter method
			throw new NoSuchMethodException("Object with class " + object.getClass() + " has no getter method for property " + propertyName);
		}
	}

	/**
	 * Determines of which, if any, of the provided classes the given object is an instance.
	 * @param object The object to check as an instance; may be <code>null</code>.
	 * @param classes The classes of which to check the given object being an instance.
	 * @return The first of the listed classes of which the given object is an instance, or <code>null</code> if the object is not an instance of any of the
	 *         listed classes.
	 */
	public static Class<?> getInstanceOf(final Object object, final Class<?>... classes) {
		for(final Class<?> objectClass : classes) {
			if(objectClass.isInstance(object)) {
				return objectClass;
			}
		}
		return null;
	}

	/**
	 * Determines if the object is an instance of any of the provided classes.
	 * @param object The object to check as an instance; may be <code>null</code>.
	 * @param classes The classes of which to check the given object being an instance.
	 * @return <code>true</code> if the object is an instance of one of the listed classes.
	 */
	public static boolean isInstanceOf(final Object object, final Class<?>... classes) {
		return getInstanceOf(object, classes) != null;
	}

	/**
	 * Checks to see if the elements are instances of any object, and throws a {@link NullPointerException} if any element is <code>null</code>.
	 * @param objects The objects of which to check.
	 * @return The given objects.
	 * @throws NullPointerException if any of the given objects are <code>null</code>.
	 */
	public static Object[] requireNonNulls(final Object... objects) {
		return Arrays.requireNonNulls(objects); //check for null with no description
	}

	/**
	 * Converts an object to an {@link AutoCloseable} instance so that it can be used with try-with-resources.
	 * @apiNote The {@link Exception} thrown by the {@link AutoCloseable} interface may be too broad; the {@link com.globalmentor.io.IO#toCloseable(Object)}
	 *          method converts to an interface with more limited exceptions allowed.
	 * @implSpec If the given object is an instance of {@link AutoCloseable}, the object itself is returned; otherwise, a no-operation {@link AutoCloseable}
	 *           instance is returned.
	 * @param object The object to convert to an {@link AutoCloseable}.
	 * @return An {@link AutoCloseable} instance that will ensure the object is closed if it implements {@link AutoCloseable}.
	 * @see com.globalmentor.io.IO#toCloseable(Object)
	 */
	public static AutoCloseable toAutoCloseable(@Nonnull final Object object) {
		if(object instanceof AutoCloseable) {
			return (AutoCloseable)object;
		}
		requireNonNull(object); //if the object was auto-closeable above, we didn't need the null check
		return () -> { //no-op
		};
	}

	/**
	 * Returns the string representation of the object or {@value Java#NULL_KEYWORD}.
	 * @param object An object to be represented by a string.
	 * @return The string representation of the object or {@value Java#NULL_KEYWORD} if the object is <code>null</code>.
	 */
	public static final String toString(final Object object) {
		return object != null ? object.toString() : NULL_KEYWORD; //return the object's string representation or "null"
	}

}
