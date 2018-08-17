package io.github.wulkanowy.ui.login.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import javax.inject.Inject

class LoginOptionsFragment : BaseFragment(), LoginOptionsContract.View {

    @Inject
    lateinit var presenter: LoginOptionsContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_options, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.attachView(this)
    }
}