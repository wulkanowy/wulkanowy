package io.github.wulkanowy.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.Unbinder;
import io.github.wulkanowy.WulkanowyApp;
import io.github.wulkanowy.di.component.ActivityComponent;
import io.github.wulkanowy.di.component.DaggerActivityComponent;
import io.github.wulkanowy.di.modules.ActivityModule;
import io.github.wulkanowy.utils.NetworkUtils;

public abstract class BaseActivity extends AppCompatActivity implements RootActivity {

    private ActivityComponent activityComponent;

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(((WulkanowyApp) getApplication()).getApplicationComponent())
                .build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void showLoadingBar() {
    }

    @Override
    public void hideLoadingBar() {
    }

    @Override
    public void onError(int resId) {
    }

    @Override
    public void onError(String message) {
    }

    @Override
    public void isNetworkConnected() {
        NetworkUtils.isOnline(getApplicationContext());
    }

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    public void setButterKnife(Unbinder unbinder) {
        this.unbinder = unbinder;
    }
}
