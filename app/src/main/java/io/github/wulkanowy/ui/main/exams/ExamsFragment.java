package io.github.wulkanowy.ui.main.exams;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.wulkanowy.R;
import io.github.wulkanowy.di.component.FragmentComponent;
import io.github.wulkanowy.ui.base.BaseFragment;
import io.github.wulkanowy.ui.base.BasePagerAdapter;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;

public class ExamsFragment extends BaseFragment implements ExamsContract.View {

    @BindView(R.id.exams_fragment_viewpager)
    ViewPager viewPager;

    @BindView(R.id.exams_fragment_tab_layout)
    TabLayout tabLayout;

    @Inject
    BasePagerAdapter pagerAdapter;

    @Inject
    ExamsContract.Presenter presenter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exams, container, false);

        FragmentComponent component = getFragmentComponent();
        if (component != null) {
            component.inject(this);
            setButterKnife(ButterKnife.bind(this, view));
            presenter.onStart(this, (OnFragmentIsReadyListener) getActivity());
        }
        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (presenter != null) {
            presenter.onFragmentVisible(menuVisible);
        }
    }

    @Override
    public void setActivityTitle() {
        setTitle(getString(R.string.exams_text));
    }
}
