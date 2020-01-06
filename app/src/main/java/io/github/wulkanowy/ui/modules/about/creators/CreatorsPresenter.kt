package io.github.wulkanowy.ui.modules.about.creators

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Single
import javax.inject.Inject

class CreatorsPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<CreatorsView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: CreatorsView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>) {
        if (item !is CreatorsItem) return
        view?.run { item.library.license?.licenseDescription?.let { openLicense(it) } }
    }

    private fun loadData() {
        disposable.add(Single.fromCallable { view?.appLibraries }
            .map {
                val exclude = listOf("Android-Iconics", "CircleImageView", "FastAdapter", "Jsoup", "okio", "Retrofit")
                it.filter { library -> !exclude.contains(library.libraryName) }
            }
            .map { it.map { library -> CreatorsItem(library) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnEvent { _, _ -> view?.showProgress(false) }
            .subscribe({ view?.run { updateData(it) } }, { errorHandler.dispatch(it) }))
    }
}
