package io.github.wulkanowy.ui.modules.message.trash

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
import kotlinx.android.synthetic.main.fragment_message_trash.*
import javax.inject.Inject

class TrashFragment : BaseFragment(), TrashView, MessageView.MessageChildView {

    @Inject
    lateinit var presenter: TrashPresenter

    @Inject
    lateinit var trashAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = TrashFragment()
    }

    override val isViewEmpty
        get() = trashAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_trash, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = messageTrashRecycler
        presenter.onAttachView(this)
    }

    override fun initView() {
        trashAdapter.run {
            isAutoCollapseOnExpand = true
            isAutoScrollOnExpand = true
            setOnItemClickListener { presenter.onMessageItemSelected(it) }
        }

        messageTrashRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = trashAdapter
        }
        messageTrashSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
    }

    override fun updateData(data: List<MessageItem>) {
        trashAdapter.updateDataSet(data, true)
    }

    override fun updateItem(item: AbstractFlexibleItem<*>) {
        trashAdapter.updateItem(item)
    }

    override fun clearView() {
        trashAdapter.clear()
    }

    override fun showProgress(show: Boolean) {
        messageTrashProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        messageTrashRecycler.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showEmpty(show: Boolean) {
        messageTrashEmpty.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showRefresh(show: Boolean) {
        messageTrashSwipe.isRefreshing = show
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
