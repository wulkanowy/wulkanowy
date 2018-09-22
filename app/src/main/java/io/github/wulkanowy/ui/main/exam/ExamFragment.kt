package io.github.wulkanowy.ui.main.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.utils.extension.isHolidays
import io.github.wulkanowy.utils.extension.setOnItemClickListener
import io.github.wulkanowy.utils.extension.setOnUpdateListener
import io.github.wulkanowy.utils.extension.toFormat
import kotlinx.android.synthetic.main.fragment_exam.*
import org.threeten.bp.LocalDate
import javax.inject.Inject

class ExamFragment : BaseFragment(), ExamView {

    @Inject
    lateinit var presenter: ExamPresenter

    @Inject
    lateinit var examAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = ExamFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exam, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.run {
            attachView(this@ExamFragment)
            loadData()
        }
    }

    override fun initView() {
        examAdapter.run {
            setOnUpdateListener { presenter.onUpdateDataList(it) }
            setOnItemClickListener { presenter.onExamItemSelected(getItem(it)) }
        }
        examRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = examAdapter
        }
        examSwipe.setOnRefreshListener { presenter.loadData(forceRefresh = true) }
        examPreviousButton.setOnClickListener {
            presenter.loadExamsForDate(presenter.currentDate.minusDays(7))
        }
        examNextButton.setOnClickListener {
            presenter.loadExamsForDate(presenter.currentDate.plusDays(7))
        }
    }

    override fun updateData(data: List<ExamItem>) {
        examAdapter.updateDataSet(data, true)
    }

    override fun updateWeekNavigation(date: LocalDate) {
        examPreviousButton.visibility = if (date.minusDays(7).isHolidays()) INVISIBLE else VISIBLE
        examNextButton.visibility = if (date.plusDays(7).isHolidays()) INVISIBLE else VISIBLE
        examNavDate.text = String.format("%s-%s", date.toFormat("dd.MM"), date.plusDays(4).toFormat("dd.MM"))
    }

    override fun showEmpty(show: Boolean) {
        examEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showProgress(show: Boolean) {
        examProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        examRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showRefresh(show: Boolean) {
        examSwipe.isRefreshing = show
    }

    override fun showExamDialog(exam: Exam) {
        ExamDialog.newInstance(exam).show(fragmentManager, exam.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }
}
