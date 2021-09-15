package io.github.wulkanowy.ui.modules.conference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.databinding.DialogConferenceBinding
import io.github.wulkanowy.utils.lifecycleAwareVariable

class ConferenceDialog : DialogFragment() {

    private var binding: DialogConferenceBinding by lifecycleAwareVariable()

    private lateinit var conference: Conference

    companion object {

        private const val ARGUMENT_KEY = "item"

        fun newInstance(conference: Conference) = ConferenceDialog().apply {
            arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, conference) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.let {
            conference = it.getSerializable(ARGUMENT_KEY) as Conference
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogConferenceBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            conferenceDialogClose.setOnClickListener { dismiss() }
        }
    }
}