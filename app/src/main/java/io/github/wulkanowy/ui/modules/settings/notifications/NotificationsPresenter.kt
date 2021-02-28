package io.github.wulkanowy.ui.modules.settings.notifications

import androidx.work.WorkInfo
import com.chuckerteam.chucker.api.ChuckerCollector
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.alarm.TimetableNotificationSchedulerHelper
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.isHolidays
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.LocalDate.now
import javax.inject.Inject

class NotificationsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val preferencesRepository: PreferencesRepository,
    private val timetableNotificationHelper: TimetableNotificationSchedulerHelper,
    private val analytics: AnalyticsHelper,
    private val syncManager: SyncManager,
    private val chuckerCollector: ChuckerCollector,
    private val appInfo: AppInfo
) : BasePresenter<NotificationsView>(errorHandler, studentRepository) {

    override fun onAttachView(view: NotificationsView) {
        super.onAttachView(view)
        Timber.i("Settings view was initialized")
        view.setServicesSuspended(preferencesRepository.serviceEnableKey, now().isHolidays)
        view.initView()
    }

    fun onSharedPreferenceChanged(key: String) {
        Timber.i("Change settings $key")

        preferencesRepository.apply {
            when (key) {
                serviceEnableKey -> with(syncManager) { if (isServiceEnabled) startPeriodicSyncWorker() else stopSyncWorker() }
                servicesIntervalKey, servicesOnlyWifiKey -> syncManager.startPeriodicSyncWorker(true)
                isDebugNotificationEnableKey -> chuckerCollector.showNotification =
                    isDebugNotificationEnable
                appThemeKey -> view?.recreateView()
                isUpcomingLessonsNotificationsEnableKey -> if (!isUpcomingLessonsNotificationsEnable) timetableNotificationHelper.cancelNotification()
                appLanguageKey -> view?.run {
                    recreateView()
                }
            }
        }
        analytics.logEvent("setting_changed", "name" to key)
    }

    fun onFixSyncIssuesClicked() {
        view?.showFixSyncDialog()
    }
}
