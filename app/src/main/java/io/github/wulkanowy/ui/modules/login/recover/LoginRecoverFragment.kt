package io.github.wulkanowy.ui.modules.login.recover

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.widget.doOnTextChanged
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.form.LoginSymbolAdapter
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.showSoftInput
import kotlinx.android.synthetic.main.fragment_login_form.*
import kotlinx.android.synthetic.main.fragment_login_recover.*
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

class LoginRecoverFragment : BaseFragment(), LoginRecoverView {

    @Inject
    lateinit var presenter: LoginRecoverPresenter

    @Inject
    lateinit var webAppInterface: WebAppInterface

    companion object {
        fun newInstance() = LoginRecoverFragment()
    }

    private lateinit var hostKeys: Array<String>

    private lateinit var hostValues: Array<String>

    override val recoverHostValue: String?
        get() = hostValues.getOrNull(hostKeys.indexOf(loginRecoverHost.text.toString()))

    override val recoverNameValue: String
        get() = loginRecoverName.text.toString().trim()

    override val recoverSymbolValue: String
        get() = loginRecoverSymbol.text.toString().trim()


    override fun setErrorNameRequired() {
        with(loginRecoverNameLayout) {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_recover, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        webAppInterface.onRecaptchaResponse = { presenter.sendRecoverRequest(it) }
        hostKeys = resources.getStringArray(R.array.hosts_keys)
        hostValues = resources.getStringArray(R.array.hosts_values)

        loginRecoverName.doOnTextChanged { _, _, _, _ -> presenter.onNameTextChanged() }
        loginRecoverSymbol.doOnTextChanged { _, _, _, _ -> presenter.onSymbolTextChanged() }
        loginRecoverHost.setOnItemClickListener { _, _, _, _ -> presenter.onHostSelected() }
        loginRecoverConfirm.setOnClickListener { presenter.onConfirmClick() }

        with(loginRecoverHost) {
            setText(hostKeys.getOrElse(0) { "" })
            setAdapter(LoginSymbolAdapter(context, R.layout.support_simple_spinner_dropdown_item, hostKeys))
        }
    }

    override fun setDefaultCredentials(name: String, symbol: String) {
        loginRecoverName.setText(name)
        loginRecoverSymbol.setText(symbol)
    }

    override fun clearNameError() {
        loginRecoverNameLayout.error = null
    }

    override fun clearSymbolError() {
        loginRecoverSymbolLayout.error = null
    }

    override fun showProgress(show: Boolean) {
        loginRecoverProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContentForm(show: Boolean) {
        loginRecoverFormContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContentCaptcha(show: Boolean) {
        loginRecoverCaptchaContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showSoftKeyboard() {
        activity?.showSoftInput()
    }

    override fun hideSoftKeyboard() {
        activity?.hideSoftInput()
    }

    override fun loadRecaptcha(siteKey: String, URL: String){
        val HTML = "<div id=\"recaptcha\"></div>\n<script src=\"https://www.google.com/recaptcha/api.js?onload=cl&render=explicit&hl=pl\" async defer></script>\n<script>var cl=()=>grecaptcha.render(\"recaptcha\",{sitekey:'$siteKey',callback:e =>Android.recaptchaCallback(e)})</script>"

        with(loginRecoverWebView) {
            settings.javaScriptEnabled = true
            addJavascriptInterface(webAppInterface, "Android")
            loadDataWithBaseURL(URL, HTML, "text/html", "UTF-8", null)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    Timber.i("Zaladowano")
                    showContentCaptcha(true)
                    showProgress(false)
                }
                //TODO Error przy niezaładowaniu strony
            }
        }

    }

    class WebAppInterface @Inject constructor(private val mContext: Context) {
        var onRecaptchaResponse: (String) -> Unit = {}

        @JavascriptInterface
        fun recaptchaCallback(recaptchaResponse: String) {
            Timber.d(recaptchaResponse)
            onRecaptchaResponse(recaptchaResponse)
        }
    }

}
