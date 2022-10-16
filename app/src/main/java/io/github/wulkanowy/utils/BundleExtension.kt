package io.github.wulkanowy.utils

import android.content.Intent
import android.os.Build
import android.os.Bundle
import java.io.Serializable

inline fun <reified T : Serializable> Bundle.serializable(key: String): T = when {
    Build.VERSION.SDK_INT >= 33 -> getSerializable(key, T::class.java)!!
    else -> @Suppress("DEPRECATION") getSerializable(key) as T
}

inline fun <reified T : Serializable> Bundle.nullableSerializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as T?
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T = when {
    Build.VERSION.SDK_INT >= 33 -> getSerializableExtra(key, T::class.java)!!
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as T
}

inline fun <reified T : Serializable> Intent.nullableSerializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as T?
}
