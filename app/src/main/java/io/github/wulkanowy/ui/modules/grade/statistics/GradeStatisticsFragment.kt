package io.github.wulkanowy.ui.modules.grade.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.grade.GradeView
import kotlinx.android.synthetic.main.fragment_grade_summary.*
import javax.inject.Inject

class GradeStatisticsFragment : BaseSessionFragment(), GradeStatisticsView, GradeView.GradeChildView {

    @Inject
    lateinit var presenter: GradeStatisticsPresenter

    companion object {
        fun newInstance() = GradeStatisticsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grade_statistics, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = gradeSummaryRecycler
        presenter.onAttachView(this)
    }

    override fun initView() {
    }

    override fun onParentChangeSemester() {
    }

    override fun onParentLoadData(semesterId: Int, forceRefresh: Boolean) {
    }

    override fun onParentReselected() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
