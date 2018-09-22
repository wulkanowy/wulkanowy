package io.github.wulkanowy.ui.main.grade.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.main.grade.GradeFragment
import io.github.wulkanowy.ui.main.grade.GradeView
import kotlinx.android.synthetic.main.fragment_grade_summary.*
import javax.inject.Inject

class GradeSummaryFragment : BaseFragment(), GradeSummaryView, GradeView.GradeChildView {

    @Inject
    lateinit var presenter: GradeSummaryPresenter

    @Inject
    lateinit var gradeSummaryAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = GradeSummaryFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grade_summary, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.attachView(this)
    }

    override fun initView() {
        gradeSummaryAdapter.setDisplayHeadersAtStartUp(true)

        gradeSummaryRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = gradeSummaryAdapter
            isNestedScrollingEnabled = false
        }
        gradeSummarySwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
    }

    override fun updateDataSet(data: List<GradeSummaryItem>, finalAvg: String, calculatedAvg: String) {
        gradeSummaryAdapter.updateDataSet(data)
        gradeSummaryFinalAverage.text = finalAvg
        gradeSummaryCalculatedAverage.text = calculatedAvg
    }

    override fun loadData(semesterId: String, forceRefresh: Boolean) {
        presenter.loadData(semesterId, forceRefresh)
    }

    override fun onDataLoaded(semesterId: String) {
        (parentFragment as? GradeFragment)?.onChildFragmentLoaded(semesterId)
    }

    override fun onSwipeRefresh() {
        (parentFragment as? GradeFragment)?.onChildRefresh()
    }

    override fun notifyShowProgress(showProgress: Boolean) {
        presenter.onParentShowProgress(showProgress)
    }

    override fun showContent(show: Boolean) {
        gradeSummaryContent.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showProgress(show: Boolean) {
        gradeSummaryProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showRefresh(show: Boolean) {
        gradeSummarySwipe.isRefreshing = show
    }

    override fun showEmpty(show: Boolean) {
        gradeSummaryEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun predictedString() = getString(R.string.grade_summary_predicted_average)

    override fun finalString() = getString(R.string.grade_summary_final_average)

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }
}
