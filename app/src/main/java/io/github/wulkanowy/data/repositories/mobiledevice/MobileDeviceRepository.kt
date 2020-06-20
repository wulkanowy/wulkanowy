package io.github.wulkanowy.data.repositories.mobiledevice

import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileDeviceRepository @Inject constructor(
    private val local: MobileDeviceLocal,
    private val remote: MobileDeviceRemote
) {

    suspend fun refreshDevices(student: Student, semester: Semester) {
        val new = remote.getDevices(student, semester)
        val old = local.getDevices(semester).first()

        local.deleteDevices(old uniqueSubtract new)
        local.saveDevices(new uniqueSubtract old)
    }

    fun getDevices(student: Student, semester: Semester): Flow<List<MobileDevice>> {
        return local.getDevices(semester).map {
            if (it.isNotEmpty()) return@map it
            refreshDevices(student, semester)
            it
        }
    }

    suspend fun unregisterDevice(student: Student, semester: Semester, device: MobileDevice) {
        remote.unregisterDevice(student, semester, device)
        local.deleteDevices(listOf(device))
    }

    suspend fun getToken(student: Student, semester: Semester): MobileDeviceToken {
        return remote.getToken(student, semester)
    }
}
