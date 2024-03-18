package io.github.wulkanowy.data.repositories

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.DIGEST_SHA256
import android.security.keystore.KeyProperties.DIGEST_SHA512
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_RSA_OAEP
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.security.Scrambler
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.MGF1ParameterSpec.SHA1
import java.time.Instant
import javax.crypto.Cipher
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.CipherOutputStream
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.security.auth.x500.X500Principal
import kotlin.test.assertEquals

@SdkSuppress(minSdkVersion = 23)
@SmallTest
@RunWith(AndroidJUnit4::class)
class PasswordRepositoryTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val scrambler = Scrambler(context)

    private fun encrypt(plainText: String): String {
        val keyStore = KeyStore.getInstance(Scrambler.KEYSTORE_NAME).apply { load(null) }
        val isKeyPairExists = keyStore.getKey(Scrambler.KEY_ALIAS, null) != null

        if (!isKeyPairExists) generateKeyPair()

        val cipher = Cipher.getInstance(
            "RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
            "AndroidKeyStoreBCWorkaround"
        )

        cipher.let {
            OAEPParameterSpec("SHA-256", "MGF1", SHA1, PSource.PSpecified.DEFAULT).let { spec ->
                it.init(
                    ENCRYPT_MODE,
                    keyStore.getCertificate(Scrambler.KEY_ALIAS).publicKey,
                    spec
                )
            }

            return ByteArrayOutputStream().let { output ->
                CipherOutputStream(output, it).apply {
                    write(plainText.toByteArray(Charset.forName("UTF-8")))
                    close()
                }
                encodeToString(output.toByteArray(), DEFAULT)
            }
        }
    }

    private fun generateKeyPair() {
        KeyGenParameterSpec.Builder(Scrambler.KEY_ALIAS, PURPOSE_DECRYPT or PURPOSE_ENCRYPT)
            .setDigests(DIGEST_SHA256, DIGEST_SHA512)
            .setEncryptionPaddings(ENCRYPTION_PADDING_RSA_OAEP)
            .setCertificateSerialNumber(BigInteger.TEN)
            .setCertificateSubject(X500Principal("CN=Wulkanowy"))
            .build()
            .let {
                KeyPairGenerator.getInstance("RSA", Scrambler.KEYSTORE_NAME).apply {
                    initialize(it)
                    genKeyPair()
                }
            }
    }

    private fun getStudentEntity(password: String) = Student(
        scrapperBaseUrl = "http://fakelog.cf",
        scrapperDomainSuffix = "",
        email = "jan@fakelog.cf",
        certificateKey = "",
        classId = 0,
        className = "",
        isCurrent = false,
        isParent = false,
        loginMode = Sdk.Mode.HEBE.name,
        loginType = "STANDARD",
        mobileBaseUrl = "",
        privateKey = "",
        registrationDate = Instant.now(),
        schoolName = "",
        schoolShortName = "test",
        schoolSymbol = "",
        studentId = 1,
        studentName = "",
        symbol = "",
        userLoginId = 1,
        userName = "",
        password = password
    ).apply {
        id = 1
    }

    @Test
    fun encrypt_and_decrypt_password_encrypted_by_androidx() {
        val passwordRepository = PasswordRepository(
            context = context,
            scrambler = scrambler
        )
        val passwordToTest = "password_androidx"
        val student = getStudentEntity(passwordToTest)
        val studentNoPass = getStudentEntity("")

        passwordRepository.savePassword(student)
        val resultPassword = passwordRepository.getPassword(studentNoPass)

        assertEquals(expected = passwordToTest, actual = resultPassword)
    }

    @Test
    fun encrypt_and_decrypt_password_encrypted_by_legacy() {
        val passwordRepository = PasswordRepository(
            context = context,
            scrambler = scrambler
        )
        val passwordToTest = "password_legacy"
        val encryptedLegacyPassword = encrypt(passwordToTest)
        val student = getStudentEntity(encryptedLegacyPassword)

        val resultPassword = passwordRepository.getPassword(student)

        assertEquals(expected = passwordToTest, actual = resultPassword)
    }
}
