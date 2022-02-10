package io.github.wulkanowy.ui.modules.about.contributor

import io.github.wulkanowy.data.pojos.Contributor
import io.github.wulkanowy.data.repositories.AppCreatorRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.onResourceError
import io.github.wulkanowy.utils.onResourceLoading
import io.github.wulkanowy.utils.onResourceSuccess
import io.github.wulkanowy.utils.resourceFlow
import javax.inject.Inject

class ContributorPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val appCreatorRepository: AppCreatorRepository
) : BasePresenter<ContributorView>(errorHandler, studentRepository) {

    override fun onAttachView(view: ContributorView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    fun onItemSelected(contributor: Contributor) {
        view?.openUserGithubPage(contributor.githubUsername)
    }

    fun onSeeMoreClick() {
        view?.openGithubContributorsPage()
    }

    private fun loadData() {
        resourceFlow { appCreatorRepository.getAppCreators() }
            .onResourceSuccess {
                view?.run {
                    showProgress(false)
                    updateData(it)
                }
            }
            .onResourceError { errorHandler.dispatch(it) }
            .onResourceLoading { view?.showProgress(true) }
            .launch()
    }
}
