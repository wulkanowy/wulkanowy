package io.github.wulkanowy.ui.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_NULL
import android.widget.EditText
import android.widget.Toast
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.utils.KeyboardUtils
import io.github.wulkanowy.utils.KeyboardUtils.showSoftInput
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginContract.View {

    @Inject
    lateinit var presenter: LoginContract.Presenter

    private lateinit var requestedView: EditText

    companion object {
        fun getStartIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter.attachView(this)
        setUpOnCreate()
    }

    private fun setUpOnCreate() {
        messageView = loginContainer

        loginSignButton.setOnClickListener {
            presenter.attemptLogin(loginEmailEdit.text.toString(), loginPassEdit.text.toString())
        }

        loginPassEdit.setOnEditorActionListener { _, id, _ ->
            when (id) {
                IME_ACTION_DONE, IME_NULL -> loginSignButton.callOnClick()
                else -> false
            }
        }
    }

    override fun setErrorEmailRequired() {
        requestedView = loginEmailEdit.apply {
            requestFocus()
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorEmailInvalid() {
        requestedView = loginEmailEdit.apply {
            requestFocus()
            error = getString(R.string.error_invalid_email)
        }
    }

    override fun setErrorPassRequired() {
        requestedView = loginPassEdit.apply {
            requestFocus()
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorPassInvalid() {
        requestedView = loginPassEdit.apply {
            requestFocus()
            error = getString(R.string.error_invalid_password)
        }
    }

    override fun setErrorPassIncorrect() {
        requestedView = loginPassEdit.apply {
            requestFocus()
            error = getString(R.string.error_incorrect_password)
        }
    }

    override fun resetViewErrors() {
        loginEmailEdit.error = null
        loginPassEdit.error = null
    }

    override fun showSoftKeyboard() = showSoftInput(requestedView, this)

    override fun hideSoftKeyboard() = KeyboardUtils.hideSoftInput(this)

    override fun setStepOneLoginProgress() {
        onLoginProgressUpdate("1", getString(R.string.step_login))
    }

    override fun setStepTwoLoginProgress() {
        onLoginProgressUpdate("2", getString(R.string.step_synchronization))
    }

    override fun openMainActivity() {
        //startActivity(MainActivity.getStartIntent(this));
        finish()
    }

    override fun showLoginProgress(show: Boolean) {
        val animTime = resources.getInteger(android.R.integer.config_shortAnimTime)

        loginFormContainer.run {
            visibility = if (show) View.GONE else View.VISIBLE
            animate().run {
                duration = animTime.toLong()
                alpha((if (show) 0f else 1f))
                setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        loginFormContainer.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })
            }
        }

        loginProgressContainer.run {
            visibility = if (show) View.VISIBLE else View.GONE
            animate().run {
                duration = animTime.toLong()
                alpha((if (show) 1f else 0f))
                setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        loginProgressContainer.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
            }
        }
    }

    override fun showActionBar(show: Boolean) {
        supportActionBar?.run { if (show) show() else hide() }
    }

    override fun onSyncFailed() {
        Toast.makeText(applicationContext, R.string.login_sync_error, Toast.LENGTH_LONG).show()
    }

    private fun onLoginProgressUpdate(step: String, message: String) {
        loginProgressText!!.text = String.format("%1\$s/2 - %2\$s...", step, message)
    }

    public override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
