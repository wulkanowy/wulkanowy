package io.github.wulkanowy.ui.modules.grade.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.grade.GradeView
import kotlinx.android.synthetic.main.fragment_grade_statistics.*
import kotlinx.android.synthetic.main.fragment_grade_summary.*
import java.text.DecimalFormat
import javax.inject.Inject

class GradeStatisticsFragment : BaseSessionFragment(), GradeStatisticsView, GradeView.GradeChildView {

    @Inject
    lateinit var presenter: GradeStatisticsPresenter

    companion object {
        fun newInstance() = GradeStatisticsFragment()
    }

    private val gradeColors = listOf(
        6 to R.color.grade_six,
        5 to R.color.grade_five,
        4 to R.color.grade_four,
        3 to R.color.grade_three,
        2 to R.color.grade_two,
        1 to R.color.grade_one
    )

    private val gradeLabels = listOf(
        "6, 6-", "5, 5-, 5+", "4, 4-, 4+", "3, 3-, 3+", "2, 2-, 2+", "1, 1+"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grade_statistics, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = gradeSummaryRecycler
        presenter.onAttachView(this)
    }

    override fun initView() {
        gradeStatisticsChart.run {
            animateXY(1000, 1000)
            legend.setCustom(gradeLabels.mapIndexed { i, it ->
                LegendEntry().apply {
                    label = it
                    formColor = ContextCompat.getColor(context, gradeColors[i].second)
                    form = Legend.LegendForm.SQUARE
                }
            })
        }
    }

    override fun updateData(items: List<GradeStatistics>) {
        val chartItems = items.sortedByDescending { it.grade }.filter { it.amount != 0 }
        gradeStatisticsChart.run {
            data = PieData(PieDataSet(chartItems.map {
                PieEntry(it.amount.toFloat(), it.grade.toString())
            }, "Legenda").apply {
                setColors(chartItems.map {
                    gradeColors.single { color -> color.first == it.grade }.second
                }.toIntArray(), context)
            }).apply {
                setValueFormatter { value, _, _, _ -> "Ocen: " + DecimalFormat("##0").format(value) }
                centerText = "Oceny cząstkowe"
            }
            description = Description().apply { text = "" }
            invalidate()
        }
    }

    override fun onParentLoadData(semesterId: Int, forceRefresh: Boolean) {
        presenter.onParentViewLoadData(semesterId, forceRefresh)
    }

    override fun onParentReselected() {
        presenter.onParentViewChangeSemester()
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

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
