package io.github.wulkanowy.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.BasePagerAdapter
import io.github.wulkanowy.ui.login.form.LoginFormFragment
import io.github.wulkanowy.ui.login.options.LoginOptionsFragment
import io.github.wulkanowy.utils.setOnSelectPageListener
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject
import javax.inject.Named

class LoginActivity : BaseActivity(), LoginView, LoginSwitchListener {

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

        messageView = loginContainer
        loginAdapter.addFragments(LoginFormFragment(), LoginOptionsFragment())
        loginViewpager.run {
            adapter = loginAdapter
            setOnSelectPageListener { (loginAdapter.getItem(it) as LoginOptionsFragment).loadData() }
        }
    }

    override fun switchFragment(position: Int) {
        loginViewpager.setCurrentItem(position, false)
    }

    public override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
