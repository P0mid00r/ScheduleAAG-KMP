package com.pomidorka.scheduleaag.updater

import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.Signature
import java.security.SignatureException
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date

object SignatureGenerator {
    @Throws(
        NoSuchAlgorithmException::class,
        InvalidKeySpecException::class,
        InvalidKeyException::class,
        SignatureException::class
    )
    fun generateSignature(keyId: String, privateKeyContent: String?): String {
        val kf = KeyFactory.getInstance("RSA")
        val keySpecPKCS8 = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent))
        val privateKey = kf.generatePrivate(keySpecPKCS8)
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val timestamp: String = dateFormat.format(Date())
        val messageToSign = keyId + timestamp
        val signature: Signature = Signature.getInstance("SHA512withRSA")
        signature.initSign(privateKey)
        signature.update(messageToSign.toByteArray())
        val signatureBytes: ByteArray? = signature.sign()
        val signatureValue: String? = Base64.getEncoder().encodeToString(signatureBytes)
        return String.format(
            "{\n  \"keyId\":\"%s\",\n  \"timestamp\":\"%s\",\n  \"signature\":\"%s\"\n}\n",
            keyId,
            timestamp,
            signatureValue
        )
    }
}