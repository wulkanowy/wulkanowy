package io.github.wulkanowy.ui.modules.message.mailboxchooser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
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

        const val LISTENER_KEY = "mailbox_selected"
        const val ARGUMENT_KEY = "selected_mailbox"

        fun newInstance(mailboxes: List<Mailbox>, folder: String) = MailboxChooserDialog().apply {
            arguments = bundleOf(
                ARGUMENT_KEY to mailboxes.toTypedArray(),
                LISTENER_KEY to folder,
            )
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
            mailboxes = requireArguments().getParcelableArray(ARGUMENT_KEY).orEmpty()
                .toList() as List<Mailbox>,
        )
    }

    override fun initView() {
        binding.accountQuickDialogRecycler.adapter = mailboxAdapter.apply {
            onClickListener = presenter::onMailboxSelect
        }
    }

    override fun submitData(items: List<Mailbox>) {
        mailboxAdapter.submitList(items)
    }

    override fun onMailboxSelected(item: Mailbox) {
        setFragmentResult(
            requestKey = requireArguments().getString(LISTENER_KEY).orEmpty(),
            result = bundleOf(ARGUMENT_KEY to item),
        )
        dismiss()
    }
}
