package io.github.wulkanowy.ui.modules.attendance.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import kotlinx.android.synthetic.main.fragment_attendance_summary.*
import javax.inject.Inject

class AttendanceSummaryFragment : BaseFragment(), AttendanceSummaryView, MainView.TitledView {

    @Inject
    lateinit var presenter: AttendanceSummaryPresenter

    @Inject
    lateinit var attendanceSummaryAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    private lateinit var subjectsAdapter: ArrayAdapter<String>

    companion object {
        private const val SAVED_SUBJECT_KEY = "CURRENT_SUBJECT"
        fun newInstance() = AttendanceSummaryFragment()
    }

    override val titleStringId: Int
        get() = R.string.attendance_title

    override val isViewEmpty
        get() = attendanceSummaryAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_attendance_summary, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = attendanceSummaryRecycler
        presenter.onAttachView(this, savedInstanceState?.getInt(SAVED_SUBJECT_KEY))
    }

    override fun initView() {
        context?.run {
            subjectsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ArrayList<String>())
            subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        attendanceSummaryAdapter.setDisplayHeadersAtStartUp(true)

        attendanceSummaryRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = attendanceSummaryAdapter
        }
        attendanceSummarySwipe.setOnRefreshListener { presenter.onSwipeRefresh() }

        attendanceSummarySubjects.run {
            adapter = subjectsAdapter
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    presenter.loadForSubject((view as TextView).text.toString())
                }
            }
        }
    }

    override fun updateSubjects(data: ArrayList<String>) {
        subjectsAdapter.run {
            clear()
            addAll(data)
            notifyDataSetChanged()
        }
    }

    override fun updateDataSet(data: List<AttendanceSummaryItem>, header: AttendanceSummaryScrollableHeader) {
        attendanceSummaryAdapter.apply {
            updateDataSet(data, true)
            removeAllScrollableHeaders()
            addScrollableHeader(header)
        }
    }

    override fun showEmpty(show: Boolean) {
        attendanceSummaryEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showProgress(show: Boolean) {
        attendanceSummaryProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        attendanceSummaryRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun hideRefresh() {
        attendanceSummarySwipe.isRefreshing = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SAVED_SUBJECT_KEY, presenter.currentSubjectId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
