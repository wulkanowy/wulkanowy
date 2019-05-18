package io.github.wulkanowy.data.repositories.mobiledevice

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.toLocalDate
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileDeviceRemote @Inject constructor(private val api: Api) {

    fun getDevices(student: Student): Single<List<MobileDevice>> {
        return api.getRegisteredDevices().map { devices ->
            devices.map {
                MobileDevice(
                    studentId = student.studentId,
                    date = it.date.toLocalDate(),
                    deviceId = it.id,
                    name = it.name
                )
            }
        }
    }
}
