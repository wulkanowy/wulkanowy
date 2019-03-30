package io.github.wulkanowy.ui.widgets.timetable

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.Intent
import android.os.Bundle
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity

class TimetableWidgetAccountActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(R.layout.dialog_account)

        intent.extras?.getInt(EXTRA_APPWIDGET_ID)?.let {
            setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_APPWIDGET_ID, it) })
        }
    }
}
