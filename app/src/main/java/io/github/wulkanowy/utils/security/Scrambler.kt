package io.github.wulkanowy.utils.security

import android.annotation.TargetApi
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR2
import android.os.Build.VERSION_CODES.M
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.*
import android.util.Base64
import android.util.Base64.DEFAULT
import org.apache.commons.lang3.StringUtils
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal


object Scrambler {

    private const val KEY_ALIAS = "USER_PASSWORD"

    private const val ALGORITHM_RSA = "RSA"

    private const val KEYSTORE_NAME = "AndroidKeyStore"

    private const val KEY_TRANSFORMATION_ALGORITHM = "RSA/ECB/PKCS1Padding"

    private const val KEY_CIPHER_JELLY_PROVIDER = "AndroidOpenSSL"

    private const val KEY_CIPHER_M_PROVIDER = "AndroidKeyStoreBCWorkaround"

    fun encrypt(plainText: String, context: Context): String {
        if (StringUtils.isNotEmpty(plainText)) {
            if (SDK_INT < JELLY_BEAN_MR2) {
                return Base64.encode(plainText.toByteArray(), DEFAULT).toString()
            } else {
                try {
                    if (!isKeyPairExist()) {
                        generateKeyPair(context)
                    }

                    val cipher = if (SDK_INT >= M) {
                        Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_M_PROVIDER)
                    } else {
                        Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_JELLY_PROVIDER)
                    }
                    cipher.init(ENCRYPT_MODE, getPublicKey())

                    val outputStream = ByteArrayOutputStream()
                    val cipherOutputStream = CipherOutputStream(outputStream, cipher)
                    cipherOutputStream.write(plainText.toByteArray())
                    cipherOutputStream.close()

                    return Base64.encodeToString(outputStream.toByteArray(), DEFAULT)
                } catch (e: Exception) {
                    throw ScramblerException("An error occurred while encrypting text", e)
                }
            }
        }
        throw ScramblerException("Text to be encrypted is empty")
    }

    fun decrypt(cipherText: String): String {
        return ""
    }

    private fun getKeyStoreInstance(): KeyStore {
        val keyStore = KeyStore.getInstance(KEYSTORE_NAME)
        keyStore.load(null)
        return keyStore
    }

    private fun getPublicKey(): PublicKey = if (isKeyPairExist()) {
        (getKeyStoreInstance().getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry).certificate.publicKey
    } else {
        throw ScramblerException("KeyPair doesn't exist")
    }

    @TargetApi(JELLY_BEAN_MR2)
    @Suppress("DEPRECATION")
    private fun generateKeyPair(context: Context) {
        val spec = if (SDK_INT >= M) {
            KeyGenParameterSpec.Builder(KEY_ALIAS, PURPOSE_DECRYPT or PURPOSE_ENCRYPT)
                    .setDigests(DIGEST_SHA256, DIGEST_SHA512)
                    .setEncryptionPaddings(ENCRYPTION_PADDING_RSA_PKCS1)
                    .build()
        } else {
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 99)

            KeyPairGeneratorSpec.Builder(context)
                    .setAlias(KEYSTORE_NAME)
                    .setSubject(X500Principal("CN=Sample Name, O=Android Authority"))
                    .setSerialNumber(BigInteger.TEN)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .build()
        }
        val generator = KeyPairGenerator.getInstance(ALGORITHM_RSA)
        generator.initialize(spec)
        generator.generateKeyPair()
    }

    private fun isKeyPairExist(): Boolean = getKeyStoreInstance().getKey(KEY_ALIAS, null) != null
}