package io.github.wulkanowy.ui.modules.message.send

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hootsuite.nachos.ChipConfiguration
import com.hootsuite.nachos.chip.ChipSpan
import com.hootsuite.nachos.chip.ChipSpanChipCreator
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.setOnTextChangedListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_send_message.*
import javax.inject.Inject

class SendMessageFragment : BaseSessionFragment(), SendMessageView, MainView.TitledView {

    @Inject
    lateinit var presenter: SendMessagePresenter

    private var recipients: List<Recipient> = emptyList()

    private lateinit var recipientsAdapter: ArrayAdapter<Recipient>

    companion object {
        fun newInstance() = SendMessageFragment()
    }

    override val titleStringId: Int
        get() = R.string.send_message_title

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_message, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
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
            recipientsAdapter = ArrayAdapter(it, android.R.layout.simple_dropdown_item_1line)
        }

        sendMessageRecipientInput.setAdapter(recipientsAdapter)
        sendMessageRecipientInput.setOnTextChangedListener { refreshRecipientsAdapter() }

        (activity as? MainActivity)?.mainBottomNav?.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.action_menu_send_message, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.sendMessageMenuSend) {
            when {
                sendMessageRecipientInput.allChips.isEmpty()
                -> Toast.makeText(context, getString(R.string.send_message_required_recipients), Toast.LENGTH_LONG).show()

                sendMessageContentInput.text?.length ?: 0 < 3
                -> Toast.makeText(context, getString(R.string.send_message_content_min_length), Toast.LENGTH_LONG).show()

                else -> {
                    presenter.onSend(
                        subject = sendMessageContentInput.text.toString(),
                        content = sendMessageSubjectInput.text.toString(),
                        recipients = sendMessageRecipientInput.allChips.map { it.data as Recipient }
                    )
                }
            }
            false
        } else false
    }

    override fun setReportingUnit(unit: ReportingUnit) {
        sendMessageFromTextView.setText(unit.senderName)
    }

    override fun setRecipients(recipients: List<Recipient>) {
        this.recipients = recipients
        refreshRecipientsAdapter()
    }

    private fun refreshRecipientsAdapter() {
        recipientsAdapter.run {
            clear()
            addAll(recipients - sendMessageRecipientInput.allChips.map { it.data as Recipient })
            notifyDataSetChanged()
        }
    }

    override fun showProgress(show: Boolean) {
        sendMessageProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        sendMessageContent.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showEmpty(show: Boolean) {
        sendMessageEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun popView() {
        (activity as? MainActivity)?.popView()
    }

    override fun onSuccess() {
        Toast.makeText(context, getString(R.string.send_message_successful), Toast.LENGTH_LONG).show()
        popView()
    }

    override fun hideSoftInput() {
        activity?.hideSoftInput()
    }

    override fun onDestroyView() {
        (activity as? MainActivity)?.mainBottomNav?.visibility = View.VISIBLE
        super.onDestroyView()
        presenter.onDetachView()
    }
}
