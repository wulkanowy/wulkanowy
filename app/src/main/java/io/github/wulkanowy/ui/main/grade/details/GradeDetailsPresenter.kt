package io.github.wulkanowy.ui.main.grade.details

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.calcAverage
import io.github.wulkanowy.utils.getValueColor
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import javax.inject.Inject

class GradeDetailsPresenter @Inject constructor(
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersManager,
        private val gradeRepository: GradeRepository,
        private val sessionRepository: SessionRepository) : BasePresenter<GradeDetailsView>(errorHandler) {

    override fun attachView(view: GradeDetailsView) {
        super.attachView(view)
        view.initView()
    }

    fun loadData(semesterId: String, forceRefresh: Boolean) {
        disposable.add(sessionRepository.getSemesters()
                .flatMap { gradeRepository.getGrades(it.first { item -> item.semesterId == semesterId }, forceRefresh) }
                .map { createGradeItems(it.groupBy { grade -> grade.subject }.toSortedMap()) }
                .subscribeOn(schedulers.backgroundThread())
                .observeOn(schedulers.mainThread())
                .doFinally {
                    view?.run {
                        showRefresh(false)
                        showProgress(false)
                        notifyParentDataLoaded(semesterId)
                    }
                }
                .subscribe({
                    view?.run {
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                        updateData(it)
                    }
                }) {
                    view?.run { showEmpty(isViewEmpty()) }
                    errorHandler.proceed(it)
                })
    }

    fun onGradeItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is GradeDetailsItem) view?.showGradeDialog(item.grade)
    }

    fun onSwipeRefresh() {
        view?.notifyParentRefresh()
    }

    fun onParentViewReselected() {
        view?.run {
            if (!isViewEmpty()) resetView()
        }
    }

    fun onParentChangeSemester() {
        view?.run {
            showProgress(true)
            showRefresh(false)
            showContent(false)
            showEmpty(false)
            clearView()
        }
        disposable.clear()
    }

    private fun createGradeItems(items: Map<String, List<Grade>>): List<GradeDetailsHeader> {
        return items.map {
            calcAverage(it.value).let { average ->
                GradeDetailsHeader(
                        subject = it.key,
                        average = formatAverage(average),
                        number = view?.gradeNumberString(it.value.size).orEmpty()
                ).apply {
                    subItems = it.value.map { item ->
                        GradeDetailsItem(
                                grade = item,
                                weightString = view?.weightString().orEmpty(),
                                valueColor = getValueColor(item)
                        )
                    }
                }
            }
        }
    }

    private fun formatAverage(average: Float): String {
        return view?.run {
            if (average == 0f) emptyAverageString()
            else averageString().format(average)
        }.orEmpty()
    }
}
