package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.ui.base.session.BaseSessionView

interface SendMessageView : BaseSessionView {

    fun initView()

    fun setReportingUnit(unit: ReportingUnit)

    fun setRecipients(recipients: List<Recipient>)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun popView()

    fun onSuccess()

    fun hideKeyboard()
}
