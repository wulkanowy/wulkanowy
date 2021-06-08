package io.github.wulkanowy.ui.modules.debug.notification

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentDebugNotificationsBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDebugFragment :
    BaseFragment<FragmentDebugNotificationsBinding>(R.layout.fragment_debug_notifications),
    NotificationDebugView, MainView.TitledView {

    @Inject
    lateinit var presenter: NotificationDebugPresenter

    override val titleStringId: Int
        get() = R.string.notification_debug_title

    companion object {
        fun newInstance() = NotificationDebugFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDebugNotificationsBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        binding.grade1.setOnClickListener { presenter.sendGradeNotifications(1) }
        binding.grade3.setOnClickListener { presenter.sendGradeNotifications(3) }
        binding.grade10.setOnClickListener { presenter.sendGradeNotifications(10) }

        binding.homework1.setOnClickListener { presenter.sendHomeworkNotifications(1) }
        binding.homework3.setOnClickListener { presenter.sendHomeworkNotifications(3) }
        binding.homework10.setOnClickListener { presenter.sendHomeworkNotifications(10) }
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
