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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.j2cl.junit.apt.J2clTestInput;
import java.nio.ReadOnlyBufferException;
import org.junit.Before;
import org.junit.Test;

@J2clTestInput(ReadOnlyHeapByteBufferTest.class)
public class ReadOnlyHeapByteBufferTest extends HeapByteBufferTest {

  @Before
  public void gwtSetUp() {
    super.gwtSetUp();
    buf = buf.asReadOnlyBuffer();
    baseBuf = buf;
  }

  @Test
  public void testIsReadOnly() {
    assertTrue(buf.isReadOnly());
  }

  @Test
  public void testArrayOffset() {
    try {
      buf.arrayOffset();
      fail("Should throw ReadOnlyBufferException"); // $NON-NLS-1$
    } catch (ReadOnlyBufferException e) {
      // expected
    }
  }

  @Test
  public void testHasArray() {
    assertFalse(buf.hasArray());
    try {
      buf.array();
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (ReadOnlyBufferException e) {
      // expected
    }
  }

  @Test
  public void testHashCode() {
    super.readOnlyHashCode(false);
  }

  @Test
  public void testArray() {
    try {
      buf.array();
      fail("Should throw ReadOnlyBufferException"); // $NON-NLS-1$
    } catch (ReadOnlyBufferException e) {
      // expected
    }
  }
}
