package io.github.wulkanowy.ui.main;


import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;

public class MainPresenter extends BasePresenter<MainContract.View>
        implements MainContract.Presenter {

    private int currentTabPosition = 0;

    @Inject
    MainPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onTabSelected(int position, boolean wasSelected, int defaultPosition) {
        if (!wasSelected) {
            getView().setChildFragmentSelected(position, true);
            getView().setCurrentPage(position);

            getView().setChildFragmentSelected(position, false);

            currentTabPosition = position;
        }

        if (wasSelected && position == defaultPosition && currentTabPosition == 0) {
            getView().setChildFragmentSelected(position, true);
            currentTabPosition = position;
        }
    }
}
