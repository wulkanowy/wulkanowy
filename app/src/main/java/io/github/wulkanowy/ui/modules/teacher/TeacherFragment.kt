package io.github.wulkanowy.ui.modules.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.note.NoteDialog
import io.github.wulkanowy.ui.modules.note.NoteFragment
import io.github.wulkanowy.ui.modules.note.NoteItem
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_note.*
import javax.inject.Inject

class TeacherFragment : BaseFragment(), TeacherView, MainView.TitledView {
    @Inject
    lateinit var presenter: TeacherPresenter

    @Inject
    lateinit var teacherAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = NoteFragment()
    }

    override val titleStringId: Int
        get() = R.string.note_title

    override val isViewEmpty: Boolean
        get() = teacherAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        noteRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = teacherAdapter
            addItemDecoration(FlexibleItemDecoration(context)
                .withDefaultDivider()
                .withDrawDividerOnLastItem(false)
            )
        }
        noteSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
    }

    override fun updateData(data: List<TeacherItem>) {
        teacherAdapter.updateDataSet(data, true)
    }

    override fun updateItem(item: AbstractFlexibleItem<*>) {
        teacherAdapter.updateItem(item)
    }

    override fun clearData() {
        teacherAdapter.clear()
    }

    override fun showEmpty(show: Boolean) {
        noteEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        noteProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun enableSwipe(enable: Boolean) {
        noteSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        noteRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun hideRefresh() {
        noteSwipe.isRefreshing = false
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}