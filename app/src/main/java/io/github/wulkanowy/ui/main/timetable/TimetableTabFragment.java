package io.github.wulkanowy.ui.main.timetable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import io.github.wulkanowy.R;
import io.github.wulkanowy.di.component.ActivityComponent;
import io.github.wulkanowy.ui.base.BaseFragment;

public class TimetableTabFragment extends BaseFragment implements TimetableTabContract.View {

    private static final String ARGUMENT_KEY = "Date";

    private boolean isPrimary = false;

    private boolean isSelected = false;

    @BindView(R.id.timetable_tab_fragment_recycler)
    RecyclerView recyclerView;

    @Inject
    TimetableTabContract.Presenter presenter;

    @Inject
    FlexibleAdapter<TimetableHeaderItem> adapter;

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

            if (getArguments() != null) {
                presenter.setArgumentDate(getArguments().getString(ARGUMENT_KEY));
            }

            presenter.onStart(this, isPrimary);
        }
        return view;
    }

    @Override
    protected void setUpOnViewCreated(View fragmentView) {
        adapter.setAutoCollapseOnExpand(true);
        adapter.setAutoScrollOnExpand(true);
        adapter.expandItemsAtStartUp();

        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(fragmentView.getContext()));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void updateAdapterList(List<TimetableHeaderItem> headerItems) {
        adapter.updateDataSet(headerItems);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (presenter != null && getView() != null) {
            presenter.onFragmentSelected(isSelected);
        } else if (isSelected) {
            isPrimary = true;
        }
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public void onDestroyView() {
        isPrimary = false;
        presenter.onDestroy();
        super.onDestroyView();
    }
}
