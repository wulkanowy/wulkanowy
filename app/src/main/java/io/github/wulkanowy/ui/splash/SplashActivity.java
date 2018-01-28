package io.github.wulkanowy.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.wulkanowy.BuildConfig;
import io.github.wulkanowy.R;
import io.github.wulkanowy.services.jobs.FullSyncJob;
import io.github.wulkanowy.ui.base.BaseActivity;
import io.github.wulkanowy.ui.login.LoginActivity;
import io.github.wulkanowy.ui.main.MainActivity;

public class SplashActivity extends BaseActivity implements SplashContract.View {

    @BindView(R.id.splash_activity_version_text)
    public TextView versionText;

    @Inject
    SplashContract.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getActivityComponent().inject(this);
        setButterKnife(ButterKnife.bind(this));

        versionText.setText(getString(R.string.version_text, BuildConfig.VERSION_NAME));

        presenter.onStart(this);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void openLoginActivity() {
        startActivity(LoginActivity.getStartIntent(this));
        finish();
    }

    @Override
    public void openDashboardActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void startFullSyncService() {
        new FullSyncJob().scheduledJob(getApplicationContext());
    }
}
