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

package java.nio;

import elemental2.core.ArrayBufferView;
import elemental2.core.Int32Array;
import org.gwtproject.nio.HasArrayBufferView;

/**
 * This class wraps a byte buffer to be a int buffer.
 * <p>
 * Implementation notice:
 * <ul>
 * <li>After a byte buffer instance is wrapped, it becomes privately owned by
 * the adapter. It must NOT be accessed outside the adapter any more.</li>
 * <li>The byte buffer's position and limit are NOT linked with the adapter.
 * The adapter extends Buffer, thus has its own position and limit.</li>
 * </ul>
 * </p>
 */
final class DirectReadOnlyIntBufferAdapter extends IntBuffer implements HasArrayBufferView {

    private final DirectByteBuffer byteBuffer;
    private final Int32Array intArray;

    DirectReadOnlyIntBufferAdapter(DirectByteBuffer byteBuffer) {
        super((byteBuffer.capacity() >> 2));
        this.byteBuffer = byteBuffer;
        this.byteBuffer.clear();

        this.intArray = new Int32Array(byteBuffer.getTypedArray(), byteBuffer.getTypedArray().byteOffset, capacity);
    }

    static IntBuffer wrap(DirectByteBuffer byteBuffer) {
        return new DirectReadOnlyIntBufferAdapter((DirectByteBuffer) byteBuffer.slice());
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    protected int[] protectedArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int protectedArrayOffset() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean protectedHasArray() {
        return false;
    }

    @Override
    public IntBuffer slice() {
        byteBuffer.limit(limit << 2);
        byteBuffer.position(position << 2);
        IntBuffer result = new DirectReadOnlyIntBufferAdapter((DirectByteBuffer) byteBuffer.slice());
        byteBuffer.clear();
        return result;
    }

    @Override
    public IntBuffer duplicate() {
        DirectReadOnlyIntBufferAdapter buf = new DirectReadOnlyIntBufferAdapter(
                (DirectByteBuffer) byteBuffer.duplicate());
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    @Override
    public IntBuffer asReadOnlyBuffer() {
        DirectReadOnlyIntBufferAdapter buf = new DirectReadOnlyIntBufferAdapter(byteBuffer);
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    @Override
    public int get() {
//        if (position == limit) {
//            throw new BufferUnderflowException();
//        }
        return intArray.getAt(position++).intValue();
    }

    @Override
    public IntBuffer put(int c) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        return intArray.getAt(index).intValue();
    }

    @Override
    public IntBuffer put(int index, int c) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public IntBuffer compact() {
        throw new ReadOnlyBufferException();
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    public ByteOrder order() {
        return byteBuffer.order();
    }

    public ArrayBufferView getTypedArray() {
        return intArray;
    }

    public int getElementSize() {
        return 4;
    }

    @Override
    public int getElementType() {
        return 0x1404; // GL_INT
    }
}
