package io.github.wulkanowy.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;

import butterknife.Unbinder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.utils.NetworkUtils;

public abstract class BaseActivity extends AppCompatActivity implements BaseContract.View {

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onError(int resId) {
        onError(getString(resId));
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoNetworkError() {
        onError(R.string.noInternet_text);
    }

    @Override
    public boolean isNetworkConnected() {
        return NetworkUtils.isOnline(getApplicationContext());
    }

    public void setButterKnife(Unbinder unbinder) {
        this.unbinder = unbinder;
    }
}
