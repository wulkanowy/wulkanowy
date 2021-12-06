package io.github.wulkanowy.utils

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.fredporciuncula.flow.preferences.Preference
import com.fredporciuncula.flow.preferences.Serializer
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun <T : Any> FlowSharedPreferences.getObject(
    key: String,
    defValue: String,
    serializer: Serializer<T>,
): Preference<T> = getObject(key, serializer, serializer.deserialize(getString(defValue).get()))
