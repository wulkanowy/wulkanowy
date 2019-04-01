package io.github.wulkanowy.ui.modules.timetablewidget

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getStudentWidgetKey
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class TimetableWidgetConfigurePresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository,
    private val sharedPref: SharedPrefHelper
) : BasePresenter<TimetableWidgetConfigureView>(errorHandler) {

    private var widgetId: Int? = null

    private var isFromProvider = false

    fun onAttachView(view: TimetableWidgetConfigureView, widgetId: Int?, isFromProvider: Boolean?) {
        super.onAttachView(view)
        this.widgetId = widgetId
        this.isFromProvider = isFromProvider ?: false
        view.initView()
        loadData()
    }

    fun onItemSelect(item: AbstractFlexibleItem<*>) {
        if (item is TimetableWidgetConfigureItem) {
            registerStudent(item.student)
        }
    }

    private fun loadData() {
        disposable.add(studentRepository.getSavedStudents(false)
            .map { it.map { student -> TimetableWidgetConfigureItem(student) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.backgroundThread)
            .subscribe({
                when {
                    it.isEmpty() -> view?.openLoginView()
                    it.size == 1 && !isFromProvider -> registerStudent(it.single().student)
                    else -> view?.updateData(it)
                }
            }, { errorHandler.dispatch(it) }))
    }

    private fun registerStudent(student: Student) {
        widgetId?.also {
            sharedPref.putLong(getStudentWidgetKey(it), student.id)
            view?.apply {
                updateTimetableWidget(it)
                setSuccessResult(it)
            }
        }
        view?.finishView()
    }
}