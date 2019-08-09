package io.github.wulkanowy.data

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import java.net.URL
import javax.inject.Inject

class SdkHelper @Inject constructor(private val api: Sdk) {

    fun initApi(student: Student) {
        api.apply {
            email = student.email
            password = student.password
            symbol = student.symbol
            schoolSymbol = student.schoolSymbol
            studentId = student.studentId
            classId = student.classId
            scrapperHost = URL(student.scrapperBaseUrl).run { host + ":$port".removeSuffix(":-1") }
            ssl = student.scrapperBaseUrl.startsWith("https")
            loginType = Sdk.ScrapperLoginType.valueOf(student.loginType)
            loginId = student.userLoginId

            mode = Sdk.Mode.valueOf(student.loginMode)
            apiBaseUrl = student.apiBaseUrl
            apiKey = student.apiKey
            certKey = student.certificateKey
            certificate = student.certificate
        }
    }

    fun initApi(email: String, password: String, symbol: String, endpoint: String, mode: Sdk.Mode, apiKey: String) {
        api.apply {
            this.email = email
            this.password = password
            this.symbol = symbol
            this.apiKey = apiKey
            this.mode = mode
            certKey = "" // clear certificate on getStudent()
            certificate = ""
            scrapperHost = URL(endpoint).run { host + ":$port".removeSuffix(":-1") }
            ssl = endpoint.startsWith("https")
        }
    }
}
