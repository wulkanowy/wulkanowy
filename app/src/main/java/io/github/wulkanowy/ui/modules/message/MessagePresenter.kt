package io.github.wulkanowy.ui.modules.message

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Completable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MessagePresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository
) : BasePresenter<MessageView>(errorHandler) {

    override fun onAttachView(view: MessageView) {
        super.onAttachView(view)
        disposable.add(Completable.timer(150, TimeUnit.MILLISECONDS, schedulers.mainThread)
            .subscribe {
                view.initView()
                loadData()
            })
    }

    fun onPageSelected(index: Int) {
        loadChild(index)
    }

    private fun loadData() {
        disposable.add(studentRepository.getCurrentStudent()
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({ view?.run { loadChild(currentPageIndex) } }) { errorHandler.proceed(it) })
    }

    private fun loadChild(index: Int, forceRefresh: Boolean = false) {
        view?.notifyChildLoadData(index, forceRefresh)
    }

    fun onChildViewLoaded() {
        view?.apply {
            showContent(true)
            showProgress(false)
        }
    }
}
