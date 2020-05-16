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

package java.nio;

import com.google.gwt.corp.compatibility.Numbers;
import elemental2.core.ArrayBuffer;
import elemental2.core.ArrayBufferView;
import elemental2.core.Int8Array;
import org.apache.harmony.luni.platform.Endianness;
import org.gwtproject.nio.HasArrayBufferView;

/**
 * DirectByteBuffer, DirectReadWriteByteBuffer and DirectReadOnlyHeapByteBuffer compose
 * the implementation of direct byte buffers.
 * <p>
 * DirectByteBuffer implements all the shared readonly methods and is extended by
 * the other two classes.
 * </p>
 * <p>
 * All methods are marked final for runtime performance.
 * </p>
 * 
 */
public abstract class DirectByteBuffer extends BaseByteBuffer implements HasArrayBufferView {

	Int8Array byteArray;

    DirectByteBuffer(int capacity) {
    	this(new ArrayBuffer(capacity), capacity, 0);
    }

    DirectByteBuffer(ArrayBuffer buf) {
    	this(buf, buf.byteLength, 0);
    }
    
    DirectByteBuffer(ArrayBuffer buffer, int capacity, int offset) {
    	super(capacity);
    	byteArray = new Int8Array(buffer, offset, capacity);
    }

	public ArrayBufferView getTypedArray() {
		return byteArray;
	}
	
	public int getElementSize() {
		return 1;
	}

    /*
     * Override ByteBuffer.get(byte[], int, int) to improve performance.
     * 
     * (non-Javadoc)
     * 
     * @see java.nio.ByteBuffer#get(byte[], int, int)
     */
    public final ByteBuffer get(byte[] dest, int off, int len) {
        int length = dest.length;
        if (off < 0 || len < 0 || off + len > length) {
            throw new IndexOutOfBoundsException();
        }
        if (len > remaining()) {
            throw new BufferUnderflowException();
        }
        
        for (int i = 0; i < len; i++) {
        	dest[i + off] = get(position + i);
        }
        
        position += len;
        return this;
    }
    
    public final byte get() {
//        if (position == limit) {
//            throw new BufferUnderflowException();
//        }
        return byteArray.getAt(position++).byteValue();
    }

    public final byte get(int index) {
//        if (index < 0 || index >= limit) {
//            throw new IndexOutOfBoundsException();
//        }
        return byteArray.getAt(index).byteValue();
    }

    public final double getDouble() {
        return Numbers.longBitsToDouble(getLong());
    }

    public final double getDouble(int index) {
        return Numbers.longBitsToDouble(getLong(index));
    }

    public final float getFloat() {
        return Numbers.intBitsToFloat(getInt());
    }

    public final float getFloat(int index) {
        return Numbers.intBitsToFloat(getInt(index));
    }

    public final int getInt() {
        int newPosition = position + 4;
//        if (newPosition > limit) {
//            throw new BufferUnderflowException();
//        }
        int result = loadInt(position);
        position = newPosition;
        return result;
    }

    public final int getInt(int index) {
//        if (index < 0 || index + 4 > limit) {
//            throw new IndexOutOfBoundsException();
//        }
        return loadInt(index);
    }

    public final long getLong() {
        int newPosition = position + 8;
//        if (newPosition > limit) {
//            throw new BufferUnderflowException();
//        }
        long result = loadLong(position);
        position = newPosition;
        return result;
    }

    public final long getLong(int index) {
//        if (index < 0 || index + 8 > limit) {
//            throw new IndexOutOfBoundsException();
//        }
        return loadLong(index);
    }

    public final short getShort() {
        int newPosition = position + 2;
//        if (newPosition > limit) {
//            throw new BufferUnderflowException();
//        }
        short result = loadShort(position);
        position = newPosition;
        return result;
    }

    public final short getShort(int index) {
//        if (index < 0 || index + 2 > limit) {
//            throw new IndexOutOfBoundsException();
//        }
        return loadShort(index);
    }

    public final boolean isDirect() {
        return true;
    }

    protected final int loadInt(int baseOffset) {
        int bytes = 0;
        if(order == Endianness.BIG_ENDIAN){
            for (int i = 0; i < 4; i++) {
                bytes = bytes << 8;
                bytes = bytes | (byteArray.getAt(baseOffset + i).byteValue() & 0xFF);
            }    
        }else{
            for (int i = 3; i >= 0; i--) {
                bytes = bytes << 8;
                bytes = bytes | (byteArray.getAt(baseOffset + i).byteValue() & 0xFF);
            }
        }
        return bytes;
    }

    protected final long loadLong(int baseOffset) {
        long bytes = 0;
        if(order == Endianness.BIG_ENDIAN){
            for (int i = 0; i < 8; i++) {
                bytes = bytes << 8;
                bytes = bytes | (byteArray.getAt(baseOffset + i).byteValue() & 0xFF);
            }    
        }else{
            for (int i = 7; i >= 0; i--) {
                bytes = bytes << 8;
                bytes = bytes | (byteArray.getAt(baseOffset + i).byteValue() & 0xFF);
            }
        }
        return bytes;
    }

    protected final short loadShort(int baseOffset) {
        short bytes  = 0;
        if(order == Endianness.BIG_ENDIAN){
            bytes = (short) (byteArray.getAt(baseOffset).byteValue() << 8);
            bytes |= (byteArray.getAt(baseOffset + 1).byteValue() & 0xFF);
        }else{
            bytes = (short) (byteArray.getAt(baseOffset+1).byteValue() << 8);
            bytes |= (byteArray.getAt(baseOffset).byteValue() & 0xFF);
        }
        return bytes;
    }

    protected final void store(int baseOffset, int value) {
        if (order == Endianness.BIG_ENDIAN) {
            for (int i = 3; i >= 0; i--) {
                byteArray.setAt(baseOffset + i, (double) (value & 0xFF));
                value = value >> 8;
            }
        } else {
            for (int i = 0; i <= 3; i++) {
                byteArray.setAt(baseOffset + i, (double) (value & 0xFF));
                value = value >> 8;
            }
        }
    }

    protected final void store(int baseOffset, long value) {
        if (order == Endianness.BIG_ENDIAN) {
            for (int i = 7; i >= 0; i--) {
                byteArray.setAt(baseOffset + i, (double) (value & 0xFF));
                value = value >> 8;
            }
        } else {
            for (int i = 0; i <= 7; i++) {
                byteArray.setAt(baseOffset + i, (double) (value & 0xFF));
                value = value >> 8;
            }
        }
    }

    protected final void store(int baseOffset, short value) {
        if (order == Endianness.BIG_ENDIAN) {
            byteArray.setAt(baseOffset, (double) ((value >> 8) & 0xFF));
            byteArray.setAt(baseOffset + 1, (double) (value & 0xFF));
        } else {
            byteArray.setAt(baseOffset+1, (double) ((value >> 8) & 0xFF));
            byteArray.setAt(baseOffset, (double) (value & 0xFF));
        }
    }

    @Override
    public int getElementType() {
        return 0x1400; // GL_BYTE
    }
}