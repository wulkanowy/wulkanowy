package io.github.wulkanowy.ui.modules.about

import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import timber.log.Timber
import javax.inject.Inject

class AboutPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<AboutView>(errorHandler) {

    override fun onAttachView(view: AboutView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("About view was initialized")
        loadData()
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
