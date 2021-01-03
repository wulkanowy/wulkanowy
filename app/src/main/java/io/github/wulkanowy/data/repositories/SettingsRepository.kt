package io.github.wulkanowy.data.repositories

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val dataStore: DataStore<Preferences>,
) {

    private val gradeColorThemeKey = preferencesKey<String>(R.string.pref_key_global_grade_color_scheme.asString)
    val gradeColorTheme = gradeColorThemeKey.getFlowValue(R.string.pref_default_grade_color_scheme.asString)
    suspend fun setGradeColorTheme(value: String) = gradeColorThemeKey.saveValue(value)

    private val isGradeExpandableKey = preferencesKey<Boolean>(R.string.pref_key_global_expand_grade.asString)
    val isGradeExpandable = isGradeExpandableKey.getFlowValue(R.bool.pref_default_expand_grade.asBoolean)
    suspend fun setGradeExpandable(value: Boolean) = isGradeExpandableKey.saveValue(value)

    private val Int.asString get() = context.getString(this)
    private val Int.asBoolean get() = context.resources.getBoolean(this)
    private fun <T> Preferences.Key<T>.getFlowValue(default: T) = dataStore.data.map { it[this] ?: default }
    private suspend fun <T> Preferences.Key<T>.saveValue(value: T) = dataStore.edit { it[this] = value }
}
