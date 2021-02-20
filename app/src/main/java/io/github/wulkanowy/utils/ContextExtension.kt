package io.github.wulkanowy.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.text.TextPaint
import android.util.DisplayMetrics.DENSITY_DEFAULT
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.TypefaceCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import io.github.wulkanowy.BuildConfig.APPLICATION_ID

@ColorInt
fun Context.getThemeAttrColor(@AttrRes colorAttr: Int): Int {
    val array = obtainStyledAttributes(null, intArrayOf(colorAttr))
    return try {
        array.getColor(0, 0)
    } finally {
        array.recycle()
    }
}

@ColorInt
fun Context.getThemeAttrColor(@AttrRes colorAttr: Int, alpha: Int): Int {
    return ColorUtils.setAlphaComponent(getThemeAttrColor(colorAttr), alpha)
}

@ColorInt
fun Context.getCompatColor(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

fun Context.getCompatDrawable(@DrawableRes drawableRes: Int) =
    ContextCompat.getDrawable(this, drawableRes)

fun Context.openInternetBrowser(uri: String, onActivityNotFound: (uri: String) -> Unit) {
    Intent.parseUri(uri, 0).let {
        if (it.resolveActivity(packageManager) != null) startActivity(it)
        else onActivityNotFound(uri)
    }
}

fun Context.openAppInMarket(onActivityNotFound: (uri: String) -> Unit) {
    openInternetBrowser("market://details?id=${APPLICATION_ID}") {
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
    startActivity(intent)
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

fun Context.dpToPx(dp: Float) = dp * resources.displayMetrics.densityDpi / DENSITY_DEFAULT

@SuppressLint("DefaultLocale")
fun Context.createNameInitialsDrawable(text: String, backgroundColor: Long): RoundedBitmapDrawable {
    val firstChar =
        text.split(" ").let { "${it.getOrElse(0) { "" }.first()}${it.getOrElse(1) { "" }.first()}" }
    val bounds = Rect()
    val dimension = this.dpToPx(64f).toInt()
    val paint = TextPaint().apply {
        typeface = TypefaceCompat.create(
            this@createNameInitialsDrawable,
            Typeface.create("sans-serif-light", Typeface.NORMAL),
            Typeface.NORMAL
        )
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        textSize = this@createNameInitialsDrawable.dpToPx(32f)
        getTextBounds(firstChar, 0, 1, bounds)
    }

    val bitmap = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888)
        .applyCanvas {
            drawColor(backgroundColor.toInt())
            drawText(
                firstChar,
                0,
                2,
                (dimension / 2).toFloat(),
                (dimension / 2 + (bounds.bottom - bounds.top) / 2).toFloat(),
                paint
            )
        }

    return RoundedBitmapDrawableFactory.create(this.resources, bitmap).apply { isCircular = true }
}
