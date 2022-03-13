package io.github.wulkanowy.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import io.github.wulkanowy.BuildConfig

fun Context.openInternetBrowser(uri: String, onActivityNotFound: (uri: String) -> Unit = {}) {
    Intent.parseUri(uri, 0).let {
        try {
            startActivity(it)
        } catch (e: ActivityNotFoundException) {
            onActivityNotFound(uri)
        }
    }
}

fun Context.openAppInMarket(onActivityNotFound: (uri: String) -> Unit) {
    openInternetBrowser("market://details?id=${BuildConfig.APPLICATION_ID}") {
        openInternetBrowser("https://github.com/wulkanowy/wulkanowy/releases", onActivityNotFound)
    }
}

fun Context.openEmailClient(
    chooserTitle: String,
    email: String,
    subject: String,
    body: String,
    onActivityNotFound: () -> Unit = {}
) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(Intent.createChooser(intent, chooserTitle))
    } else onActivityNotFound()
}

fun Context.openNavigation(location: String) {
    val intentUri = Uri.parse("geo:0,0?q=${Uri.encode(location)}")
    val intent = Intent(Intent.ACTION_VIEW, intentUri)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

fun Context.openDialer(phone: String) {
    val intentUri = Uri.parse("tel:$phone")
    val intent = Intent(Intent.ACTION_DIAL, intentUri)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

fun Context.shareText(text: String, subject: String?) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        if (subject != null) {
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}
