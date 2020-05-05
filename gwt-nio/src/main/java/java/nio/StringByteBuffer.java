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

import com.google.gwt.corp.compatibility.Numbers;

class StringByteBuffer extends BaseByteBuffer {

    private String s;

    StringByteBuffer(String s, int position, int limit) {
        this(s);
        this.position = position;
        this.limit = limit;
    }

    StringByteBuffer(String s) {
        super(s.length());
        this.s = s;
        order(ByteOrder.LITTLE_ENDIAN);
    }

    byte[] protectedArray() {
        throw new UnsupportedOperationException();
    }

    int protectedArrayOffset() {
        throw new UnsupportedOperationException();
    }

    boolean protectedHasArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuffer slice() {
        // TODO(jgw): I don't think this is right, but might work for our purposes.
        StringByteBuffer slice = new StringByteBuffer(s, position, limit);
        slice.order = order;
        return slice;
    }

    @Override
    public ByteBuffer duplicate() {
        return this;
    }

    public ByteBuffer asReadOnlyBuffer() {
        return this;
    }

    @Override
    public byte get() {
        return get(position++);
    }

    @Override
    public ByteBuffer put(byte b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte get(int index) {
        return get(s, index);
    }

    @Override
    public ByteBuffer put(int index, byte b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuffer compact() {
        return this;
    }

    @Override
    public boolean isDirect() {
        return false;
    }

    public final short getShort() {
        int newPosition = position + 2;
        short result = loadShort(position);
        position = newPosition;
        return result;
    }

    @Override
    public ByteBuffer putShort(short value) {
        throw new UnsupportedOperationException();
    }

    public final short getShort(int index) {
        return loadShort(index);
    }

    @Override
    public ByteBuffer putShort(int index, short value) {
        throw new UnsupportedOperationException();
    }

    public final int getInt() {
        int newPosition = position + 4;
        int result = loadInt(position);
        position = newPosition;
        return result;
    }

    @Override
    public ByteBuffer putInt(int value) {
        throw new UnsupportedOperationException();
    }

    public final int getInt(int index) {
        return loadInt(index);
    }

    @Override
    public ByteBuffer putInt(int index, int value) {
        throw new UnsupportedOperationException();
    }

    public final long getLong() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuffer putLong(long value) {
        throw new UnsupportedOperationException();
    }

    public final long getLong(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuffer putLong(int index, long value) {
        throw new UnsupportedOperationException();
    }

    public final float getFloat() {
        return Numbers.intBitsToFloat(getInt());
    }

    @Override
    public ByteBuffer putFloat(float value) {
        throw new UnsupportedOperationException();
    }

    public final float getFloat(int index) {
        return Numbers.intBitsToFloat(getInt(index));
    }

    @Override
    public ByteBuffer putFloat(int index, float value) {
        throw new UnsupportedOperationException();
    }

    public final double getDouble() {
        return Numbers.longBitsToDouble(getLong());
    }

    @Override
    public ByteBuffer putDouble(double value) {
        throw new UnsupportedOperationException();
    }

    public final double getDouble(int index) {
        return Numbers.longBitsToDouble(getLong(index));
    }

    @Override
    public ByteBuffer putDouble(int index, double value) {
        throw new UnsupportedOperationException();
    }

    protected final int loadInt(int baseOffset) {
        int bytes = 0;
        for (int i = 3; i >= 0; i--) {
            bytes = bytes << 8;
            bytes = bytes | (get(baseOffset + i) & 0xFF);
        }
        return bytes;
    }

    protected final short loadShort(int baseOffset) {
        short bytes = 0;
        bytes = (short) (get(baseOffset + 1) << 8);
        bytes |= (get(baseOffset) & 0xFF);
        return bytes;
    }

    ;

    private byte get(String s, int i) {
        int x = s.codePointAt(i) & 0xff;
        if (x > 127) {
            x -= 256;
        }
        return (byte) x;
    } /*-{
    var x = s.charCodeAt(i) & 0xff;
    if (x > 127) x -= 256;
    return x;
  }-*/

    @Override
    public boolean isReadOnly() {
        return true;
    }
}
