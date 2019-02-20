package io.github.wulkanowy.ui.modules.grade

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.WindowManager
import com.google.android.material.tabs.TabLayout
import timber.log.Timber

/**
 * @see <a href="https://medium.com/@elsenovraditya/set-tab-minimum-width-of-scrollable-tablayout-programmatically-8146d6101efe">Set Tab Minimum Width of Scrollable TabLayout Programmatically</a>
 */
class CustomTabLayout : TabLayout {

    constructor(context: Context) : super(context) {
        initTabMinWidth()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initTabMinWidth()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initTabMinWidth()
    }

    companion object {
        private const val WIDTH_INDEX = 0
        private const val HEIGHT_INDEX = 1
        private const val DIVIDER_FACTOR = 3
    }

    private fun initTabMinWidth() {
        val wh = getScreenSize(context)
        val tabMinWidth = wh[WIDTH_INDEX] / DIVIDER_FACTOR

        try {
            TabLayout::class.java.getDeclaredField("scrollableTabMinWidth").run {
                isAccessible = true
                set(this@CustomTabLayout, tabMinWidth)
            }
        } catch (e: NoSuchFieldException) {
            Timber.e(e)
        } catch (e: IllegalAccessException) {
            Timber.e(e)
        }
    }

    @Suppress("DEPRECATION")
    private fun getScreenSize(context: Context): IntArray {
        val widthHeight = IntArray(2)
        widthHeight[WIDTH_INDEX] = 0
        widthHeight[HEIGHT_INDEX] = 0

        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay

        Point().also {
            display.getSize(it)
            widthHeight[WIDTH_INDEX] = it.x
            widthHeight[HEIGHT_INDEX] = it.y
        }

        if (!isScreenSizeRetrieved(widthHeight)) {
            DisplayMetrics().also {
                display.getMetrics(it)
                widthHeight[0] = it.widthPixels
                widthHeight[1] = it.heightPixels
            }
        }

        // Last defense. Use deprecated API that was introduced in lower than API 13
        if (!isScreenSizeRetrieved(widthHeight)) {
            widthHeight[0] = display.width // deprecated
            widthHeight[1] = display.height // deprecated
        }

        return widthHeight
    }

    private fun isScreenSizeRetrieved(widthHeight: IntArray): Boolean {
        return widthHeight[WIDTH_INDEX] != 0 && widthHeight[HEIGHT_INDEX] != 0
    }
}
