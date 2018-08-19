package io.github.wulkanowy.ui.splash

import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.login.LoginActivity
import javax.inject.Inject

class SplashActivity : BaseActivity(), SplashView {

    @Inject
    lateinit var presenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun openLoginActivity() {
        startActivity(LoginActivity.getStartIntent(this))
        finish()
    }

    override fun openMainActivity() {
        // startActivity(MainActivity.getStartIntent(this));
        finish()
    }

    fun setCurrentThemeMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun cancelNotifications() {
        // new NotificationService(getApplicationContext()).cancelAll();
    }
}
