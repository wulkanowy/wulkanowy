package io.github.wulkanowy.ui.modules.message.send

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.main.MainView
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
}
