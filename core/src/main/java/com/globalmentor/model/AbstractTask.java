/*
 * Copyright © 2008 GlobalMentor, Inc. <https://www.globalmentor.com/>
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

package com.globalmentor.model;

import static java.util.Objects.*;

import com.globalmentor.beans.BoundPropertyObject;
import com.globalmentor.event.EventListenerManager;

/**
 * Abstract implementation of a task performed in a sequence.
 * @author Garret Wilson
 */
public abstract class AbstractTask extends BoundPropertyObject implements Task {

	/** Constructor. */
	protected AbstractTask() {
	}

	/** The object managing event listeners. */
	private final EventListenerManager eventListenerManager = new EventListenerManager();

	/**
	 * Returns the object managing event listeners.
	 * @return The object managing event listeners.
	 */
	protected EventListenerManager getEventListenerManager() {
		return eventListenerManager;
	}

	/** The current state of the task. */
	private TaskState state = TaskState.UNSTARTED;

	/** @return The current state of the task. */
	public TaskState getState() {
		return state;
	}

	/**
	 * Sets the current state of the task. This is a bound property.
	 * @param newState The new state of the task.
	 * @throws NullPointerException if the given state is <code>null</code>.
	 * @see Task#STATE_PROPERTY
	 */
	public void setState(final TaskState newState) {
		if(state != requireNonNull(newState, "State cannot be null.")) { //if the value is really changing
			final TaskState oldState = state; //get the current value
			state = newState; //update the value
			firePropertyChange(STATE_PROPERTY, oldState, newState);
		}
	}

}
