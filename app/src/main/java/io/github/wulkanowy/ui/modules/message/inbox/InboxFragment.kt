package io.github.wulkanowy.ui.modules.message.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.message.MessageItem
import io.github.wulkanowy.ui.modules.message.MessageView
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_message_inbox.*
import javax.inject.Inject

class InboxFragment : BaseFragment(), InboxView, MessageView.MessageChildView {

    @Inject
    lateinit var presenter: InboxPresenter

    @Inject
    lateinit var inboxAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = InboxFragment()
    }

    override val isViewEmpty
        get() = inboxAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_inbox, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = messageInboxRecycler
        presenter.onAttachView(this)
    }

    override fun initView() {
        inboxAdapter.run {
            isAutoCollapseOnExpand = true
            isAutoScrollOnExpand = true
            setOnItemClickListener { presenter.onMessageItemSelected(it) }
        }

        messageInboxRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = inboxAdapter
        }
        messageInboxSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
    }

    override fun updateData(data: List<MessageItem>) {
        inboxAdapter.updateDataSet(data, true)
    }

    override fun updateItem(item: AbstractFlexibleItem<*>) {
        inboxAdapter.updateItem(item)
    }

    override fun clearView() {
        inboxAdapter.clear()
    }

    override fun showProgress(show: Boolean) {
        messageInboxProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        messageInboxRecycler.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showEmpty(show: Boolean) {
        messageInboxEmpty.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showRefresh(show: Boolean) {
        messageInboxSwipe.isRefreshing = show
    }

    override fun showMessage(message: Message) {
//        MessageDialog.newInstance(message).show(fragmentManager, message.toString())
    }

    override fun notifyParentDataLoaded() {
        (parentFragment as? MessageFragment)?.onChildFragmentLoaded()
    }

    override fun onParentLoadData(forceRefresh: Boolean) {
        presenter.onParentViewLoadData(forceRefresh)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
