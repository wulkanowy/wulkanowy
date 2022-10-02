package io.github.wulkanowy.ui.modules.message.mailboxchooser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.databinding.DialogMailboxChooserBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import javax.inject.Inject

@AndroidEntryPoint
class MailboxChooserDialog : BaseDialogFragment<DialogMailboxChooserBinding>(), MailboxChooserView {

    @Inject
    lateinit var presenter: MailboxChooserPresenter

    @Inject
    lateinit var mailboxAdapter: MailboxChooserAdapter

    companion object {

        private const val ARGUMENT_KEY = "selected_mailbox"

        fun newInstance(mailboxes: List<Mailbox>) = MailboxChooserDialog().apply {
            arguments =
                Bundle().apply { putParcelableArray(ARGUMENT_KEY, mailboxes.toTypedArray()) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogMailboxChooserBinding.inflate(inflater).apply { binding = this }.root

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.onAttachView(
            view = this,
            mailboxes = (requireArguments().getParcelableArrayList<Mailbox>(ARGUMENT_KEY)).orEmpty(),
        )
    }

    override fun initView() {
        with(binding.accountQuickDialogRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = mailboxAdapter.apply {
                onClickListener = presenter::onMailboxSelect
            }
        }
    }

    override fun submitData(items: List<Mailbox>) {
        mailboxAdapter.submitList(items)
    }
}
