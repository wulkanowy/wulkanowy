package io.github.wulkanowy.ui.modules.account

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class AccountPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val studentRepository: StudentRepository,
    private val schedulers: SchedulersProvider
) : BasePresenter<AccountView>(errorHandler) {

    override fun onAttachView(view: AccountView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>) {
        if (item is AccountScrollableFooter) {
            view?.openLoginView()
        } else if (item is AccountItem) {
            if (item.student.isCurrent) {
                view?.dismissView()
            } else {
                disposable.add(studentRepository.switchStudent(item.student)
                    .subscribeOn(schedulers.backgroundThread)
                    .observeOn(schedulers.mainThread)
                    .subscribe({ view?.recreateView() }, { errorHandler.proceed(it) }))
            }
        }
    }

    private fun loadData() {
        disposable.add(studentRepository.getSavedStudents()
            .map { it.map { item -> AccountItem(item) } to AccountScrollableFooter() }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({ view?.updateData(it.first, it.second) }, { errorHandler.proceed(it) }))
    }
}

