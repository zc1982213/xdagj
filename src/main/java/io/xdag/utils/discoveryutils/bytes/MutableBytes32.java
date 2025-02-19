/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2030 The XdagJ Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.xdag.utils.discoveryutils.bytes;

import java.security.MessageDigest;

import static com.google.common.base.Preconditions.checkArgument;

public interface MutableBytes32 extends MutableBytesValue, Bytes32 {

    /**
     * Wraps a 32 bytes array as a mutable 32 bytes value.
     *
     * <p>This method behave exactly as {@link Bytes32#wrap(byte[])} except that the result is a
     * mutable.
     *
     * @param value The value to wrap.
     * @return A {@link MutableBytes32} wrapping {@code value}.
     * @throws IllegalArgumentException if {@code value.length != 32}.
     */
    static MutableBytes32 wrap(final byte[] value) {
        return new MutableArrayWrappingBytes32(value);
    }

    /**
     * Creates a new mutable 32 bytes value.
     *
     * @return A newly allocated {@link MutableBytesValue}.
     */
    static MutableBytes32 create() {
        return new MutableArrayWrappingBytes32(new byte[SIZE]);
    }

    /**
     * Wraps an existing {@link MutableBytesValue} of size 32 as a mutable 32 bytes value.
     *
     * <p>This method does no copy the provided bytes and so any mutation on {@code value} will also
     * be reflected in the value returned by this method. If a copy is desirable, this can be simply
     * achieved with calling {@link BytesValue#copyTo(MutableBytesValue)} with a newly created {@link
     * MutableBytes32} as destination to the copy.
     *
     * @param value The value to wrap.
     * @return A {@link MutableBytes32} wrapping {@code value}.
     * @throws IllegalArgumentException if {@code value.size() != 32}.
     */
    static MutableBytes32 wrap(final MutableBytesValue value) {
        checkArgument(value.size() == SIZE, "Expected %s bytes but got %s", SIZE, value.size());
        return new MutableBytes32() {
            @Override
            public void set(final int i, final byte b) {
                value.set(i, b);
            }

            @Override
            public MutableBytesValue mutableSlice(final int i, final int length) {
                return value.mutableSlice(i, length);
            }

            @Override
            public byte get(final int i) {
                return value.get(i);
            }

            @Override
            public BytesValue slice(final int index) {
                return value.slice(index);
            }

            @Override
            public BytesValue slice(final int index, final int length) {
                return value.slice(index, length);
            }

            @Override
            public Bytes32 copy() {
                return Bytes32.wrap(value.extractArray());
            }

            @Override
            public MutableBytes32 mutableCopy() {
                return MutableBytes32.wrap(value.extractArray());
            }

            @Override
            public void copyTo(final MutableBytesValue destination) {
                value.copyTo(destination);
            }

            @Override
            public void copyTo(final MutableBytesValue destination, final int destinationOffset) {
                value.copyTo(destination, destinationOffset);
            }

            @Override
            public int commonPrefixLength(final BytesValue other) {
                return value.commonPrefixLength(other);
            }

            @Override
            public BytesValue commonPrefix(final BytesValue other) {
                return value.commonPrefix(other);
            }

            @Override
            public void update(final MessageDigest digest) {
                value.update(digest);
            }

            @Override
            public boolean isZero() {
                return value.isZero();
            }

            @Override
            public boolean equals(final Object other) {
                return value.equals(other);
            }

            @Override
            public int hashCode() {
                return value.hashCode();
            }

            @Override
            public String toString() {
                return value.toString();
            }
        };
    }
}
