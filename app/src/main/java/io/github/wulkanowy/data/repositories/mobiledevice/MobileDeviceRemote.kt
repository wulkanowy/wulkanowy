package io.github.wulkanowy.data.repositories.mobiledevice

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.utils.toLocalDateTime
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileDeviceRemote @Inject constructor(private val api: Api) {

    fun getDevices(semester: Semester): Single<List<MobileDevice>> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { api.getRegisteredDevices() }
            .map { devices ->
                devices.map {
                    MobileDevice(
                        studentId = semester.studentId,
                        date = it.date.toLocalDateTime(),
                        deviceId = it.id,
                        name = it.name
                    )
                }
            }
    }

    fun unregisterDevice(semester: Semester, device: MobileDevice): Single<Boolean> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { api.unregisterDevice(device.deviceId) }
    }

    data class Token(val token: String, val symbol: String, val pin: String, val qr: String)

    fun getToken(semester: Semester): Single<Token> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { api.getToken() }
            .map {
                Token(
                    token = it.token,
                    symbol = it.symbol,
                    pin = it.pin,
                    qr = it.qrCodeImage
                )
            }
    }
}
