/*
 * Copyright © 2012 GlobalMentor, Inc. <https://www.globalmentor.com/>
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

/**
 * Represents some operation that can be executed. In essence this class is a lightweight task encapsulation. The operation can be canceled, which prevents its
 * execution if execution has not yet started. Operation execution is initiated by a call to {@link #run()}.
 * 
 * @author Garret Wilson
 */
public interface Operation extends Runnable {

	/** Cancels the operation. If called before execution has started, the operation will not execute. */
	public void cancel();

	/**
	 * Returns whether this operation has been canceled.
	 * @return Whether this operation has been canceled and should not execute.
	 */
	public boolean isCanceled();

}
