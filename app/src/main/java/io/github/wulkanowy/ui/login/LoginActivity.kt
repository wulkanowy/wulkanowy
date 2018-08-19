package io.github.wulkanowy.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.BasePagerAdapter
import io.github.wulkanowy.ui.login.form.LoginFormFragment
import io.github.wulkanowy.ui.login.options.LoginOptionsFragment
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject
import javax.inject.Named

class LoginActivity : BaseActivity(), LoginView {

    @Inject
    lateinit var presenter: LoginPresenter

    @Inject
    @field:Named("Login")
    lateinit var loginAdapter: BasePagerAdapter

    companion object {
        fun getStartIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter.attachView(this)

        loginAdapter.addFragments(LoginFormFragment(), LoginOptionsFragment())
        loginViewpager.adapter = loginAdapter
    }

    public override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
