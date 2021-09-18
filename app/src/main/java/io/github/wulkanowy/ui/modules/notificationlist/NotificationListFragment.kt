package io.github.wulkanowy.ui.modules.notificationlist

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentNotificationListBinding
import io.github.wulkanowy.ui.base.BaseFragment
import javax.inject.Inject

@AndroidEntryPoint
class NotificationListFragment :
    BaseFragment<FragmentNotificationListBinding>(R.layout.fragment_notification_list),
    NotificationListView {

    @Inject
    lateinit var presenter: NotificationListPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationListBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}