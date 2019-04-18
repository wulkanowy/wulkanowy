package io.github.wulkanowy.ui.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import dagger.android.support.DaggerAppCompatActivity
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), BaseView {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    protected lateinit var messageContainer: View

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        when (preferencesRepository.appTheme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            "black" -> setTheme(R.style.WulkanowyTheme_Black)
        }
    }

    override fun showError(text: String, error: Throwable) {
        Snackbar.make(messageContainer, text, LENGTH_LONG).setAction(R.string.all_details) {
            ErrorDialog.newInstance(error).show(supportFragmentManager, error.toString())
        }.show()
    }

    override fun showMessage(text: String) {
        Snackbar.make(messageContainer, text, LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        invalidateOptionsMenu()
    }
}
