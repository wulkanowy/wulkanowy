package io.github.wulkanowy.ui.splash;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.wulkanowy.BuildConfig;
import io.github.wulkanowy.R;
import io.github.wulkanowy.ui.base.BaseActivity;
import io.github.wulkanowy.ui.base.RootPresenter;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.rawText)
    public TextView versionText;

    RootPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getActivityComponent().inject(this);
        setButterKnife(ButterKnife.bind(this));

        versionText.setText(getString(R.string.version_text, BuildConfig.VERSION_NAME));

        presenter.onStart(this);
    }
}
