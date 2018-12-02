package io.github.wulkanowy.ui.modules.message.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.message.MessageItem
import io.github.wulkanowy.ui.modules.message.MessageView
import io.github.wulkanowy.ui.modules.message.preview.PreviewFragment
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_message_tab.*
import javax.inject.Inject

class MessageTabFragment : BaseFragment(), MessageTabView, MessageView.MessageChildView {

    @Inject
    lateinit var presenter: MessageTabPresenter

    @Inject
    lateinit var tabAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        const val MESSAGE_TAB_FOLDER_ID = "message_tab_folder_id"
        fun newInstance(folderId: Int): MessageTabFragment {
            return MessageTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(MESSAGE_TAB_FOLDER_ID, folderId)
                }
            }
        }
    }

    override val isViewEmpty
        get() = tabAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_tab, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = messageTabRecycler
        presenter.onAttachView(this, (savedInstanceState ?: arguments)?.getInt(MessageTabFragment.MESSAGE_TAB_FOLDER_ID) ?: 0)
    }

    override fun initView() {
        tabAdapter.run {
            isAutoCollapseOnExpand = true
            isAutoScrollOnExpand = true
            setOnItemClickListener { presenter.onMessageItemSelected(it) }
        }

        messageTabRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = tabAdapter
        }
        messageTabSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
    }

    override fun updateData(data: List<MessageItem>) {
        tabAdapter.updateDataSet(data, true)
    }

    override fun updateItem(item: AbstractFlexibleItem<*>) {
        tabAdapter.updateItem(item)
    }

    override fun clearView() {
        tabAdapter.clear()
    }

    override fun showProgress(show: Boolean) {
        messageTabProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        messageTabRecycler.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showEmpty(show: Boolean) {
        messageTabEmpty.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showRefresh(show: Boolean) {
        messageTabSwipe.isRefreshing = show
    }

    override fun openMessage(messageId: Long) {
        (activity as? MainActivity)?.pushView(PreviewFragment.newInstance(messageId))
    }

    override fun notifyParentDataLoaded() {
        (parentFragment as? MessageFragment)?.onChildFragmentLoaded()
    }

    override fun onParentLoadData(forceRefresh: Boolean) {
        presenter.onParentViewLoadData(forceRefresh)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MessageTabFragment.MESSAGE_TAB_FOLDER_ID, presenter.folderId)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
