/*
 * Copyright (c) 2022 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.vitorpamplona.amethyst.test

object Hex {
    private val EMPTY_BYTES = byteArrayOf()
    private val HEX_CHARS = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    )

    private fun decodeNibble(c: Char): Int {
        // Character.digit() is not used here, as it addresses a larger
        // set of characters (both ASCII and full-width latin letters).
        if (c >= '0' && c <= '9') {
            return c.code - '0'.code
        }
        if (c >= 'A' && c <= 'F') {
            return c.code - ('A'.code - 0xA)
        }
        return if (c >= 'a' && c <= 'f') {
            c.code - ('a'.code - 0xA)
        } else -1
    }

    fun decodeByte(chars: CharSequence, offset: Int): Byte {
        val hi = decodeNibble(chars[offset])
        val lo = decodeNibble(chars[offset + 1])
        require(!(hi == -1 || lo == -1)) {
            String.format(
                "Invalid hex byte '%s' at index %d of '%s'",
                chars.subSequence(offset, offset + 2), offset, chars
            )
        }
        return ((hi shl 4) + lo).toByte()
    }

    @JvmOverloads
    fun decode(chars: CharSequence, offset: Int = 0, length: Int = chars.length): ByteArray {
        require(!(length < 0 || length and 1 != 0)) { "Invalid length: $length" }
        if (length == 0) return EMPTY_BYTES
        val bytes = ByteArray(length ushr 1)
        var i = 0
        while (i < length) {
            bytes[i ushr 1] = decodeByte(chars, offset + i)
            i += 2
        }
        return bytes
    }

    @JvmOverloads
    fun encode(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): String {
        val chars = CharArray(length * 2)
        for (i in 0 until length) {
            val v = bytes[offset + i].toInt() and 0xFF
            chars[i shl 1] = HEX_CHARS[v ushr 4]
            chars[(i shl 1) + 1] = HEX_CHARS[v and 0x0F]
        }
        return String(chars)
    }
}