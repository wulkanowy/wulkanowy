package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.PreferenceDao
import io.github.wulkanowy.data.db.entities.Preference
import javax.inject.Inject

class PreferencesLocal @Inject constructor(private val dao: PreferenceDao) {

    suspend fun putPreference(studentId: Int, key: String, value: String) {
        dao.putPreference(Preference(studentId, key, value))
    }

    suspend fun getPreference(studentId: Int, key: String): Preference? {
        return dao.getPreference(studentId, key)
    }
}
