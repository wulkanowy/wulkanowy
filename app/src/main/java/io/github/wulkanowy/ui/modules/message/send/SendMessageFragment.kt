package io.github.wulkanowy.ui.modules.message.send

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.hootsuite.nachos.ChipConfiguration
import com.hootsuite.nachos.chip.ChipSpan
import com.hootsuite.nachos.chip.ChipSpanChipCreator
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.main.MainView
import kotlinx.android.synthetic.main.fragment_send_message.*
import javax.inject.Inject

class SendMessageFragment() : BaseSessionFragment(), SendMessageView, MainView.TitledView {

    @Inject
    lateinit var presenter: SendMessagePresenter

    private lateinit var nachosAdapter: ArrayAdapter<Recipient>

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

        context?.let {
            sendMessageRecipientInput.chipTokenizer = SpanChipTokenizer<ChipSpan>(it, object : ChipSpanChipCreator() {
                override fun createChip(context: Context, text: CharSequence, data: Any?): ChipSpan {
                    return ChipSpan(context, text, ContextCompat.getDrawable(context, R.drawable.ic_all_account_24dp), data)
                }

                override fun configureChip(chip: ChipSpan, chipConfiguration: ChipConfiguration) {
                    super.configureChip(chip, chipConfiguration)
                    chip.setShowIconOnLeft(true)
                }
            }, ChipSpan::class.java)
            nachosAdapter = ArrayAdapter(it, android.R.layout.simple_dropdown_item_1line)
        }

        sendMessageRecipientInput.setAdapter(nachosAdapter)
    }

    override fun setReportingUnit(unit: ReportingUnit) {
        sendMessageFromTextView.text = unit.senderName
    }

    override fun setRecipients(recipients: List<Recipient>) {
        nachosAdapter.run {
            clear()
            addAll(recipients)
            notifyDataSetChanged()
        }
    }

    override fun showProgress(show: Boolean) {
        sendMessageProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        sendMessageContent.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
