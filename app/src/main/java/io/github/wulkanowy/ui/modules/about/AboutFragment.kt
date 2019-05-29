package io.github.wulkanowy.ui.modules.about

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.BuildConfig.VERSION_CODE
import io.github.wulkanowy.BuildConfig.VERSION_NAME
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatDrawable
import io.github.wulkanowy.utils.openInternetBrowser
import kotlinx.android.synthetic.main.fragment_about.*
import javax.inject.Inject

class AboutFragment : BaseFragment(), AboutView, MainView.TitledView {

    @Inject
    lateinit var presenter: AboutPresenter

    @Inject
    lateinit var aboutAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    override val versionRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_version), "$VERSION_NAME ($VERSION_CODE)", getCompatDrawable(R.drawable.ic_all_about))
        }

    override val feedbackRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_feedback), getString(R.string.about_feedback_summary), getCompatDrawable(R.drawable.ic_about_feedback))
        }

    override val discordRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_discord), getString(R.string.about_discord_summary), getCompatDrawable(R.drawable.ic_about_discord))
        }

    override val homepageRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_homepage), getString(R.string.about_homepage_summary), getCompatDrawable(R.drawable.ic_about_homepage))
        }

    override val licensesRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_licenses), getString(R.string.about_licenses_summary), getCompatDrawable(R.drawable.ic_about_licenses))
        }

    override val privacyRes: Triple<String, String, Drawable?>?
        get() = context?.run {
            Triple(getString(R.string.about_privacy), getString(R.string.about_privacy_summary), getCompatDrawable(R.drawable.ic_about_privacy))
        }

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

    override fun initView() {
        aboutRecycler.apply {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = aboutAdapter
        }
    }

    override fun updateData(header: AboutScrollableHeader, items: List<AboutItem>) {
        aboutAdapter.apply {
            removeAllScrollableHeaders()
            addScrollableHeader(header)
            updateDataSet(items)
        }
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
                Build: $VERSION_CODE
                SDK: ${android.os.Build.VERSION.SDK_INT}
                Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}
            """.trimIndent())
        }

        context?.let {
            if (intent.resolveActivity(it.packageManager) != null) {
                //startActivity(Intent.createChooser(intent, getString(R.string.about_feedback)))
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
