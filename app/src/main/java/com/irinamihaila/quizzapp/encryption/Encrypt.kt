package com.irinamihaila.quizzapp.encryption

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object Encrypt {
    fun encrypt(data: String) = run {
        val plaintext: ByteArray = data.toByteArray()
        val keygen = KeyGenerator.getInstance("AES")
        keygen.init(256)
        val key: SecretKey = keygen.generateKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val ciphertext: ByteArray = cipher.doFinal(plaintext)
        val iv: ByteArray = cipher.iv
        ciphertext to iv
    }
}