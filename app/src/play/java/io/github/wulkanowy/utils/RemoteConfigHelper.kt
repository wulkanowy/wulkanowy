package io.github.wulkanowy.utils

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigHelper @Inject constructor(
    @ApplicationContext context: Context,
    private val appInfo: AppInfo,
) : BaseRemoteConfigHelper() {

    private val remoteConfig by lazy {
        FirebaseApp.initializeApp(context)
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(remoteConfigSettings {
                fetchTimeoutInSeconds = 3
                if (appInfo.isDebug) {
                    minimumFetchIntervalInSeconds = 0
                }
            })
        }
    }

    init {
        remoteConfig.setDefaultsAsync(RemoteConfigDefaults.values().associate {
            it.key to it.value
        })
    }

    override val userAgentTemplate: String
        get() = remoteConfig.getString(RemoteConfigDefaults.USER_AGENT_TEMPLATE.key)

    override fun fetchAndActivate(callback: (RemoteConfigHelper) -> Unit) {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.d("Fetch and activate succeeded: ${task.result}")
            } else {
                Timber.d("Fetch failed")
            }
            callback(this)
        }
    }
}
