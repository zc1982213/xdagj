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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class BytesValues {

    private static final int MAX_UNSIGNED_BYTE = (1 << 8) - 1;
    private static final int MAX_UNSIGNED_SHORT = (1 << 16) - 1;
    private static final long MAX_UNSIGNED_INT = (1L << 32) - 1;

    private BytesValues() {}

    private static byte b(final long l) {
        return (byte) (l & 0xFF);
    }

    /**
     * Returns the number of zero-valued bytes in the bytes value.
     *
     * @param value The value of which to count tye zero-valued bytes within
     * @return the number of zero-valued bytes in the bytes value.
     */
    public static int countZeros(final BytesValue value) {
        int count = 0;
        for (int i = 0; i < value.size(); i++) {
            if (value.get(i) == 0) {
                ++count;
            }
        }
        return count;
    }

    /**
     * Return a slice of the provided value representing the same value but without any potential
     * leading zeros.
     *
     * @param value The value of which to trim leading zeros.
     * @return {@code value} if its left-most byte is non zero, or a slice that exclude any leading
     *     zeros. Note that if {@code value} contains only zeros, it will return an empty value.
     */
    public static BytesValue trimLeadingZeros(final BytesValue value) {
        final int toTrim = leadingZeros(value);
        return value.slice(toTrim);
    }

    /**
     * Returns the smallest bytes value whose bytes correspond to the provided long. That is, the
     * returned value may be of size less than 8 if the provided int has leading zero bytes.
     *
     * @param l The long from which to create the bytes value.
     * @return The minimal bytes representation corresponding to {@code l}.
     */
    public static BytesValue toMinimalBytes(final long l) {
        if (l == 0) return BytesValue.EMPTY;

        final int zeros = Long.numberOfLeadingZeros(l);
        final int resultBytes = 8 - (zeros / 8);

        final byte[] result = new byte[resultBytes];
        int shift = 0;
        for (int i = 0; i < resultBytes; i++) {
            result[resultBytes - i - 1] = b(l >> shift);
            shift += 8;
        }
        return BytesValue.wrap(result);
    }

    /**
     * Returns a 1 byte value corresponding to the provided value interpreted as an unsigned byte
     * value.
     *
     * @param v The value, which must fit an unsigned byte.
     * @return A single byte value corresponding to {@code v}.
     * @throws IllegalArgumentException if {@code v < 0} or {@code v} is too big to fit an unsigned
     *     byte (that is, if {@code v >= (1 << 8)}).
     */
    public static BytesValue ofUnsignedByte(final int v) {
        checkArgument(
                v >= 0 && v <= MAX_UNSIGNED_BYTE,
                "Value %s cannot be represented as an unsigned byte (it is negative or too big)",
                v);
        final byte[] res = new byte[1];
        res[0] = b(v);
        return BytesValue.wrap(res);
    }

    /**
     * Returns a 2 bytes value corresponding to the provided value interpreted as an unsigned short
     * value.
     *
     * @param v The value, which must fit an unsigned short.
     * @return A 2 bytes value corresponding to {@code v}.
     * @throws IllegalArgumentException if {@code v < 0} or {@code v} is too big to fit an unsigned
     *     2-bytes short (that is, if {@code v >= (1 << 16)}).
     */
    public static BytesValue ofUnsignedShort(final int v) {
        checkArgument(
                v >= 0 && v <= MAX_UNSIGNED_SHORT,
                "Value %s cannot be represented as an unsigned short (it is negative or too big)",
                v);
        final byte[] res = new byte[2];
        res[0] = b(v >> 8);
        res[1] = b(v);
        return BytesValue.wrap(res);
    }

    /**
     * Returns a 4 bytes value corresponding to the provided value interpreted as an unsigned int
     * value.
     *
     * @param v The value, which must fit an unsigned int.
     * @return A 4 bytes value corresponding to {@code v}.
     * @throws IllegalArgumentException if {@code v < 0} or {@code v} is too big to fit an unsigned
     *     4-bytes int (that is, if {@code v >= (1L << 32)}).
     */
    public static BytesValue ofUnsignedInt(final long v) {
        checkArgument(
                v >= 0 && v <= MAX_UNSIGNED_INT,
                "Value %s cannot be represented as an unsigned int (it is negative or too big)",
                v);
        final byte[] res = new byte[4];
        res[0] = b(v >> 24);
        res[1] = b(v >> 16);
        res[2] = b(v >> 8);
        res[3] = b(v);
        return BytesValue.wrap(res);
    }

    /**
     * Creates a newly allocated value containing the concatenation of the values provided.
     *
     * @param values The value to copy/concatenate.
     * @return A newly allocated value containing the result of containing the value from {@code
     *     values} in their provided order.
     */
    public static BytesValue concatenate(final BytesValue... values) {
        int size = 0;
        for (final BytesValue value : values) {
            size += value.size();
        }

        final MutableBytesValue result = MutableBytesValue.create(size);
        int offset = 0;
        for (final BytesValue value : values) {
            value.copyTo(result, offset);
            offset += value.size();
        }
        return result;
    }

    /**
     * The BigInteger corresponding to interpreting the provided bytes as an unsigned integer.
     *
     * @param bytes The bytes to interpret.
     * @return A positive (or zero) {@link BigInteger} corresponding to this value interpreted as an
     *     unsigned integer representation.
     */
    public static BigInteger asUnsignedBigInteger(final BytesValue bytes) {
        return new BigInteger(1, bytes.getArrayUnsafe());
    }

    /**
     * Decode the bytes as a UTF-8 String.
     *
     * @param bytes The bytes to decode.
     * @return A utf-8 string corresponding to the bytes data.
     */
    public static String asString(final BytesValue bytes) {
        return new String(bytes.extractArray(), StandardCharsets.UTF_8);
    }

    /**
     * The BigInteger corresponding to interpreting the provided bytes as a signed integer.
     *
     * @param bytes The bytes to interpret.
     * @return A {@link BigInteger} corresponding to this value interpreted as a two's-complement
     *     integer representation.
     */
    public static BigInteger asSignedBigInteger(final BytesValue bytes) {
        // An empty byte value is an invalid magnitude as far as BigInteger is concerned because it
        // wants at least 1 sign bit.
        if (bytes.size() == 0) {
            return BigInteger.ZERO;
        }
        return new BigInteger(bytes.getArrayUnsafe());
    }

    // In Java9, this could be moved to BytesValue and made private
    static BytesValue fromHexString(final String str, final int destSize, final boolean lenient) {
        return BytesValue.wrap(fromRawHexString(str, destSize, lenient));
    }

    static byte[] fromRawHexString(
            final String str, final int taintedDestSize, final boolean lenient) {
        String hex = str;
        if (str.startsWith("0x")) {
            hex = str.substring(2);
        }

        int len = hex.length();
        int idxShift = 0;
        if (len % 2 != 0) {
            if (!lenient) {
                throw new IllegalArgumentException("Invalid odd-length hex binary representation " + str);
            }

            hex = "0" + hex;
            len += 1;
            idxShift = 1;
        }

        final int size = len / 2;
        final int destSize;

        if (taintedDestSize < 0) {
            destSize = size;
        } else {
            destSize = taintedDestSize;
            checkArgument(
                    size <= destSize,
                    "Hex value %s is too big: expected at most %s bytes but got %s",
                    str,
                    destSize,
                    size);
        }

        final byte[] out = new byte[destSize];

        final int destOffset = (destSize - size);
        for (int i = 0; i < len; i += 2) {
            final int h = hexToBin(hex.charAt(i));
            final int l = hexToBin(hex.charAt(i + 1));
            if (h == -1) {
                throw new IllegalArgumentException(
                        String.format(
                                "Illegal character '%c' found at index %d in hex binary representation %s",
                                hex.charAt(i), i - idxShift, str));
            }
            if (l == -1) {
                throw new IllegalArgumentException(
                        String.format(
                                "Illegal character '%c' found at index %d in hex binary representation %s",
                                hex.charAt(i + 1), i + 1 - idxShift, str));
            }

            out[destOffset + (i / 2)] = (byte) (h * 16 + l);
        }
        return out;
    }

    private static int hexToBin(final char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - 48;
        } else if ('A' <= ch && ch <= 'F') {
            return ch - 65 + 10;
        } else {
            return 'a' <= ch && ch <= 'f' ? ch - 97 + 10 : -1;
        }
    }

    private static int leadingZeros(final BytesValue bytes) {
        for (int i = 0; i < bytes.size(); i++) {
            if (bytes.get(i) != 0) {
                return i;
            }
        }
        return bytes.size();
    }
}
