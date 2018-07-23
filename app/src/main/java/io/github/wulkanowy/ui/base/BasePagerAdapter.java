package io.github.wulkanowy.ui.base;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static io.github.wulkanowy.utils.TimeUtilsKt.getAppDateFormatter;

public class BasePagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();

    private List<LocalDate> titleList = new ArrayList<>();

    public BasePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void addFragment(@NonNull Fragment fragment, @NonNull LocalDate title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    public void addFragment(@NonNull Fragment fragment) {
        fragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (!titleList.isEmpty()) {
            return titleList.get(position).format(getAppDateFormatter());
        }
        return null;
    }
}
