package io.github.wulkanowy.ui.modules.notificationscentre

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentNotificationsCentreBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsCentreFragment :
    BaseFragment<FragmentNotificationsCentreBinding>(R.layout.fragment_notifications_centre),
    NotificationsCentreView, MainView.TitledView {

    @Inject
    lateinit var presenter: NotificationsCentrePresenter

    @Inject
    lateinit var notificationsCentreAdapter: NotificationsCentreAdapter

    companion object {

        fun newInstance() = NotificationsCentreFragment()
    }

    override val titleStringId: Int
        get() = R.string.notifications_centre_title

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationsCentreBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(binding.notificationsCentreRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationsCentreAdapter
        }
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}