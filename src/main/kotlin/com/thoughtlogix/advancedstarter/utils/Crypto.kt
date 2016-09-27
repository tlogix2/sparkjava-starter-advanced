/*
 * MIT License
 *
 * Copyright (c) 2016 Thought Logix
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

package com.thoughtlogix.advancedstarter.utils

import org.apache.commons.codec.binary.Hex
import org.slf4j.LoggerFactory
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.xml.bind.DatatypeConverter

object Crypto {

    val logger = LoggerFactory.getLogger(Crypto::class.java)
    var pepper = "S7(>(bc%p<vPUVZ9ATp{`mX#IxOhosgkb/," + "_u67H\$zy/yzlju@k_G3o>}|m7zHYPSGL_\$zJN>/F3a9Ca6tb0<F4qePso^C#vxO\"c^grK8m\"?#('6[D'Meg)fhL9r7<hJ"

    //    private static final int iterations = 100000;
    private val iterations = 1000

    fun generateMD5(value: String): String {

        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.reset()
            messageDigest.update(value.toByteArray(Charset.forName("UTF8")))
            val resultByte = messageDigest.digest()
            return String(Hex.encodeHex(resultByte))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "BJFHGDJHGGHBVCSDEULKMVDDHJMNHFCDSSRGHBGTGVBNMLKK"
    }

    fun generateSalt(): String {
        var sr: SecureRandom? = null
        try {
            sr = SecureRandom.getInstance("SHA1PRNG")
        } catch (e: NoSuchAlgorithmException) {
            logger.error(e.message, e)
            return "dksjdklsjdlkjsdkljskldjslkd"
        }

        val salt = ByteArray(16)
        sr!!.nextBytes(salt)
        return toHex(salt)
    }

    fun generatePasswordHash(seed: String, password: String): String {
        var seed = seed
        seed = seed + pepper
        val chars = password.toCharArray()
        val salt = seed.toByteArray()

        val spec = PBEKeySpec(chars, salt, iterations, 64 * 8)
        var skf: SecretKeyFactory? = null
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        } catch (e: NoSuchAlgorithmException) {
            logger.error(e.message, e)
        }

        val hash: ByteArray
        try {
            hash = skf!!.generateSecret(spec).encoded
        } catch (e: InvalidKeySpecException) {
            logger.error(e.message, e)
            return "";
        }

        return toHex(hash)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun validatePassword(password: String, storedPassword: String, seed: String): Boolean {
        var seed = seed
        seed = seed + pepper
        val salt = seed.toByteArray()
        val hash = fromHex(storedPassword)

        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, 64 * 8)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        val testHash = skf.generateSecret(spec).encoded

        var diff: Int = hash.size xor testHash.size
        var i = 0
        while (i < hash.size && i < testHash.size) {
            diff = diff or (hash[i].toInt() xor testHash[i].toInt())
            i++
        }
        return diff == 0
    }

    private fun fromHex(hex: String): ByteArray {
        return DatatypeConverter.parseHexBinary(hex)
    }

    private fun toHex(array: ByteArray): String {
        return DatatypeConverter.printHexBinary(array)
    }
}
