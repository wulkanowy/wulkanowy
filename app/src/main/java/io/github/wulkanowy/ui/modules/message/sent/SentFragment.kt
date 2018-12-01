package io.github.wulkanowy.ui.modules.message.sent

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
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.message.MessageItem
import io.github.wulkanowy.ui.modules.message.MessageView
import io.github.wulkanowy.ui.modules.message.preview.PreviewFragment
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_message_sent.*
import javax.inject.Inject

class SentFragment : BaseFragment(), SentView, MessageView.MessageChildView {

    @Inject
    lateinit var presenter: SentPresenter

    @Inject
    lateinit var sentAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = SentFragment()
    }

    override val isViewEmpty
        get() = sentAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_sent, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = messageSentRecycler
        presenter.onAttachView(this)
    }

    override fun initView() {
        sentAdapter.run {
            isAutoCollapseOnExpand = true
            isAutoScrollOnExpand = true
            setOnItemClickListener { presenter.onMessageItemSelected(it) }
        }

        messageSentRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = sentAdapter
        }
        messageSentSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
    }

    override fun updateData(data: List<MessageItem>) {
        sentAdapter.updateDataSet(data, true)
    }

    override fun updateItem(item: AbstractFlexibleItem<*>) {
        sentAdapter.updateItem(item)
    }

    override fun clearView() {
        sentAdapter.clear()
    }

    override fun showProgress(show: Boolean) {
        messageSentProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        messageSentRecycler.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showEmpty(show: Boolean) {
        messageSentEmpty.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showRefresh(show: Boolean) {
        messageSentSwipe.isRefreshing = show
    }

    override fun openMessage(messageId: Long) {
        (activity as? MainActivity)?.pushView(PreviewFragment.newInstance().apply {
            arguments = Bundle().apply { putLong(PreviewFragment.MESSAGE_ID_KEY, messageId) }
        })
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
