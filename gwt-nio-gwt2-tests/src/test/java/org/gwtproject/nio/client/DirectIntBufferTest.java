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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class DirectIntBufferTest extends IntBufferTest {
  public void gwtSetUp() {
    capacity = BUFFER_LENGTH;
    buf = ByteBuffer.allocateDirect(BUFFER_LENGTH * 4).asIntBuffer();
    loadTestData1(buf);
    baseBuf = buf;
  }

  public void gwtTearDown() {
    buf = null;
    baseBuf = null;
  }

  public void testPutWhenOffsetIsNonZero() {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(40);
    byteBuffer.order(ByteOrder.nativeOrder());
    IntBuffer intBuffer = byteBuffer.asIntBuffer();

    int[] source = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

    intBuffer.put(source, 2, 2);
    intBuffer.put(source, 4, 2);
    assertEquals(4, intBuffer.get(0));
    assertEquals(5, intBuffer.get(1));
    assertEquals(6, intBuffer.get(2));
    assertEquals(7, intBuffer.get(3));
  }

  public void testHasArray() {
    assertFalse(buf.hasArray());
  }

  public void testArray() {
    try {
      buf.array();
      fail("Should throw UnsupportedOperationException"); // $NON-NLS-1$
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testArrayOffset() {
    try {
      buf.arrayOffset();
      fail("Should throw UnsupportedOperationException"); // $NON-NLS-1$
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  public void testIsDirect() {
    assertTrue(buf.isDirect());
  }

  public void testOrder() {
    assertEquals(ByteOrder.BIG_ENDIAN, buf.order());
  }

  public void testRangeChecks() {
    int[] myInts = new int[BUFFER_LENGTH];

    for (int i = 0; i < BUFFER_LENGTH; i++) {
      myInts[i] = 1000 + i;
    }

    buf.position(0);
    buf.put(myInts, 0, BUFFER_LENGTH);
    buf.position(0);
    buf.put(myInts, 0, BUFFER_LENGTH);

    try {
      buf.put(myInts, 0, 1); // should fail
      fail("BufferOverflowException expected but not thrown");
    } catch (BufferOverflowException boe) {
      // expected
    }

    try {
      buf.position(0);
      buf.put(myInts, 0, BUFFER_LENGTH + 1); // should fail
      fail("BufferOverflowException expected but not thrown");
    } catch (IndexOutOfBoundsException ioobe) {
      // expected
    }

    try {
      buf.position(BUFFER_LENGTH - 1);
      buf.put(myInts, 0, 2); // should fail
      fail("BufferOverflowException expected but not thrown");
    } catch (BufferOverflowException boe) {
      // expected
    }
  }
}
