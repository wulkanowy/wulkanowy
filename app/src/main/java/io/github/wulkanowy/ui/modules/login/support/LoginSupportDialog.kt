package io.github.wulkanowy.ui.modules.login.support

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.databinding.DialogLoginSupportBinding
import io.github.wulkanowy.sdk.scrapper.login.AccountPermissionException
import io.github.wulkanowy.sdk.scrapper.login.InvalidSymbolException
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.openEmailClient
import io.github.wulkanowy.utils.serializable
import javax.inject.Inject

@AndroidEntryPoint
class LoginSupportDialog : BaseDialogFragment<DialogLoginSupportBinding>() {

    @Inject
    lateinit var appInfo: AppInfo

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    private lateinit var supportInfo: LoginSupportInfo

    companion object {
        private const val ARGUMENT_KEY = "item"

        fun newInstance(info: LoginSupportInfo) = LoginSupportDialog().apply {
            arguments = bundleOf(ARGUMENT_KEY to info)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportInfo = requireArguments().serializable(ARGUMENT_KEY)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext(), theme)
            .setView(
                DialogLoginSupportBinding.inflate(layoutInflater).apply { binding = this }.root
            )
            .create()
            .apply {
                setButton(
                    DialogInterface.BUTTON_POSITIVE,
                    getString(R.string.login_support_submit)
                ) { _, _ ->
                    onSubmitClick()
                }
            }

    private fun onSubmitClick() {
        with(binding) {
            context?.openEmailClient(
                chooserTitle = requireContext().getString(R.string.login_email_intent_title),
                email = "wulkanowyinc@gmail.com",
                subject = requireContext().getString(R.string.login_email_subject),
                body = requireContext().getString(
                    R.string.login_email_text,
                    "${appInfo.systemManufacturer} ${appInfo.systemModel}",
                    appInfo.systemVersion.toString(),
                    "${appInfo.versionName}-${appInfo.buildFlavor}",
                    supportInfo.loginData.baseUrl + "/" + supportInfo.loginData.symbol,
                    preferencesRepository.installationId,
                    getLastErrorFromStudentSelectScreen(),
                    dialogLoginSupportSchoolInput.text.takeIf { !it.isNullOrBlank() }
                        ?: return@with,
                    dialogLoginSupportAdditionalInput.text,
                )
            )
        }
    }

    private fun getLastErrorFromStudentSelectScreen(): String {
        if (!supportInfo.lastErrorMessage.isNullOrBlank()) {
            return supportInfo.lastErrorMessage!!
        }
        if (supportInfo.registerUser?.symbols.isNullOrEmpty()) {
            return ""
        }

        return "\n" + supportInfo.registerUser?.symbols?.filterNot {
            (it.error is AccountPermissionException || it.error is InvalidSymbolException) &&
                it.symbol != supportInfo.enteredSymbol
        }?.joinToString(";\n") { symbol ->
            buildString {
                append(" -")
                append(symbol.symbol)
                append("(${symbol.error?.message?.let { it.take(46) + "..." } ?: symbol.schools.size})")
                if (symbol.schools.isNotEmpty()) {
                    append(": ")
                }
                append(symbol.schools.joinToString(", ") { unit ->
                    buildString {
                        append(unit.schoolShortName)
                        append("(${unit.error?.message?.let { it.take(46) + "..." } ?: unit.students.size})")
                    }
                })
            }
        } + "\nPozostałe: " + supportInfo.registerUser?.symbols?.filter {
            it.error is AccountPermissionException || it.error is InvalidSymbolException
        }?.joinToString(", ") { it.symbol }
    }
}
