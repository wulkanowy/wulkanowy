package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import javax.inject.Inject

class SendMessagePresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler
) : BaseSessionPresenter<SendMessageView>(errorHandler) {

}
