package io.github.wulkanowy.ui.main.exams;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;

public class ExamsPresenter extends BasePresenter<ExamsContract.View>
        implements ExamsContract.Presenter {

    private OnFragmentIsReadyListener listener;

    @Inject
    ExamsPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onStart(ExamsContract.View view, OnFragmentIsReadyListener listener) {
        super.onStart(view);
        this.listener = listener;

        if (getView().isMenuVisible()) {
            getView().setActivityTitle();
        }

        this.listener.onFragmentIsReady();
    }

    @Override
    public void onFragmentVisible(boolean isVisible) {
        if (isVisible) {
            getView().setActivityTitle();
        }
    }
}
