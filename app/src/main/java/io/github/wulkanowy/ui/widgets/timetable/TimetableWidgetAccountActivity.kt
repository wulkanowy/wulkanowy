package io.github.wulkanowy.ui.widgets.timetable

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.setOnItemClickListener
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_widget_account.*
import javax.inject.Inject

class TimetableWidgetAccountActivity : BaseActivity() {

    private val disposable: CompositeDisposable = CompositeDisposable()

    @Inject
    lateinit var studentRepository: StudentRepository

    @Inject
    lateinit var adapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    @Inject
    lateinit var schedulers: SchedulersProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(R.layout.activity_widget_account)
        timetableWidgetAccountActivityRecycler.adapter = adapter
        timetableWidgetAccountActivityRecycler.layoutManager = SmoothScrollLinearLayoutManager(this)

        adapter.setOnItemClickListener {
            intent.extras?.getInt(EXTRA_APPWIDGET_ID)?.let {
                setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_APPWIDGET_ID, it) })
            }
            finish()
        }

        disposable.add(studentRepository.getSavedStudents(false)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .map { it.map { student -> TimetableWidgetAccountItem(student) } }
            .subscribe({
                adapter.updateDataSet(it)
            }, { Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show() }))
    }
}
