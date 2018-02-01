package io.github.wulkanowy.ui.main.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.github.wulkanowy.R;
import io.github.wulkanowy.di.component.ActivityComponent;
import io.github.wulkanowy.ui.base.BaseFragment;

public class DashboardFragment extends BaseFragment implements DashboardContract.View {

    @Inject
    DashboardContract.Presenter presenter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setButterKnife(ButterKnife.bind(this, view));
            presenter.onStart(this);
        }

        return view;
    }

    @Override
    protected void setUpOnViewCreated(View fragmentView) {

    }
}
