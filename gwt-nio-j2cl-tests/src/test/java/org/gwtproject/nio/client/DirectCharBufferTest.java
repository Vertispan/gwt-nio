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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.j2cl.junit.apt.J2clTestInput;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.junit.Before;
import org.junit.Test;

@J2clTestInput(DirectCharBufferTest.class)
public class DirectCharBufferTest extends CharBufferTest {

  @Before
  public void gwtSetUp() {
    super.gwtSetUp();
    capacity = BUFFER_LENGTH;
    buf = ByteBuffer.allocateDirect(BUFFER_LENGTH * 2).asCharBuffer();
    loadTestData1(buf);
    baseBuf = buf;
  }

  public void gwtTearDown() {
    buf = null;
    baseBuf = null;
  }

  @Test
  public void testArray() {
    try {
      buf.array();
      fail("Should throw UnsupportedOperationException"); // $NON-NLS-1$
    } catch (UnsupportedOperationException e) {
    }
  }

  @Test
  public void testArrayOffset() {
    try {
      buf.arrayOffset();
      fail("Should throw UnsupportedOperationException"); // $NON-NLS-1$
    } catch (UnsupportedOperationException e) {
    }
  }

  @Test
  public void testIsDirect() {
    assertTrue(buf.isDirect());
  }

  @Test
  public void testHasArray() {
    assertFalse(buf.hasArray());
  }

  @Test
  public void testOrder() {
    assertEquals(ByteOrder.BIG_ENDIAN, buf.order());
  }
}
