package io.github.wulkanowy.ui.modules.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import kotlinx.android.synthetic.main.fragment_note.*
import javax.inject.Inject

class NoteFragment : BaseFragment(), NoteView, MainView.TitledView {

    @Inject
    lateinit var presenter: NotePresenter

    @Inject
    lateinit var noteAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = NoteFragment()
    }

    override val titleStringId: Int
        get() = R.string.note_title

    override val isViewEmpty: Boolean
        get() = noteAdapter.isEmpty

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
            adapter = noteAdapter
        }
        noteSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
    }

    override fun updateData(data: List<NoteItem>) {
        noteAdapter.updateDataSet(data, true)
    }

    override fun clearData() {
        noteAdapter.clear()
    }

    override fun showEmpty(show: Boolean) {
        noteEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showProgress(show: Boolean) {
        noteProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        noteRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun hideRefresh() {
        noteSwipe.isRefreshing = false
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
