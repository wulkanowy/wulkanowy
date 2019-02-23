package io.github.wulkanowy.services.sync

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import io.github.wulkanowy.services.sync.channels.SyncChannel
import javax.inject.Inject

class SyncManager @Inject constructor(private val syncChannel: SyncChannel) {

    fun initialize() {
        if (SDK_INT >= O) syncChannel.create()
    }
}

