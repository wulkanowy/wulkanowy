package io.github.wulkanowy.ui.modules.luckynumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.databinding.FragmentLuckyNumberBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class LuckyNumberFragment : BaseFragment(), LuckyNumberView, MainView.TitledView {

    private lateinit var binding: FragmentLuckyNumberBinding

    @Inject
    lateinit var presenter: LuckyNumberPresenter

    companion object {
        fun newInstance() = LuckyNumberFragment()
    }

    override val titleStringId: Int
        get() = R.string.lucky_number_title

    override val isViewEmpty get() = binding.luckyNumberText.text.isBlank()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLuckyNumberBinding.inflate(inflater).apply { binding = this }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = binding.luckyNumberSwipe
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(binding) {
            luckyNumberSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
            luckyNumberErrorRetry.setOnClickListener { presenter.onRetry() }
            luckyNumberErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }
    }

    override fun updateData(data: LuckyNumber) {
        binding.luckyNumberText.text = data.luckyNumber.toString()
    }

    override fun hideRefresh() {
        binding.luckyNumberSwipe.isRefreshing = false
    }

    override fun showEmpty(show: Boolean) {
        binding.luckyNumberEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        binding.luckyNumberError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.luckyNumberErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.luckyNumberProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.luckyNumberSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.luckyNumberContent.visibility = if (show) VISIBLE else GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
