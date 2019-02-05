package io.github.wulkanowy.ui.modules.message.send

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.api.messages.ReportingUnit
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.main.MainView
import kotlinx.android.synthetic.main.fragment_send_message.*
import javax.inject.Inject

class SendMessageFragment() : BaseSessionFragment(), SendMessageView, MainView.TitledView {

    @Inject
    lateinit var presenter: SendMessagePresenter

    companion object {
        fun newInstance() = SendMessageFragment()
    }

    override val titleStringId: Int
        get() = R.string.send_message_title

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_message, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        showProgress(true)
        showContent(false)
    }

    override fun updateData(reportingUnits: List<ReportingUnit>) {
        sendMessageFromTextView.text = reportingUnits[0].senderId.toString() // TODO Use sender name here
    }

    override fun showProgress(show: Boolean) {
        sendMessageProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        sendMessageContent.visibility = if (show) View.VISIBLE else View.GONE
    }


}
