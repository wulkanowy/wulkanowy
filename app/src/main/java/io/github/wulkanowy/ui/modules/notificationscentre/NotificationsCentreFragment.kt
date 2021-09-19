package io.github.wulkanowy.ui.modules.notificationscentre

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Notification
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

    override fun updateData(data: List<Notification>) {
        notificationsCentreAdapter.submitList(data)
    }

    override fun showEmpty(show: Boolean) {
        binding.notificationsCentreEmpty.isVisible = show
    }

    override fun showProgress(show: Boolean) {
        binding.notificationsCentreProgress.isVisible = show
    }

    override fun showContent(show: Boolean) {
        binding.notificationsCentreRecycler.isVisible = show
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}