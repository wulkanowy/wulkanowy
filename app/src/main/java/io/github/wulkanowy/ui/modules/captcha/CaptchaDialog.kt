package io.github.wulkanowy.ui.modules.captcha

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.databinding.DialogCaptchaBinding
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.ui.base.BaseDialogFragment
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.net.HttpCookie
import java.net.URI
import javax.inject.Inject

@AndroidEntryPoint
class CaptchaDialog : BaseDialogFragment<DialogCaptchaBinding>() {

    @Inject
    lateinit var sdk: Sdk

    companion object {
        private const val CAPTCHA_URL = "captcha_url"
        fun newInstance(url: String?): CaptchaDialog {
            return CaptchaDialog().apply {
                arguments = bundleOf(CAPTCHA_URL to url)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DialogCaptchaBinding.inflate(inflater).apply { binding = this }.root

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.captchaWebview) {
            with(settings) {
                javaScriptEnabled = true
                // todo: make dynamic just like in sdk
                userAgentString = "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Mobile Safari/537.36"
            }

            CookieManager.getInstance().removeAllCookies(null)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(webView: WebView?, url: String) {
                    super.onPageFinished(webView, url)

                    val cookieManager = CookieManager.getInstance()
                    val cookies = cookieManager.getCookie(url)

                    val httpCookies = cookies.split(";").mapNotNull { cookie ->
                        Cookie.parse(url.toHttpUrl(), cookie.trim())?.let {
                            HttpCookie(it.name, it.value)
                        }
                    }
                    httpCookies.forEach {
                        sdk.cookieManager.cookieStore.add(URI.create(url), it)
                        sdk.alternativeCookieManager.cookieStore.add(URI.create(url), it)
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                }
            }

            loadUrl(arguments?.getString(CAPTCHA_URL).orEmpty())
        }
    }
}
