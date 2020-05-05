/* Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gwtproject.nio.client;

import java.nio.BufferUnderflowException;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests for BufferUnderflowException
 */
public class BufferUnderflowExceptionTest extends GWTTestCase {

    /**
     *@tests {@link java.nio.BufferUnderflowException#BufferUnderflowException()}
     */
    public void test_Constructor() {
        BufferUnderflowException exception = new BufferUnderflowException();
        assertNull(exception.getMessage());
        assertNull(exception.getLocalizedMessage());
        assertNull(exception.getCause());
    }

    @Override
    public String getModuleName() {
        return "org.gwtproject.nio.NIOTest";
    }
}
