package io.github.wulkanowy.ui.modules.message.preview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.send.SendMessageActivity
import kotlinx.android.synthetic.main.fragment_message_preview.*
import javax.inject.Inject

@SuppressLint("SetTextI18n")
class MessagePreviewFragment : BaseSessionFragment(), MessagePreviewView, MainView.TitledView {

    @Inject
    lateinit var presenter: MessagePreviewPresenter

    private var menuReplyButton: MenuItem? = null

    override val titleStringId: Int
        get() = R.string.message_title

    override val noSubjectString: String
        get() = getString(R.string.message_no_subject)

    companion object {
        const val MESSAGE_ID_KEY = "message_id"

        fun newInstance(messageId: Int?): MessagePreviewFragment {
            return MessagePreviewFragment().apply {
                arguments = Bundle().apply { putInt(MESSAGE_ID_KEY, messageId ?: 0) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_preview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = message
        presenter.onAttachView(this, (savedInstanceState ?: arguments)?.getInt(MESSAGE_ID_KEY) ?: 0)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.action_menu_message_preview, menu)
        menuReplyButton = menu?.findItem(R.id.messagePreviewMenuReply)
        presenter.onCreateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.messagePreviewMenuReply) presenter.onReply()
        else false
    }

    override fun setSubject(subject: String) {
        messageSubject.text = subject
    }

    override fun setRecipient(recipient: String) {
        messageAuthor.text = "${getString(R.string.message_to)} $recipient"
    }

    override fun setSender(sender: String) {
        messageAuthor.text = "${getString(R.string.message_from)} $sender"
    }

    override fun setDate(date: String) {
        messageDate.text = getString(R.string.message_date, date)
    }

    override fun setContent(content: String) {
        messageContent.text = content
    }

    override fun showProgress(show: Boolean) {
        messageProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showReplyButton(show: Boolean) {
        menuReplyButton?.isVisible = show
    }

    override fun showMessageError() {
        messageError.visibility = View.VISIBLE
    }

    override fun openMessageReply(message: Message?) {
        context?.let { it.startActivity(SendMessageActivity.getStartIntent(it, message)) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MESSAGE_ID_KEY, presenter.messageId)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
