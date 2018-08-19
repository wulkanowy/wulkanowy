package io.github.wulkanowy.ui.login.form

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.showSoftInput
import kotlinx.android.synthetic.main.fragment_login_form.*
import javax.inject.Inject

class LoginFormFragment : BaseFragment(), LoginFormView {

    @Inject
    lateinit var presenter: LoginFormPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.attachView(this)

        loginSignButton.setOnClickListener {
            presenter.attemptLogin(loginEmailEdit.text.toString(), loginPassEdit.text.toString())
        }

        loginPassEdit.setOnEditorActionListener { _, id, _ ->
            when (id) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_NULL -> loginSignButton.callOnClick()
                else -> false
            }
        }
    }

    override fun setErrorEmailRequired() {
        loginEmailEdit.run {
            requestFocus()
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorEmailInvalid() {
        loginEmailEdit.run {
            requestFocus()
            error = getString(R.string.error_invalid_email)
        }
    }

    override fun setErrorPassRequired(focus: Boolean) {
        loginPassEdit.run {
            if (focus) requestFocus()
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorPassInvalid(focus: Boolean) {
        loginPassEdit.run {
            if (focus) requestFocus()
            error = getString(R.string.error_invalid_password)
        }
    }

    override fun setErrorPassIncorrect() {
        loginPassEdit.run {
            requestFocus()
            error = getString(R.string.error_incorrect_password)
        }
    }

    override fun resetViewErrors() {
        loginEmailEdit.error = null
        loginPassEdit.error = null
    }

    override fun showActionBar(show: Boolean) {
        activity?.actionBar?.run { if (show) show() else hide() }
    }

    override fun showSoftKeyboard() {
        activity?.showSoftInput()
    }

    override fun hideSoftKeyboard() {
        activity?.hideSoftInput()
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}