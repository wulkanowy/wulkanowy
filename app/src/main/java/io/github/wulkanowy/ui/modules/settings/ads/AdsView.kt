package io.github.wulkanowy.ui.modules.settings.ads

import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.utils.SupportAd

interface AdsView : BaseView {

    fun initView()

    fun showAd(ad: SupportAd)

    fun openPrivacyPolicy()

    fun showLoadingSupportAd(show: Boolean)

    fun showWatchAdOncePerVisit(show: Boolean)

    fun setCheckedAdsEnabled(checked: Boolean)
}
