package io.github.wulkanowy.ui.modules.about

import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.Libs.SpecialButton.SPECIAL1
import com.mikepenz.aboutlibraries.Libs.SpecialButton.SPECIAL2
import com.mikepenz.aboutlibraries.Libs.SpecialButton.SPECIAL3
import io.github.wulkanowy.data.repositories.student.StudentRepository
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class AboutPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<AboutView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: AboutView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("About view was initialized")
        loadData()
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>) {
        if (item !is AboutItem) return
        view?.apply {
            when (item.title) {
                feedbackRes?.first -> openEmailClient()
                discordRes?.first -> openDiscordInvite()
                homepageRes?.first -> openHomepage()
                licensesRes?.first -> openLicenses()
                privacyRes?.first -> openPrivacyPolicy()
            }
        }
    }

    private fun loadData() {
        view?.apply {
            updateData(AboutScrollableHeader(), listOfNotNull(
                versionRes?.let { (title, summary, image) -> AboutItem(title, summary, image) },
                feedbackRes?.let { (title, summary, image) -> AboutItem(title, summary, image) },
                discordRes?.let { (title, summary, image) -> AboutItem(title, summary, image) },
                homepageRes?.let { (title, summary, image) -> AboutItem(title, summary, image) },
                licensesRes?.let { (title, summary, image) -> AboutItem(title, summary, image) },
                privacyRes?.let { (title, summary, image) -> AboutItem(title, summary, image) }))
        }
    }
}
