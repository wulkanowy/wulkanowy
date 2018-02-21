package io.github.wulkanowy.ui.main.timetable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.github.wulkanowy.R;
import io.github.wulkanowy.di.component.ActivityComponent;
import io.github.wulkanowy.ui.base.BaseFragment;

public class TimetableTabFragment extends BaseFragment implements TimetableTabContract.View {

    private static final String ARGUMENT_KEY = "Date";

    @BindView(R.id.timetable_tab_fragment_test)
    TextView testText;

    @Inject
    TimetableTabContract.Presenter presenter;

    boolean visible = false;

    //  @Inject
    FlexibleAdapter adapter;

    public static TimetableTabFragment newInstance(String date) {
        TimetableTabFragment fragmentTab = new TimetableTabFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_KEY, date);
        fragmentTab.setArguments(bundle);

        return fragmentTab;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable_tab, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setButterKnife(ButterKnife.bind(this, view));
            presenter.onStart(this);

            if (getArguments() != null) {
                presenter.setArgumentDate(getArguments().getString(ARGUMENT_KEY));
            }
        }
        return view;
    }

    @Override
    protected void setUpOnViewCreated(View fragmentView) {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (presenter != null) {
            presenter.setFragmentVisible(isVisibleToUser);
        }
        //TODO: fuck this shit

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (presenter != null) {
            // presenter.setFragmentVisible(menuVisible);
        }
        //TODO: fuck this shit
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResumeFragment();
        //TODO: fuck this shit

    }

    @Override
    public void setTestText(String message) {
        testText.setText(message);
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }
}
