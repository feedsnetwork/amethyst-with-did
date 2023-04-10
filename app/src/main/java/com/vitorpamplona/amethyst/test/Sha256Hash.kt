/*
 * Copyright 2011 Google Inc.
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vitorpamplona.amethyst.test

import java.io.Serializable
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * A Sha256Hash just wraps a byte[] so that equals and hashcode work correctly, allowing it to be used as keys in a
 * map. It also checks that the length is correct and provides a bit more type safety.
 */
class Sha256Hash private constructor(rawHashBytes: ByteArray) : Serializable,Comparable<Sha256Hash?> {
    /**
     * Returns the internal byte array, without defensively copying. Therefore do NOT modify the returned array.
     */
    val bytes: ByteArray

    init {
        require(rawHashBytes.size != LENGTH) { "Invalid rawHashBytes $rawHashBytes" }
        bytes = rawHashBytes
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        return if (o == null || javaClass != o.javaClass) false else Arrays.equals(
            bytes,
            (o as Sha256Hash).bytes
        )
    }

    /**
     * Returns the last four bytes of the wrapped hash. This should be unique enough to be a suitable hash code even for
     * blocks, where the goal is to try and get the first bytes to be zeros (i.e. the value as a big integer lower
     * than the target value).
     */
    override fun hashCode(): Int {
        // Use the last 4 bytes, not the first 4 which are often zeros in Bitcoin.
        return bytes[LENGTH - 4].toInt() shl 24 or (bytes[LENGTH - 3].toInt() and 0xFF shl 16) or (
                bytes[LENGTH - 2].toInt() and 0xFF shl 8) or (bytes[LENGTH - 1].toInt() and 0xFF)
    }

    override fun toString(): String {
        return Hex.encode(bytes)
    }

    /**
     * Returns the bytes interpreted as a positive integer.
     */
    fun toBigInteger(): BigInteger {
        return BigInteger(1, bytes)
    }

    /**
     * Returns a reversed copy of the internal byte array.
     */
    val reversedBytes: ByteArray
        get() = reverseBytes(bytes)

    override operator fun compareTo(other: Sha256Hash?): Int {
        for (i in LENGTH - 1 downTo 0) {
            val thisByte = bytes[i].toInt() and 0xff
            val otherByte = other!!.bytes[i].toInt() and 0xff
            if (thisByte > otherByte) return 1
            if (thisByte < otherByte) return -1
        }
        return 0
    }

    companion object {
        private const val serialVersionUID = 538197488884042091L
        const val LENGTH = 32 // bytes
        val ZERO_HASH = wrap(ByteArray(LENGTH))

        /**
         * Creates a new instance that wraps the given hash value.
         *
         * @param rawHashBytes the raw hash bytes to wrap
         * @return a new instance
         * @throws IllegalArgumentException if the given array length is not exactly 32
         */
        fun wrap(rawHashBytes: ByteArray): Sha256Hash {
            return Sha256Hash(rawHashBytes)
        }

        /**
         * Creates a new instance that wraps the given hash value (represented as a hex string).
         *
         * @param hexString a hash value represented as a hex string
         * @return a new instance
         * @throws IllegalArgumentException if the given string is not a valid
         * hex string, or if it does not represent exactly 32 bytes
         */
        fun wrap(hexString: String?): Sha256Hash {
            return wrap(Hex.decode(hexString!!))
        }

        /**
         * Creates a new instance that wraps the given hash value, but with byte order reversed.
         *
         * @param rawHashBytes the raw hash bytes to wrap
         * @return a new instance
         * @throws IllegalArgumentException if the given array length is not exactly 32
         */
        fun wrapReversed(rawHashBytes: ByteArray): Sha256Hash {
            return wrap(reverseBytes(rawHashBytes))
        }

        /**
         * Creates a new instance containing the calculated (one-time) hash of the given bytes.
         *
         * @param contents the bytes on which the hash value is calculated
         * @return a new instance containing the calculated (one-time) hash
         */
        fun of(contents: ByteArray): Sha256Hash {
            return wrap(hash(contents))
        }

        /**
         * Creates a new instance containing the hash of the calculated hash of the given bytes.
         *
         * @param contents the bytes on which the hash value is calculated
         * @return a new instance containing the calculated (two-time) hash
         */
        fun twiceOf(contents: ByteArray): Sha256Hash {
            return wrap(hashTwice(contents))
        }

        /**
         * Creates a new instance containing the hash of the calculated hash of the given bytes.
         *
         * @param content1 first bytes on which the hash value is calculated
         * @param content2 second bytes on which the hash value is calculated
         * @return a new instance containing the calculated (two-time) hash
         */
        fun twiceOf(content1: ByteArray?, content2: ByteArray?): Sha256Hash {
            return wrap(hashTwice(content1, content2))
        }

        /**
         * Returns a new SHA-256 MessageDigest instance.
         *
         * This is a convenience method which wraps the checked
         * exception that can never occur with a RuntimeException.
         *
         * @return a new SHA-256 MessageDigest instance
         */
        fun newDigest(): MessageDigest {
            return try {
                MessageDigest.getInstance("SHA-256")
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e) // Can't happen.
            }
        }
        /**
         * Calculates the SHA-256 hash of the given byte range.
         *
         * @param input the array containing the bytes to hash
         * @param offset the offset within the array of the bytes to hash
         * @param length the number of bytes to hash
         * @return the hash (in big-endian order)
         */
        /**
         * Calculates the SHA-256 hash of the given bytes.
         *
         * @param input the bytes to hash
         * @return the hash (in big-endian order)
         */
        @JvmOverloads
        fun hash(input: ByteArray, offset: Int = 0, length: Int = input.size): ByteArray {
            val digest = newDigest()
            digest.update(input, offset, length)
            return digest.digest()
        }

        /**
         * Calculates the hash of hash on the given chunks of bytes. This is equivalent to concatenating the two
         * chunks and then passing the result to [.hashTwice].
         */
        fun hashTwice(input1: ByteArray?, input2: ByteArray?): ByteArray {
            val digest = newDigest()
            digest.update(input1)
            digest.update(input2)
            return digest.digest(digest.digest())
        }
        /**
         * Calculates the SHA-256 hash of the given byte range,
         * and then hashes the resulting hash again.
         *
         * @param input the array containing the bytes to hash
         * @param offset the offset within the array of the bytes to hash
         * @param length the number of bytes to hash
         * @return the double-hash (in big-endian order)
         */
        /**
         * Calculates the SHA-256 hash of the given bytes,
         * and then hashes the resulting hash again.
         *
         * @param input the bytes to hash
         * @return the double-hash (in big-endian order)
         */
        @JvmOverloads
        fun hashTwice(input: ByteArray, offset: Int = 0, length: Int = input.size): ByteArray {
            val digest = newDigest()
            digest.update(input, offset, length)
            return digest.digest(digest.digest())
        }

        /**
         * Calculates the hash of hash on the given byte ranges. This is equivalent to
         * concatenating the two ranges and then passing the result to [.hashTwice].
         */
        fun hashTwice(
            input1: ByteArray?, offset1: Int, length1: Int,
            input2: ByteArray?, offset2: Int, length2: Int
        ): ByteArray {
            val digest = newDigest()
            digest.update(input1, offset1, length1)
            digest.update(input2, offset2, length2)
            return digest.digest(digest.digest())
        }

        /**
         * Returns a copy of the given byte array in reverse order.
         */
        private fun reverseBytes(bytes: ByteArray): ByteArray {
            // We could use the XOR trick here but it's easier to understand if we don't. If we find this is really a
            // performance issue the matter can be revisited.
            val buf = ByteArray(bytes.size)
            for (i in bytes.indices) buf[i] = bytes[bytes.size - 1 - i]
            return buf
        }
    }
}