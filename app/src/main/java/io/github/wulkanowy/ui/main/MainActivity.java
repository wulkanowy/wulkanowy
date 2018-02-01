package io.github.wulkanowy.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.wulkanowy.R;
import io.github.wulkanowy.ui.base.BaseActivity;
import io.github.wulkanowy.ui.main.attendance.AttendanceFragment;
import io.github.wulkanowy.ui.main.dashboard.DashboardFragment;
import io.github.wulkanowy.ui.main.grades.GradesFragment;

public class MainActivity extends BaseActivity implements MainContract.View {

    @BindView(R.id.main_activity_nav)
    AHBottomNavigation bottomNavigation;

    @BindView(R.id.main_activity_view_pager)
    AHBottomNavigationViewPager viewPager;

    @Inject
    MainPagerAdapter pagerAdapter;

    @Inject
    MainContract.Presenter presenter;

    AHBottomNavigation.OnTabSelectedListener listener = new AHBottomNavigation.OnTabSelectedListener() {
        @Override
        public boolean onTabSelected(int position, boolean wasSelected) {
            if (!wasSelected) {
                viewPager.setCurrentItem(position, false);
            }
            return true;
        }
    };

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);
        setButterKnife(ButterKnife.bind(this));

        presenter.onStart(this);
    }

    @Override
    protected void setUpOnCreate() {
        initiationBottomNav();
        initiationViewPager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    private void initiationBottomNav() {
        bottomNavigation.addItem(new AHBottomNavigationItem(
                getString(R.string.grades_text),
                getResources().getDrawable(R.drawable.icon_grade_26dp)
        ));
        bottomNavigation.addItem(new AHBottomNavigationItem(
                getString(R.string.attendance_text),
                getResources().getDrawable(R.drawable.icon_attendance_24dp)
        ));
        bottomNavigation.addItem(new AHBottomNavigationItem(
                getString(R.string.dashboard_text),
                getResources().getDrawable(R.drawable.ic_dashboard_black_24dp)
        ));
        bottomNavigation.addItem(new AHBottomNavigationItem(
                getString(R.string.lessonplan_text),
                getResources().getDrawable(R.drawable.icon_lessonplan_24dp)
        ));
        bottomNavigation.addItem(new AHBottomNavigationItem(
                getString(R.string.settings_text),
                getResources().getDrawable(R.drawable.icon_other_24dp)
        ));
        bottomNavigation.setCurrentItem(2);

        bottomNavigation.setAccentColor(getResources().getColor(R.color.colorPrimary));
        bottomNavigation.setInactiveColor(Color.BLACK);
        bottomNavigation.setBackgroundColor(getResources().getColor(R.color.colorBackgroundBottomNavi));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setOnTabSelectedListener(listener);
    }

    private void initiationViewPager() {
        pagerAdapter.addFragment(new GradesFragment());
        pagerAdapter.addFragment(new DashboardFragment());
        pagerAdapter.addFragment(new DashboardFragment());
        pagerAdapter.addFragment(new AttendanceFragment());
        pagerAdapter.addFragment(new DashboardFragment());

        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(2, false);
        viewPager.setOffscreenPageLimit(4);
    }
}
