package io.github.wulkanowy.ui.modules.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentAccountBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>(R.layout.fragment_account),
    AccountView, MainView.TitledView {

    @Inject
    lateinit var presenter: AccountPresenter

    @Inject
    lateinit var accountAdapter: AccountAdapter

    companion object {

        fun newInstance() = AccountFragment()
    }

    override val titleStringId = R.string.account_title

    override var subtitleString = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentAccountBinding.inflate(inflater).apply { binding = this }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.onAttachView(this)
    }

    override fun initView() {
        binding.accountRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = accountAdapter
        }

        with(binding) {
            accountAdd.setOnClickListener { presenter.onAddSelected() }
        }
    }

    override fun updateData(data: List<AccountItem<*>>) {
        with(accountAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun openLoginView() {
        activity?.let {
            startActivity(LoginActivity.getStartIntent(it))
        }
    }
}