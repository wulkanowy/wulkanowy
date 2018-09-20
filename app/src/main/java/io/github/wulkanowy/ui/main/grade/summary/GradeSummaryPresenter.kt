package io.github.wulkanowy.ui.main.grade.summary

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.calcAverage
import io.github.wulkanowy.utils.calcSummaryAverage
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import java.lang.String.format
import java.util.Locale.FRANCE
import javax.inject.Inject

class GradeSummaryPresenter @Inject constructor(
        private val errorHandler: ErrorHandler,
        private val gradeSummaryRepository: GradeSummaryRepository,
        private val gradeRepository: GradeRepository,
        private val sessionRepository: SessionRepository,
        private val schedulers: SchedulersManager)
    : BasePresenter<GradeSummaryView>(errorHandler) {

    private var selectedSemester = "0"

    override fun attachView(view: GradeSummaryView) {
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
                .map { semester -> semester.first { it.semesterId == semesterId } }
                .flatMap {
                    gradeSummaryRepository.getGradesSummary(it, forceRefresh)
                            .flatMap { gradesSummary ->
                                gradeRepository.getGrades(it, forceRefresh)
                                        .map { grades ->
                                            grades.groupBy { grade -> grade.subject }
                                                    .mapValues { entry -> calcAverage(entry.value) }
                                                    .let { averages ->
                                                        DataContainer(
                                                                createGradeSummaryItems(gradesSummary, averages),
                                                                formatAverage(calcSummaryAverage(gradesSummary)),
                                                                formatAverage(averages.values.average().toFloat())
                                                        )
                                                    }
                                        }
                            }
                }
                .subscribeOn(schedulers.backgroundThread())
                .observeOn(schedulers.mainThread())
                .doFinally {
                    view?.run {
                        showProgress(false)
                        showRefresh(false)
                    }
                }.doAfterSuccess {
                    view?.run {
                        showContent(it.gradesSummaryItem.isNotEmpty())
                        showEmpty(it.gradesSummaryItem.isEmpty())
                    }
                }
                .subscribe({ view?.updateDataSet(it.gradesSummaryItem, it.finalAvg, it.calculatedAvg) })
                { errorHandler.proceed(it) })
    }

    private fun createGradeSummaryItems(gradesSummary: List<GradeSummary>, averages: Map<String, Float>)
            : List<GradeSummaryItem> {
        return gradesSummary.filter { !checkEmpty(it, averages) }
                .flatMap {
                    GradeSummaryHeader().apply {
                        average = formatAverage(averages.getOrElse(it.subject) { 0f }, "")
                        name = it.subject
                    }.let { header ->
                        listOf(GradeSummaryItem(header).apply {
                            grade = it.predictedGrade
                            title = view?.predictedString().orEmpty()
                        }, GradeSummaryItem(header).apply {
                            grade = it.finalGrade
                            title = view?.finalString().orEmpty()
                        }
                        )
                    }
                }
    }


    private fun checkEmpty(gradeSummary: GradeSummary, averages: Map<String, Float>): Boolean {
        return gradeSummary.run {
            finalGrade.isEmpty() && predictedGrade.isEmpty() && averages[subject] == null
        }
    }

    private fun formatAverage(average: Float, defaultValue: String = "-- --"): String {
        return if (average == 0f || average.isNaN()) defaultValue
        else format(FRANCE, "%.2f", average)
    }

    data class DataContainer(var gradesSummaryItem: List<GradeSummaryItem>, var finalAvg: String,
                             var calculatedAvg: String)
}
