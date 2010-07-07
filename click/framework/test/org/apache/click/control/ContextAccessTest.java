/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.control;

import junit.framework.TestCase;

/**
 * Test performance of accessing a ThreadLocal object vs accessing a normal
 * object directly.
 */
public class ContextAccessTest extends TestCase {

  /** Number of times to lookup ThreadLocal context. */
	private static final int ITERATIONS = 10000000;
	
  /** Normal Object to lookup. */
	private Object object;
	
  /** ThreadLocal holder of Object to lookup. */
	private static ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();
	
  /**
   * Test performance of looking up an Object directly.
   */
	public void testDirect() {
		long start = System.currentTimeMillis();
		
    // Count is used to ensure JVM does not remove loop away entirely.
		int count = 0;
		for (int i = 0; i < ITERATIONS; i++) {
			Object objectA = getDirectObject();
			if (objectA != null) {
				count += 1;
			}
		}
		
		System.out.println("testDirect time:" + (System.currentTimeMillis() - start));
	}
	
  /**
   * Test performance of looking up an Object using a ThreadLocal.
   */
	public void testThreadLocal() {
		long start = System.currentTimeMillis();
		
    // Count is used to ensure JVM does not remove loop away entirely.
		int count = 0;
		for (int i = 0; i < ITERATIONS; i++) {
			Object objectA = getThreadLocalObject();
			if (objectA != null) {
				count += 1;
			}
		}

		System.out.println("testThreadLocal time:" + (System.currentTimeMillis() - start) + " ms");
	}

  /**
   * Return the Object directly.
   * 
   * @return direct object
   */
	public Object getDirectObject() {
		if (object == null) {
			object = new Object();
		}
		return object;
	}

  /**
   * Return object looked up through ThreadLocal.
   *
   * @return object looked up through ThreadLocal
   */
	public Object getThreadLocalObject() {
		Object bObject = threadLocal.get();
		if (bObject == null) {
			bObject = new Object();
			threadLocal.set(bObject);
		}
		return bObject;
	}
}
