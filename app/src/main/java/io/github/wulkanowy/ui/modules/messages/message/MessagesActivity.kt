package io.github.wulkanowy.ui.modules.messages.message

import android.os.Bundle
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.messages.MessagesListAdapter
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.modules.main.MainView
import kotlinx.android.synthetic.main.activity_messages.*
import javax.inject.Inject

class MessagesActivity : BaseActivity(), MessagesView, MainView.TitledView {

    override val titleStringId: Int
        get() = R.string.message_title

    @Inject
    lateinit var presenter: MessagesPresenter

    private val messagesAdapter: MessagesListAdapter<IMessage> = MessagesListAdapter("0", null)

    companion object {
        const val CONVERSATION_ID_KEY = "CONVERSATION_ID_KEY"
        const val CONVERSATION_NAME_KEY = "CONVERSATION_NAME_KEY"
    }

    private var messagesTotal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        messageContainer = messagesContainer

        presenter.onAttachView(this,
            intent.getIntExtra(CONVERSATION_ID_KEY, 0),
            intent.getStringExtra(CONVERSATION_NAME_KEY)
        )
    }

    override fun initAdapter() {
        messagesAdapter.setLoadMoreListener { page, _ ->
            if (page <= messagesTotal) {
                presenter.loadMore(page)
            }
        }
        messagesList.setAdapter(messagesAdapter)
    }

    override fun setActivityTitle(senderName: String) {
        title = senderName
    }

    override fun setTotalMessages(total: Int) {
        messagesTotal = total
    }

    override fun addToEnd(message: List<IMessage>) {
        messagesAdapter.addToEnd(message, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetachView()
    }
}
