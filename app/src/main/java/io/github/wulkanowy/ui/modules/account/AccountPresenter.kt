package io.github.wulkanowy.ui.modules.account

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class AccountPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val sessionRepository: SessionRepository,
    private val schedulers: SchedulersProvider
) : BasePresenter<AccountView>(errorHandler) {

    override fun onAttachView(view: AccountView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    fun onItemSelected() {
        view?.dismissView()
    }

    private fun loadData() {
        disposable.add(sessionRepository.getStudents()
            .map { it.map { item -> AccountItem(item) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({ view?.updateData(it) }, { errorHandler.proceed(it) }))
    }
}

