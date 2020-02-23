package io.github.wulkanowy.ui.modules.grade.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.grade.GradeView
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.setOnItemSelectedListener
import kotlinx.android.synthetic.main.fragment_grade_statistics.*
import javax.inject.Inject

class GradeStatisticsFragment : BaseFragment(), GradeStatisticsView, GradeView.GradeChildView {

    @Inject
    lateinit var presenter: GradeStatisticsPresenter

    @Inject
    lateinit var statisticsAdapter: GradeStatisticsAdapter

    private lateinit var subjectsAdapter: ArrayAdapter<String>

    companion object {
        private const val SAVED_CHART_TYPE = "CURRENT_TYPE"

        fun newInstance() = GradeStatisticsFragment()
    }

    override val isPieViewEmpty get() = statisticsAdapter.items.isEmpty()

    override val isBarViewEmpty get() = statisticsAdapter.items.isEmpty()

    override val currentType
        get() = when (gradeStatisticsTypeSwitch.checkedRadioButtonId) {
            R.id.gradeStatisticsTypeSemester -> ViewType.SEMESTER
            R.id.gradeStatisticsTypePartial -> ViewType.PARTIAL
            else -> ViewType.POINTS
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grade_statistics, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = gradeStatisticsSwipe
        presenter.onAttachView(this, savedInstanceState?.getSerializable(SAVED_CHART_TYPE) as? ViewType)
    }

    override fun initView() {
        with(gradeStatisticsRecycler) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = statisticsAdapter
        }

        subjectsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        subjectsAdapter.setDropDownViewResource(R.layout.item_attendance_summary_subject)

        with(gradeStatisticsSubjects) {
            adapter = subjectsAdapter
            setOnItemSelectedListener<TextView> { presenter.onSubjectSelected(it?.text?.toString()) }
        }

        gradeStatisticsSubjectsContainer.setElevationCompat(requireContext().dpToPx(1f))

        gradeStatisticsSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
        gradeStatisticsErrorRetry.setOnClickListener { presenter.onRetry() }
        gradeStatisticsErrorDetails.setOnClickListener { presenter.onDetailsClick() }
    }

    override fun updateSubjects(data: ArrayList<String>) {
        with(subjectsAdapter) {
            clear()
            addAll(data)
            notifyDataSetChanged()
        }
    }

    override fun updatePieData(items: List<GradeStatistics>, theme: String) {
        statisticsAdapter.theme = theme
        statisticsAdapter.items = listOf(items)
        statisticsAdapter.notifyDataSetChanged()
    }

    override fun updateBarData(item: GradePointsStatistics) {
        statisticsAdapter.items = listOf(item)
        statisticsAdapter.notifyDataSetChanged()
    }

    override fun showSubjects(show: Boolean) {
        gradeStatisticsSubjectsContainer.visibility = if (show) View.VISIBLE else View.INVISIBLE
        gradeStatisticsTypeSwitch.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun clearView() {
        statisticsAdapter.items = emptyList()
    }

    override fun showPieContent(show: Boolean) {
        gradeStatisticsRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showBarContent(show: Boolean) {
        gradeStatisticsRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showEmpty(show: Boolean) {
        gradeStatisticsEmpty.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showErrorView(show: Boolean) {
        gradeStatisticsError.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setErrorDetails(message: String) {
        gradeStatisticsErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        gradeStatisticsProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun enableSwipe(enable: Boolean) {
        gradeStatisticsSwipe.isEnabled = enable
    }

    override fun showRefresh(show: Boolean) {
        gradeStatisticsSwipe.isRefreshing = show
    }

    override fun onParentLoadData(semesterId: Int, forceRefresh: Boolean) {
        presenter.onParentViewLoadData(semesterId, forceRefresh)
    }

    override fun onParentReselected() {
        gradeStatisticsRecycler.scrollToPosition(0)
    }

    override fun onParentChangeSemester() {
        presenter.onParentViewChangeSemester()
    }

    override fun notifyParentDataLoaded(semesterId: Int) {
        (parentFragment as? GradeFragment)?.onChildFragmentLoaded(semesterId)
    }

    override fun notifyParentRefresh() {
        (parentFragment as? GradeFragment)?.onChildRefresh()
    }

    override fun onResume() {
        super.onResume()
        gradeStatisticsTypeSwitch.setOnCheckedChangeListener { _, _ -> presenter.onTypeChange() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SAVED_CHART_TYPE, presenter.currentType)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
