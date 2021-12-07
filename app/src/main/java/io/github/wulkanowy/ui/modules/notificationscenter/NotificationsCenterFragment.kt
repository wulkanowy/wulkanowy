package io.github.wulkanowy.ui.modules.notificationscenter

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Notification
import io.github.wulkanowy.databinding.FragmentNotificationsCenterBinding
import io.github.wulkanowy.services.sync.notifications.NotificationType
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.attendance.AttendanceFragment
import io.github.wulkanowy.ui.modules.conference.ConferenceFragment
import io.github.wulkanowy.ui.modules.exam.ExamFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.homework.HomeworkFragment
import io.github.wulkanowy.ui.modules.luckynumber.LuckyNumberFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.note.NoteFragment
import io.github.wulkanowy.ui.modules.schoolannouncement.SchoolAnnouncementFragment
import io.github.wulkanowy.ui.modules.timetable.TimetableFragment
import io.github.wulkanowy.ui.modules.toStartIntent
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsCenterFragment :
    BaseFragment<FragmentNotificationsCenterBinding>(R.layout.fragment_notifications_center),
    NotificationsCenterView, MainView.TitledView {

    @Inject
    lateinit var presenter: NotificationsCenterPresenter

    @Inject
    lateinit var notificationsCenterAdapter: NotificationsCenterAdapter

    companion object {

        fun newInstance() = NotificationsCenterFragment()
    }

    override val titleStringId: Int
        get() = R.string.notifications_center_title

    override val isViewEmpty: Boolean
        get() = notificationsCenterAdapter.itemCount == 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationsCenterBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        notificationsCenterAdapter.onItemClickListener = { destination ->
            val activity = requireActivity() as MainActivity
            activity.pushView(destination.fragment)
        }

        with(binding.notificationsCenterRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationsCenterAdapter
        }
    }

    override fun updateData(data: List<Notification>) {
        notificationsCenterAdapter.submitList(data)
    }

    override fun showEmpty(show: Boolean) {
        binding.notificationsCenterEmpty.isVisible = show
    }

    override fun showProgress(show: Boolean) {
        binding.notificationsCenterProgress.isVisible = show
    }

    override fun showContent(show: Boolean) {
        binding.notificationsCenterRecycler.isVisible = show
    }

    override fun showErrorView(show: Boolean) {
        binding.notificationCenterError.isVisible = show
    }

    override fun setErrorDetails(message: String) {
        binding.notificationCenterErrorMessage.text = message
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
