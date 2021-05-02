package io.github.wulkanowy.ui.modules.directorinformation

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.DirectorInformation
import io.github.wulkanowy.databinding.FragmentDirectorInformationBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.utils.getThemeAttrColor
import javax.inject.Inject

@AndroidEntryPoint
class DirectorInformationFragment :
    BaseFragment<FragmentDirectorInformationBinding>(R.layout.fragment_director_information),
    DirectorInformationView, MainView.TitledView {

    @Inject
    lateinit var presenter: DirectorInformationPresenter

    @Inject
    lateinit var directorInformationAdapter: DirectorInformationAdapter

    companion object {
        fun newInstance() = DirectorInformationFragment()
    }

    override val titleStringId: Int
        get() = R.string.director_information_title

    override val isViewEmpty: Boolean
        get() = directorInformationAdapter.items.isEmpty()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDirectorInformationBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(binding.directorInformationRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = directorInformationAdapter
            addItemDecoration(DividerItemDecoration(context))
        }
        with(binding) {
            directorInformationSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            directorInformationSwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            directorInformationSwipe.setProgressBackgroundColorSchemeColor(
                requireContext().getThemeAttrColor(R.attr.colorSwipeRefresh)
            )
            directorInformationErrorRetry.setOnClickListener { presenter.onRetry() }
            directorInformationErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }
    }

    override fun updateData(data: List<DirectorInformation>) {
        with(directorInformationAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun clearData() {
        with(directorInformationAdapter) {
            items = listOf()
            notifyDataSetChanged()
        }
    }

    override fun showEmpty(show: Boolean) {
        binding.directorInformationEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showErrorView(show: Boolean) {
        binding.directorInformationError.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setErrorDetails(message: String) {
        binding.directorInformationErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.directorInformationProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.directorInformationSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.directorInformationRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showRefresh(show: Boolean) {
        binding.directorInformationSwipe.isRefreshing = show
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
