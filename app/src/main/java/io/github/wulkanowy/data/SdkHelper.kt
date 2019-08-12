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
            apiKey = student.apiKey
            certKey = student.certificateKey
            certificate = student.certificate
        }
    }

    fun initApi(pin: String, symbol: String, token: String, apiKey: String) {
        sdk.apply {
            this.mode = Sdk.Mode.API
            this.pin = pin
            this.symbol = symbol
            this.token = token
            this.apiKey = apiKey

            certKey = "" // clear certificate on getStudent()
            certificate = ""
        }
    }

    fun initScrapper(email: String, password: String, endpoint: String, symbol: String) {
        sdk.apply {
            this.mode = Sdk.Mode.SCRAPPER
            this.email = email
            this.password = password
            this.symbol = symbol

            scrapperHost = URL(endpoint).run { host + ":$port".removeSuffix(":-1") }
            ssl = endpoint.startsWith("https")
        }
    }

    fun initHybrid(email: String, password: String, symbol: String, endpoint: String, apiKey: String) {
        initApi("", symbol, "", apiKey)
        initScrapper(email, password, endpoint, symbol)
        sdk.mode = Sdk.Mode.HYBRID
    }
}
