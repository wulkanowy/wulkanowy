package io.github.wulkanowy.services.sync.base

import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest

interface WorkerConfiguration {

    val periodicRequest: PeriodicWorkRequest

    val oneTimeRequest: OneTimeWorkRequest
}
