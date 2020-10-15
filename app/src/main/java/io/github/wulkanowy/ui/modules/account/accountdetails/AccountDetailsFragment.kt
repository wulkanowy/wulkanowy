package io.github.wulkanowy.ui.modules.account.accountdetails

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentAccountDetailsBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

@AndroidEntryPoint
class AccountDetailsFragment :
    BaseFragment<FragmentAccountDetailsBinding>(R.layout.fragment_account_details),
    AccountDetailsView, MainView.TitledView {

    @Inject
    lateinit var presenter: AccountDetailsPresenter

    override val titleStringId = R.string.account_title

    override var subtitleString = ""

    companion object {

        fun newInstance() = AccountDetailsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountDetailsBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}