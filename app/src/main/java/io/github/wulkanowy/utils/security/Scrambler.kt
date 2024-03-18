@file:Suppress("DEPRECATION")

package io.github.wulkanowy.utils.security

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.util.Base64.DEFAULT
import android.util.Base64.decode
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.spec.MGF1ParameterSpec.SHA1
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.CipherInputStream
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource.PSpecified
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Scrambler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val keyCharset = Charset.forName("UTF-8")

    private val isKeyPairExists: Boolean
        get() = keyStore.getKey(KEY_ALIAS, null) != null

    private val keyStore: KeyStore
        get() = KeyStore.getInstance(KEYSTORE_NAME).apply { load(null) }

    private val cipher: Cipher
        get() {
            return if (SDK_INT >= M) Cipher.getInstance(
                "RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
                "AndroidKeyStoreBCWorkaround"
            )
            else Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL")
        }

    fun decrypt(cipherText: String): String {
        if (cipherText.isEmpty()) throw ScramblerException("Text to be encrypted is empty")

        return try {
            if (!isKeyPairExists) throw ScramblerException("KeyPair doesn't exist")

            cipher.let {
                if (SDK_INT >= M) {
                    OAEPParameterSpec("SHA-256", "MGF1", SHA1, PSpecified.DEFAULT).let { spec ->
                        it.init(DECRYPT_MODE, keyStore.getKey(KEY_ALIAS, null), spec)
                    }
                } else it.init(DECRYPT_MODE, keyStore.getKey(KEY_ALIAS, null))

                CipherInputStream(
                    ByteArrayInputStream(decode(cipherText, DEFAULT)),
                    it
                ).let { input ->
                    val values = ArrayList<Byte>()
                    var nextByte: Int
                    while (run { nextByte = input.read(); nextByte } != -1) {
                        values.add(nextByte.toByte())
                    }
                    val bytes = ByteArray(values.size)
                    for (i in bytes.indices) {
                        bytes[i] = values[i]
                    }
                    String(bytes, 0, bytes.size, keyCharset)
                }
            }
        } catch (e: Exception) {
            throw ScramblerException("An error occurred while decrypting text", e)
        }
    }

    fun clearKeyPair() {
        keyStore.deleteEntry(KEY_ALIAS)
        Timber.i("KeyPair has been cleared")
    }

    companion object {
        const val KEYSTORE_NAME = "AndroidKeyStore"
        const val KEY_ALIAS = "wulkanowy_password"
    }
}
