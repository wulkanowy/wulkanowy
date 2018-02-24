package io.github.wulkanowy.ui.main.timetable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.wulkanowy.R;
import io.github.wulkanowy.di.component.ActivityComponent;
import io.github.wulkanowy.ui.base.BaseFragment;

public class TimetableFragment extends BaseFragment implements TimetableContract.View, TabLayout.OnTabSelectedListener {

    @BindView(R.id.timetable_fragment_viewpager)
    ViewPager viewPager;

    @BindView(R.id.timetable_fragment_tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.timetable_fragment_progress_bar)
    View progressBar;

    @Inject
    TimetablePagerAdapter pagerAdapter;

    @Inject
    TimetableContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setButterKnife(ButterKnife.bind(this, view));
            presenter.onStart(this, isSelected());
        }
        return view;
    }

    @Override
    protected void setUpOnViewCreated(View fragmentView) {
        showProgressBar(false);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (presenter != null) {
            presenter.onFragmentVisible(isSelected());
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        presenter.onTabSelected(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        presenter.onTabUnselected(tab.getPosition());
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //do nothing
    }

    @Override
    public void setAdapterWithTabLayout() {
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void addPageToAdapter(TimetableTabFragment fragment, String title) {
        pagerAdapter.addFragment(fragment, title);
    }

    @Override
    public void scrollViewPagerToPosition(int position) {
        viewPager.setCurrentItem(position, false);
    }

    @Override
    public void setActivityTitle() {
        setTitle(getString(R.string.lessonplan_text));
    }

    @Override
    public void setChildFragmentSelected(int position, boolean selected) {
        ((BaseFragment) pagerAdapter.getItem(position)).setSelected(selected);
    }

    @Override
    public void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onError(String message) {
        if (getActivity() != null) {
            Snackbar.make(getActivity().findViewById(R.id.main_activity_view_pager),
                    message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }
}
