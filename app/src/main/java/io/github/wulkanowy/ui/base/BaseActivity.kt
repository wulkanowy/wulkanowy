package io.github.wulkanowy.ui.base

import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.view.View
import android.widget.Toast
import dagger.android.support.DaggerAppCompatActivity
import io.github.wulkanowy.R

abstract class BaseActivity : DaggerAppCompatActivity(), BaseContract.View {

    protected lateinit var messageView: View

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun showMessage(text: String) {
        //Snackbar.make(messageView, text, Snackbar.LENGTH_LONG).show()
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    }

    override fun showNoNetworkMessage() {
        showMessage(getString(R.string.noInternet_text))
    }

    override fun onDestroy() {
        super.onDestroy()
        invalidateOptionsMenu()
    }
}
