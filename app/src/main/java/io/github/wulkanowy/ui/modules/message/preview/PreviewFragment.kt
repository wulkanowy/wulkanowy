package io.github.wulkanowy.ui.modules.message.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.synthetic.main.fragment_message.*
import javax.inject.Inject

class PreviewFragment : BaseFragment(), PreviewView, MainView.TitledView {

    @Inject
    lateinit var presenter: PreviewPresenter

    override val titleStringId: Int
        get() = R.string.message_title

    companion object {
        const val MESSAGE_ID_KEY = "message_id"

        fun newInstance(messageId: Long): PreviewFragment {
            return PreviewFragment().apply {
                arguments = Bundle().apply { putLong(PreviewFragment.MESSAGE_ID_KEY, messageId) }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = message
        presenter.onAttachView(this, (savedInstanceState ?: arguments)?.getLong(MESSAGE_ID_KEY) ?: 0)
    }

    override fun setData(message: Message) {
        messageSubject.text = if (message.subject.isBlank()) "(brak tematu)" else message.subject
        messageAuthor.text = if (message.folderId == 2) "Do: ${message.recipient}" else "Od: ${message.sender}"
        messageDate.text = getString(R.string.all_date) + ": ${message.date?.toFormattedString("yyyy-MM-dd HH:mm:ss")}"
        messageContent.text = message.content
    }

    override fun showProgress(show: Boolean) {
        messageProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(MESSAGE_ID_KEY, presenter.messageId)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
