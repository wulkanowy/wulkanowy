package io.github.wulkanowy.data.repositories.mobiledevice

import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileDeviceRemote @Inject constructor(private val sdk: SdkHelper) {

    fun getDevices(semester: Semester): Single<List<MobileDevice>> {
        return sdk.changeSemester(semester).getRegisteredDevices()
            .map { devices ->
                devices.map {
                    MobileDevice(
                        studentId = semester.studentId,
                        date = it.date,
                        deviceId = it.id,
                        name = it.name
                    )
                }
            }
    }

    fun unregisterDevice(semester: Semester, device: MobileDevice): Single<Boolean> {
        return sdk.changeSemester(semester).unregisterDevice(device.deviceId)
    }

    fun getToken(semester: Semester): Single<MobileDeviceToken> {
        return sdk.changeSemester(semester).getToken()
            .map {
                MobileDeviceToken(
                    token = it.token,
                    symbol = it.symbol,
                    pin = it.pin,
                    qr = it.qrCodeImage
                )
            }
    }
}
