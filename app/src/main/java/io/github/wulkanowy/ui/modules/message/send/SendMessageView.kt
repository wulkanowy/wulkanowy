package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.api.messages.ReportingUnit
import io.github.wulkanowy.ui.base.session.BaseSessionView

interface SendMessageView : BaseSessionView {

    fun initView()

    fun updateData(reportingUnits: List<ReportingUnit>)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

}
