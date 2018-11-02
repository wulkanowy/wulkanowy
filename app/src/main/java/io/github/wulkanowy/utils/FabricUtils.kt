package io.github.wulkanowy.utils

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.crashlytics.android.answers.LoginEvent
import com.crashlytics.android.answers.SignUpEvent

fun logLogin(method: String, result: Boolean = true) {
    Answers.getInstance().logLogin(LoginEvent()
        .putMethod(method)
        .putSuccess(result)
    )
}

fun logRegister(message: String, students: Int, result: Boolean, symbol: String, endpoint: String) {
    Answers.getInstance().logSignUp(SignUpEvent()
        .putMethod("Login activity")
        .putSuccess(result)
        .putCustomAttribute("students", students)
        .putCustomAttribute("symbol", symbol)
        .putCustomAttribute("message", message)
        .putCustomAttribute("endpoint", endpoint)
    )
}

fun logEvent(name: String, params: Map<String, Any>) {
    Answers.getInstance().logCustom(CustomEvent(name)
        .apply {
            params.forEach {
                if (it.value is Number) putCustomAttribute(it.key, it.value as Number)
                else putCustomAttribute(it.key, it.value as String)
            }
        }
    )
}
