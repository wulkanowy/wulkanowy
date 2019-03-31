package io.github.wulkanowy.ui.modules.timetablewidget

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.EXTRA_STUDENT
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.activity_timetable_widget_configure.*
import java.io.Serializable
import javax.inject.Inject

class TimetableWidgetConfigureActivity : BaseActivity(), TimetableWidgetConfigureView {

    @Inject
    lateinit var configureAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    @Inject
    lateinit var presenter: TimetableWidgetConfigurePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(R.layout.activity_timetable_widget_configure)

        presenter.onAttachView(this, intent.extras?.getInt(EXTRA_APPWIDGET_ID))
    }

    override fun initView() {
        timetableWidgetConfigureRecycler.apply {
            adapter = configureAdapter
            layoutManager = SmoothScrollLinearLayoutManager(context)
        }
        configureAdapter.setOnItemClickListener { presenter.onItemSelect(it) }
    }

    override fun updateData(data: List<TimetableWidgetConfigureItem>) {
        configureAdapter.updateDataSet(data)
    }

    override fun updateTimetableWidget(widgetId: Int, student: Serializable) {
        sendBroadcast(Intent(this, TimetableWidgetProvider::class.java)
            .apply {
                action = ACTION_APPWIDGET_UPDATE
                putExtra(EXTRA_APPWIDGET_IDS, intArrayOf(widgetId))
                putExtra(EXTRA_STUDENT, student)

            })
    }

    override fun setSuccessResult(widgetId: Int) {
        setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_APPWIDGET_ID, widgetId) })
    }

    override fun showError(text: String, error: Throwable) {
        Toast.makeText(this, text, LENGTH_LONG).show()
    }

    override fun finishView() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetachView()
    }

    /*private fun doo() {
        setResult(RESULT_CANCELED)
        setContentView(R.layout.activity_timetable_widget_configure)
        timetableWidgetAccountActivityRecycler.adapter = adapter
        timetableWidgetAccountActivityRecycler.layoutManager = SmoothScrollLinearLayoutManager(this)

        adapter.setOnItemClickListener { item ->
            if (item is TimetableWidgetConfigureItem) {
                intent.extras?.getInt(EXTRA_APPWIDGET_ID)?.let {
                    sharedPref.putLong("timetable_widget_student_$it", item.student.id)
                    setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_APPWIDGET_ID, it) })

                    val intent = Intent(this, TimetableWidgetProvider::class.java).apply {
                        action = ACTION_APPWIDGET_UPDATE
                        putExtra(EXTRA_APPWIDGET_IDS, intArrayOf(it))
                    }
                    sendBroadcast(intent)
                }
            }
            finish()
        }
    }*/
}
