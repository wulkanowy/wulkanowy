package io.github.wulkanowy.ui.modules.messages.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import com.stfalcon.chatkit.utils.DateFormatter
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.messages.message.MessagesActivity
import kotlinx.android.synthetic.main.fragment_messages.*
import javax.inject.Inject

class DialogsFragment : BaseFragment(), DialogsView, MainView.MainChildView, MainView.TitledView {

    @Inject
    lateinit var presenter: DialogsPresenter

    override val titleStringId: Int
        get() = R.string.message_title

    override val isViewEmpty: Boolean
        get() = dialogsAdapter.isEmpty

    private var dialogsAdapter: DialogsListAdapter<Dialog> = DialogsListAdapter(null)

    companion object {
        fun newInstance() = DialogsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        dialogsAdapter.setOnDialogClickListener { dialog ->
            context?.startActivity(Intent(context, MessagesActivity::class.java)
                .putExtra(MessagesActivity.CONVERSATION_ID_KEY, dialog!!.users[0].id.toInt())
                .putExtra(MessagesActivity.CONVERSATION_NAME_KEY, dialog.dialogName)
            )
        }

        dialogsList.setAdapter(dialogsAdapter)
        dialogsAdapter.setDatesFormatter { date ->
            when {
                DateFormatter.isToday(date) -> DateFormatter.format(date, DateFormatter.Template.TIME)
                DateFormatter.isYesterday(date) -> getString(R.string.message_yesterday)
                else -> DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR)
            }
        }

        dialogsSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
    }

    override fun updateData(dialogs: List<Dialog>) {
        dialogsAdapter.setItems(dialogs)
    }

    override fun onFragmentReselected() {
        presenter.onViewReselected()
    }

    override fun showEmpty(show: Boolean) {
        dialogsEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        dialogProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun hideRefresh() {
        dialogsSwipe.isRefreshing = false
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
