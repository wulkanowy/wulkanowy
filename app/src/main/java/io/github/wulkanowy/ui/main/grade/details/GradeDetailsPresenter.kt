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

    private var selectedSemester = "0"

    override fun attachView(view: GradeDetailsView) {
        super.attachView(view)
        view.initView()
    }

    fun onRefresh() {
        if (selectedSemester != "0") loadData(selectedSemester, true)
    }

    fun onLoadData(semesterId: String) {
        if (semesterId != selectedSemester) {
            view?.run {
                showProgress(true)
                showContent(false)
                showEmpty(false)
            }
            loadData(semesterId)
            selectedSemester = semesterId
        }
    }

    private fun loadData(semesterId: String, forceRefresh: Boolean = false) {
        disposable.add(sessionRepository.getSemesters()
                .flatMap { semesters ->
                    gradeRepository.getGrades(semesters.first { it.semesterId == semesterId }, forceRefresh)
                            .map {
                                createGradeItems(it.groupBy { grade -> grade.subject }.toSortedMap())
                            }
                }
                .subscribeOn(schedulers.backgroundThread())
                .observeOn(schedulers.mainThread())
                .doFinally {
                    view?.run {
                        showRefresh(false)
                        showProgress(false)
                        dataLoaded()
                    }
                }
                .doAfterSuccess {
                    view?.run {
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                    }
                }
                .subscribe({ view?.updateData(it) }) { errorHandler.proceed(it) })
    }

    fun onGradeItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is GradeDetailsItem) view?.showGradeDialog(item.grade)
    }

    fun onUpdateDataList(size: Int) {
        if (size != 0) view?.showProgress(false)
    }

    private fun createGradeItems(items: Map<String, List<Grade>>): List<GradeDetailsHeader> {
        return items.map {
            val gradesAverage = calcAverage(it.value)
            GradeDetailsHeader().apply {
                subject = it.key
                average = view?.run {
                    if (gradesAverage == 0f) emptyAverageString()
                    else averageString().format(gradesAverage)
                }.orEmpty()
                number = view?.gradeNumberString(it.value.size).orEmpty()
                subItems = (it.value.reversed().map { item ->
                    GradeDetailsItem().apply {
                        grade = item
                        weightString = view?.weightString().orEmpty()
                        valueColor = getValueColor(item.value)
                    }
                })
            }
        }
    }
}
