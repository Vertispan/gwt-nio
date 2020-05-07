/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.gwtproject.nio.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.j2cl.junit.apt.J2clTestInput;
import elemental2.dom.DomGlobal;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.InvalidMarkException;
import org.junit.Before;
import org.junit.Test;

/** Tests java.nio.FloatBuffer */
@J2clTestInput(FloatBufferTest.class)
public class FloatBufferTest extends AbstractBufferTest {

  protected static final int SMALL_TEST_LENGTH = 5;

  protected static final int BUFFER_LENGTH = 20;

  protected FloatBuffer buf;

  @Before
  public void gwtSetUp() {
    capacity = BUFFER_LENGTH;
    buf = FloatBuffer.allocate(BUFFER_LENGTH);
    loadTestData1(buf);
    baseBuf = buf;
  }

  public void gwtTearDown() {
    buf = null;
    baseBuf = null;
  }

  @Test
  public void test_AllocateI() {
    // case: FloatBuffer testBuf properties is satisfy the conditions
    // specification
    FloatBuffer testBuf = FloatBuffer.allocate(20);
    assertEquals(0, testBuf.position());
    assertNotNull(testBuf.array());
    assertEquals(0, testBuf.arrayOffset());
    assertEquals(20, testBuf.limit());
    assertEquals(20, testBuf.capacity());

    testBuf = FloatBuffer.allocate(0);
    assertEquals(0, testBuf.position());
    assertNotNull(testBuf.array());
    assertEquals(0, testBuf.arrayOffset());
    assertEquals(0, testBuf.limit());
    assertEquals(0, testBuf.capacity());

    // case: expected IllegalArgumentException
    try {
      testBuf = FloatBuffer.allocate(-20);
      fail("allocate method does not throws expected exception");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testArray() {
    float array[] = buf.array();
    assertContentEquals(buf, array, buf.arrayOffset(), buf.capacity());

    loadTestData1(array, buf.arrayOffset(), buf.capacity());
    assertContentEquals(buf, array, buf.arrayOffset(), buf.capacity());

    loadTestData2(array, buf.arrayOffset(), buf.capacity());
    assertContentEquals(buf, array, buf.arrayOffset(), buf.capacity());

    loadTestData1(buf);
    assertContentEquals(buf, array, buf.arrayOffset(), buf.capacity());

    loadTestData2(buf);
    assertContentEquals(buf, array, buf.arrayOffset(), buf.capacity());
  }

  @Test
  public void testArrayOffset() {
    float array[] = buf.array();
    for (int i = 0; i < buf.capacity(); i++) {
      array[i] = i;
    }
    int offset = buf.arrayOffset();
    assertContentEquals(buf, array, offset, buf.capacity());

    FloatBuffer wrapped = FloatBuffer.wrap(array, 3, array.length - 3);

    loadTestData1(array, wrapped.arrayOffset(), wrapped.capacity());
    assertContentEquals(buf, array, offset, buf.capacity());

    loadTestData2(array, wrapped.arrayOffset(), wrapped.capacity());
    assertContentEquals(buf, array, offset, buf.capacity());
  }

  @Test
  public void testAsReadOnlyBuffer() {
    buf.clear();
    buf.mark();
    buf.position(buf.limit());

    // readonly's contents should be the same as buf
    FloatBuffer readonly = buf.asReadOnlyBuffer();
    assertNotSame(buf, readonly);
    assertTrue(readonly.isReadOnly());
    assertEquals(buf.position(), readonly.position());
    assertEquals(buf.limit(), readonly.limit());
    assertEquals(buf.isDirect(), readonly.isDirect());
    assertEquals(buf.order(), readonly.order());
    assertContentEquals(buf, readonly);

    // readonly's position, mark, and limit should be independent to buf
    readonly.reset();
    assertEquals(readonly.position(), 0);
    readonly.clear();
    assertEquals(buf.position(), buf.limit());
    buf.reset();
    assertEquals(buf.position(), 0);
  }

  @Test
  public void testCompact() {

    // case: buffer is full
    buf.clear();
    buf.mark();
    loadTestData1(buf);
    FloatBuffer ret = buf.compact();
    assertSame(ret, buf);
    assertEquals(buf.position(), buf.capacity());
    assertEquals(buf.limit(), buf.capacity());
    assertContentLikeTestData1(buf, 0, 0.0f, buf.capacity());
    try {
      buf.reset();
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (InvalidMarkException e) {
      // expected
    }

    // case: buffer is empty
    buf.position(0);
    buf.limit(0);
    buf.mark();
    ret = buf.compact();
    assertSame(ret, buf);
    assertEquals(buf.position(), 0);
    assertEquals(buf.limit(), buf.capacity());
    assertContentLikeTestData1(buf, 0, 0.0f, buf.capacity());
    try {
      // Fails on RI. Spec doesn't specify the behavior if
      // actually nothing to be done by compact(). So RI doesn't reset
      // mark position
      buf.reset();
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (InvalidMarkException e) {
      // expected
    }

    // case: normal
    assertTrue(buf.capacity() > 5);
    buf.position(1);
    buf.limit(5);
    buf.mark();
    ret = buf.compact();
    assertSame(ret, buf);
    assertEquals(buf.position(), 4);
    assertEquals(buf.limit(), buf.capacity());
    assertContentLikeTestData1(buf, 0, 1.0f, 4);
    try {
      buf.reset();
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (InvalidMarkException e) {
      // expected
    }
  }

  @Test
  public void testCompareTo() {

    // compare to self
    assertEquals(0, buf.compareTo(buf));

    // normal cases
    assertTrue(buf.capacity() > 5);
    buf.clear();
    FloatBuffer other = FloatBuffer.allocate(buf.capacity());
    loadTestData1(other);
    assertEquals(0, buf.compareTo(other));
    assertEquals(0, other.compareTo(buf));
    buf.position(1);
    assertTrue(buf.compareTo(other) > 0);
    assertTrue(other.compareTo(buf) < 0);
    other.position(2);
    assertTrue(buf.compareTo(other) < 0);
    assertTrue(other.compareTo(buf) > 0);
    buf.position(2);
    other.limit(5);
    assertTrue(buf.compareTo(other) > 0);
    assertTrue(other.compareTo(buf) < 0);

    // BEGIN android-added
    // copied from a newer version of Harmony
    FloatBuffer fbuffer1 = FloatBuffer.wrap(new float[] {Float.NaN});
    FloatBuffer fbuffer2 = FloatBuffer.wrap(new float[] {Float.NaN});
    FloatBuffer fbuffer3 = FloatBuffer.wrap(new float[] {42f});

    assertEquals("Failed equal comparison with NaN entry", 0, fbuffer1.compareTo(fbuffer2));
    assertEquals("Failed greater than comparison with NaN entry", 1, fbuffer3.compareTo(fbuffer1));
    assertEquals("Failed greater than comparison with NaN entry", 1, fbuffer1.compareTo(fbuffer3));
    // END android-added
  }

  @Test
  public void testDuplicate() {
    buf.clear();
    buf.mark();
    buf.position(buf.limit());

    // duplicate's contents should be the same as buf
    FloatBuffer duplicate = buf.duplicate();

    DomGlobal.console.log("1 " + buf.getClass().getCanonicalName());
    DomGlobal.console.log("2 " + duplicate.getClass().getCanonicalName());

    assertNotSame(buf, duplicate);
    assertEquals(buf.position(), duplicate.position());
    assertEquals(buf.limit(), duplicate.limit());
    assertEquals(buf.isReadOnly(), duplicate.isReadOnly());
    assertEquals(buf.isDirect(), duplicate.isDirect());
    assertEquals(buf.order(), duplicate.order());
    assertContentEquals(buf, duplicate);

    // duplicate's position, mark, and limit should be independent to buf
    duplicate.reset();
    assertEquals(duplicate.position(), 0);
    duplicate.clear();
    assertEquals(buf.position(), buf.limit());
    buf.reset();
    assertEquals(buf.position(), 0);

    // duplicate share the same content with buf
    if (!duplicate.isReadOnly()) {
      loadTestData1(buf);
      assertContentEquals(buf, duplicate);
      loadTestData2(duplicate);
      assertContentEquals(buf, duplicate);
    }
  }

  @Test
  public void testEquals() {
    // equal to self
    assertTrue(buf.equals(buf));
    FloatBuffer readonly = buf.asReadOnlyBuffer();
    assertTrue(buf.equals(readonly));
    FloatBuffer duplicate = buf.duplicate();
    assertTrue(buf.equals(duplicate));

    // always false, if type mismatch
    assertFalse(buf.equals(Boolean.TRUE));

    assertTrue(buf.capacity() > 5);

    buf.limit(buf.capacity()).position(0);
    readonly.limit(readonly.capacity()).position(1);
    assertFalse(buf.equals(readonly));

    buf.limit(buf.capacity() - 1).position(0);
    duplicate.limit(duplicate.capacity()).position(0);
    assertFalse(buf.equals(duplicate));
  }

  /*
   * Class under test for float get()
   */
  @Test
  public void testGet() {
    buf.clear();
    for (int i = 0; i < buf.capacity(); i++) {
      assertEquals(buf.position(), i);
      assertEquals(buf.get(), buf.get(i), 0.01);
    }
    try {
      buf.get();
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (BufferUnderflowException e) {
      // expected
    }
  }

  /*
   * Class under test for java.nio.FloatBuffer get(float[])
   */
  @Test
  public void testGetfloatArray() {
    float array[] = new float[1];
    buf.clear();
    for (int i = 0; i < buf.capacity(); i++) {
      assertEquals(buf.position(), i);
      FloatBuffer ret = buf.get(array);
      assertEquals(array[0], buf.get(i), 0.01);
      assertSame(ret, buf);
    }

    buf.get(new float[0]);

    try {
      buf.get(array);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (BufferUnderflowException e) {
      // expected
    }
  }

  /*
   * Class under test for java.nio.FloatBuffer get(float[], int, int)
   */
  @Test
  public void testGetfloatArrayintint() {
    buf.clear();
    float array[] = new float[buf.capacity()];

    try {
      buf.get(new float[buf.capacity() + 1], 0, buf.capacity() + 1);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (BufferUnderflowException e) {
      // expected
    }
    assertEquals(buf.position(), 0);
    try {
      buf.get(array, -1, array.length);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    buf.get(array, array.length, 0);
    try {
      buf.get(array, array.length + 1, 1);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    assertEquals(buf.position(), 0);
    try {
      buf.get(array, 2, -1);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    try {
      buf.get(array, 2, array.length);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    try {
      buf.get(array, 1, Integer.MAX_VALUE);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    try {
      buf.get(array, Integer.MAX_VALUE, 1);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    assertEquals(buf.position(), 0);

    buf.clear();
    FloatBuffer ret = buf.get(array, 0, array.length);
    assertEquals(buf.position(), buf.capacity());
    assertContentEquals(buf, array, 0, array.length);
    assertSame(ret, buf);
  }

  /*
   * Class under test for float get(int)
   */
  @Test
  public void testGetint() {
    buf.clear();
    for (int i = 0; i < buf.capacity(); i++) {
      assertEquals(buf.position(), i);
      assertEquals(buf.get(), buf.get(i), 0.01);
    }
    try {
      buf.get(-1);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    try {
      buf.get(buf.limit());
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
  }

  @Test
  public void testHasArray() {
    if (buf.hasArray()) {
      assertNotNull(buf.array());
    } else {
      try {
        buf.array();
        fail("Should throw Exception"); // $NON-NLS-1$
      } catch (UnsupportedOperationException e) {
        // expected
        // Note:can not tell when to catch
        // UnsupportedOperationException or
        // ReadOnlyBufferException, so catch all.
      }
    }
  }

  @Test
  public void testHashCode() {
    buf.clear();
    FloatBuffer readonly = buf.asReadOnlyBuffer();
    FloatBuffer duplicate = buf.duplicate();
    assertTrue(buf.hashCode() == readonly.hashCode());

    assertTrue(buf.capacity() > 5);
    duplicate.position(buf.capacity() / 2);
    assertTrue(buf.hashCode() != duplicate.hashCode());
  }

  @Test
  public void testIsDirect() {
    assertFalse(buf.isDirect());
  }

  @Test
  public void testIsReadOnly() {
    assertFalse(buf.isReadOnly());
  }

  @Test
  public void testOrder() {
    buf.order();
    if (buf.hasArray()) {
      assertEquals(ByteOrder.nativeOrder(), buf.order());
    }
  }

  /*
   * Class under test for java.nio.FloatBuffer put(float)
   */
  @Test
  public void testPutfloat() {
    buf.clear();
    for (int i = 0; i < buf.capacity(); i++) {
      assertEquals(buf.position(), i);
      FloatBuffer ret = buf.put((float) i);
      assertEquals(buf.get(i), (float) i, 0.0);
      assertSame(ret, buf);
    }
    try {
      buf.put(0);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (BufferOverflowException e) {
      // expected
    }
  }

  /*
   * Class under test for java.nio.FloatBuffer put(float[])
   */
  @Test
  public void testPutfloatArray() {
    float array[] = new float[1];
    buf.clear();
    for (int i = 0; i < buf.capacity(); i++) {
      assertEquals(buf.position(), i);
      array[0] = (float) i;
      FloatBuffer ret = buf.put(array);
      assertEquals(buf.get(i), (float) i, 0.0);
      assertSame(ret, buf);
    }
    try {
      buf.put(array);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (BufferOverflowException e) {
      // expected
    }
  }

  /*
   * Class under test for java.nio.FloatBuffer put(float[], int, int)
   */
  @Test
  public void testPutfloatArrayintint() {
    buf.clear();
    float array[] = new float[buf.capacity()];
    try {
      buf.put(new float[buf.capacity() + 1], 0, buf.capacity() + 1);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (BufferOverflowException e) {
      // expected
    }
    assertEquals(buf.position(), 0);
    try {
      buf.put(array, -1, array.length);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    try {
      buf.put(array, array.length + 1, 0);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    buf.put(array, array.length, 0);
    assertEquals(buf.position(), 0);
    try {
      buf.put(array, 0, -1);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    try {
      buf.put(array, 2, array.length);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    try {
      buf.put(array, Integer.MAX_VALUE, 1);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    try {
      buf.put(array, 1, Integer.MAX_VALUE);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    assertEquals(buf.position(), 0);

    loadTestData2(array, 0, array.length);
    FloatBuffer ret = buf.put(array, 0, array.length);
    assertEquals(buf.position(), buf.capacity());
    assertContentEquals(buf, array, 0, array.length);
    assertSame(ret, buf);
  }

  /*
   * Class under test for java.nio.FloatBuffer put(java.nio.FloatBuffer)
   */
  @Test
  public void testPutFloatBuffer() {
    FloatBuffer other = FloatBuffer.allocate(buf.capacity());
    try {
      buf.put(buf);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IllegalArgumentException e) {
      // expected
    }
    try {
      buf.put(FloatBuffer.allocate(buf.capacity() + 1));
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (BufferOverflowException e) {
      // expected
    }
    buf.clear();
    loadTestData2(other);
    other.clear();
    buf.clear();
    FloatBuffer ret = buf.put(other);
    assertEquals(other.position(), other.capacity());
    assertEquals(buf.position(), buf.capacity());
    assertContentEquals(other, buf);
    assertSame(ret, buf);
  }

  /*
   * Class under test for java.nio.FloatBuffer put(int, float)
   */
  @Test
  public void testPutintfloat() {
    buf.clear();
    for (int i = 0; i < buf.capacity(); i++) {
      assertEquals(buf.position(), 0);
      FloatBuffer ret = buf.put(i, (float) i);
      assertEquals(buf.get(i), (float) i, 0.0);
      assertSame(ret, buf);
    }
    try {
      buf.put(-1, 0);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
    try {
      buf.put(buf.limit(), 0);
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
  }

  @Test
  public void testSlice() {
    assertTrue(buf.capacity() > 5);
    buf.position(1);
    buf.limit(buf.capacity() - 1);

    FloatBuffer slice = buf.slice();
    assertEquals(buf.isReadOnly(), slice.isReadOnly());
    assertEquals(buf.isDirect(), slice.isDirect());
    assertEquals(buf.order(), slice.order());
    assertEquals(slice.position(), 0);
    assertEquals(slice.limit(), buf.remaining());
    assertEquals(slice.capacity(), buf.remaining());
    try {
      slice.reset();
      fail("Should throw Exception"); // $NON-NLS-1$
    } catch (InvalidMarkException e) {
      // expected
    }

    // slice share the same content with buf
    if (!slice.isReadOnly()) {
      loadTestData1(slice);
      assertContentLikeTestData1(buf, 1, 0, slice.capacity());
      buf.put(2, 500);
      assertEquals(slice.get(1), 500, 0.0);
    }
  }

  @Test
  public void testToString() {
    String str = buf.toString();
    assertTrue(str.indexOf("Float") >= 0 || str.indexOf("float") >= 0);
    assertTrue(str.indexOf("" + buf.position()) >= 0);
    assertTrue(str.indexOf("" + buf.limit()) >= 0);
    assertTrue(str.indexOf("" + buf.capacity()) >= 0);
  }

  /*
   * test for method static FloatBuffer wrap(float[] array) test covers
   * following usecases: 1. case for check FloatBuffer buf2 properties 2. case
   * for check equal between buf2 and float array[] 3. case for check a buf2
   * dependens to array[]
   */
  @Test
  public void test_Wrap$S() {
    float array[] = new float[BUFFER_LENGTH];
    loadTestData1(array, 0, BUFFER_LENGTH);
    FloatBuffer buf2 = FloatBuffer.wrap(array);

    // case: FloatBuffer buf2 properties is satisfy the conditions
    // specification
    assertEquals(buf2.capacity(), array.length);
    assertEquals(buf2.limit(), array.length);
    assertEquals(buf2.position(), 0);

    // case: FloatBuffer buf2 is equal to float array[]
    assertContentEquals(buf2, array, 0, array.length);

    // case: FloatBuffer buf2 is depended to float array[]
    loadTestData2(array, 0, buf.capacity());
    assertContentEquals(buf2, array, 0, array.length);
  }

  /*
   * test for method static FloatBuffer wrap(float[] array, int offset, int
   * length) test covers following usecases: 1. case for check FloatBuffer
   * buf2 properties 2. case for check equal between buf2 and float array[] 3.
   * case for check a buf2 dependens to array[] 4. case expected
   * IndexOutOfBoundsException
   */
  @Test
  public void test_Wrap$SII() {
    float array[] = new float[BUFFER_LENGTH];
    int offset = 5;
    int length = BUFFER_LENGTH - offset;
    loadTestData1(array, 0, BUFFER_LENGTH);
    FloatBuffer buf2 = FloatBuffer.wrap(array, offset, length);

    // case: FloatBuffer buf2 properties is satisfy the conditions
    // specification
    assertEquals(buf2.capacity(), array.length);
    assertEquals(buf2.position(), offset);
    assertEquals(buf2.limit(), offset + length);
    assertEquals(buf2.arrayOffset(), 0);

    // case: FloatBuffer buf2 is equal to float array[]
    assertContentEquals(buf2, array, 0, array.length);

    // case: FloatBuffer buf2 is depended to float array[]
    loadTestData2(array, 0, buf.capacity());
    assertContentEquals(buf2, array, 0, array.length);

    // case: expected IndexOutOfBoundsException
    try {
      offset = 7;
      buf2 = FloatBuffer.wrap(array, offset, length);
      fail("wrap method does not throws expected exception");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }
  }

  void loadTestData1(float array[], int offset, int length) {
    for (int i = 0; i < length; i++) {
      array[offset + i] = (float) i;
    }
  }

  void loadTestData2(float array[], int offset, int length) {
    for (int i = 0; i < length; i++) {
      array[offset + i] = (float) length - i;
    }
  }

  void loadTestData1(FloatBuffer buf) {
    buf.clear();
    for (int i = 0; i < buf.capacity(); i++) {
      buf.put(i, (float) i);
    }
  }

  void loadTestData2(FloatBuffer buf) {
    buf.clear();
    for (int i = 0; i < buf.capacity(); i++) {
      buf.put(i, (float) buf.capacity() - i);
    }
  }

  void assertContentEquals(FloatBuffer buf, float array[], int offset, int length) {
    for (int i = 0; i < length; i++) {
      assertEquals(buf.get(i), array[offset + i], 0.01);
    }
  }

  void assertContentEquals(FloatBuffer buf, FloatBuffer other) {
    assertEquals(buf.capacity(), other.capacity());
    for (int i = 0; i < buf.capacity(); i++) {
      DomGlobal.console.log("va1 " + buf.get(i));
      DomGlobal.console.log("va2 " + other.get(i));
      assertEquals(buf.get(i), other.get(i), 0.01);
    }
  }

  void assertContentLikeTestData1(FloatBuffer buf, int startIndex, float startValue, int length) {
    float value = startValue;
    for (int i = 0; i < length; i++) {
      assertEquals(buf.get(startIndex + i), value, 0.01);
      value = value + 1.0f;
    }
  }
}
