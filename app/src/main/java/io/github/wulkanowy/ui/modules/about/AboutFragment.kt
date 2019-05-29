package io.github.wulkanowy.ui.modules.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.wulkanowy.BuildConfig
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.openInternetBrowser
import javax.inject.Inject

class AboutFragment : BaseFragment(), AboutView, MainView.TitledView {

    @Inject
    lateinit var presenter: AboutPresenter

    companion object {
        fun newInstance() = AboutFragment()
    }

    override val titleStringId: Int
        get() = R.string.about_title

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun openDiscordInviteView() {
        context?.openInternetBrowser("https://discord.gg/vccAQBr", ::showMessage)
    }

    override fun openHomepageWebView() {
        context?.openInternetBrowser("https://wulkanowy.github.io/", ::showMessage)
    }

    override fun openEmailClientView() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, Array(1) { "wulkanowyinc@gmail.com" })
            putExtra(Intent.EXTRA_SUBJECT, "Zgłoszenie błędu")
            putExtra(Intent.EXTRA_TEXT, "Tu umieść treść zgłoszenia\n\n" + "-".repeat(40) + "\n" + """
                Build: ${BuildConfig.VERSION_CODE}
                SDK: ${android.os.Build.VERSION.SDK_INT}
                Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}
            """.trimIndent())
        }

        context?.let {
            if (intent.resolveActivity(it.packageManager) != null) {
                startActivity(Intent.createChooser(intent, getString(R.string.about_feedback)))
            } else {
                it.openInternetBrowser("https://github.com/wulkanowy/wulkanowy/issues", ::showMessage)
            }
        }
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
