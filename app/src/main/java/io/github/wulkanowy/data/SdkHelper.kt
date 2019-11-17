package io.github.wulkanowy.data

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import java.net.URL
import javax.inject.Inject

class SdkHelper @Inject constructor(private val sdk: Sdk) {

    fun init(student: Student) {
        sdk.apply {
            email = student.email
            password = student.password
            symbol = student.symbol
            schoolSymbol = student.schoolSymbol
            studentId = student.studentId
            classId = student.classId
            if (Sdk.Mode.valueOf(student.loginMode) != Sdk.Mode.API) {
                scrapperHost = URL(student.scrapperBaseUrl).run { host + ":$port".removeSuffix(":-1") }
                ssl = student.scrapperBaseUrl.startsWith("https")
                loginType = Sdk.ScrapperLoginType.valueOf(student.loginType)
            }
            loginId = student.userLoginId

            mode = Sdk.Mode.valueOf(student.loginMode)
            apiBaseUrl = student.apiBaseUrl
            certKey = student.certificateKey
            privateKey = student.privateKey
        }
    }
}
