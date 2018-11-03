package io.github.wulkanowy.utils

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.crashlytics.android.answers.LoginEvent
import com.crashlytics.android.answers.SignUpEvent
import timber.log.Timber

fun logLogin(method: String) {
    Answers.getInstance().logLogin(LoginEvent().putMethod(method))
}

fun logRegister(message: String, result: Boolean, symbol: String, endpoint: String) {
    Answers.getInstance().logSignUp(SignUpEvent()
        .putMethod("Login activity")
        .putSuccess(result)
        .putCustomAttribute("symbol", symbol)
        .putCustomAttribute("message", message)
        .putCustomAttribute("endpoint", endpoint)
    )
}

fun <T> logEvent(name: String, params: Map<String, T>) {
    Answers.getInstance().logCustom(CustomEvent(name)
        .apply {
            params.forEach {
                when {
                    it.value is String -> putCustomAttribute(it.key, it.value as String)
                    it.value is Number -> putCustomAttribute(it.key, it.value as Number)
                    it.value is Boolean -> putCustomAttribute(it.key, if ((it.value as Boolean)) "true" else "false")
                    else -> Timber.w("logEvent() unknown value type: ${it.value}")
                }
            }
        }
    )
}
