@file:Suppress("DEPRECATION")

package io.github.wulkanowy.data.repositories

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.security.Scrambler
import io.github.wulkanowy.utils.security.ScramblerException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val scrambler: Scrambler
) {

    private val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        context,
        "wulkanowy_data",
        MasterKey.Builder(context, "wulkanowy_master_key")
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getPassword(student: Student): String {
        return if (student.password.isNotBlank()) {
            scrambler.decrypt(student.password)
        } else {
            val unique =
                "${student.email}-${student.symbol}-${student.studentId}-${student.schoolSymbol}-${student.classId}"
            encryptedSharedPreferences.getString(unique, null)
                ?: throw ScramblerException("Password not found")
        }
    }

    fun savePassword(student: Student) {
        encryptedSharedPreferences.edit(commit = true) {
            val unique =
                "${student.email}-${student.symbol}-${student.studentId}-${student.schoolSymbol}-${student.classId}"
            putString(unique, student.password)
        }
    }
}
