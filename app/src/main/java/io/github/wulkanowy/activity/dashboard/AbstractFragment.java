package io.github.wulkanowy.activity.dashboard;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.dao.entities.DaoSession;

public abstract class AbstractFragment<K extends AbstractExpandableHeaderItem> extends Fragment {

    private FlexibleAdapter<K> flexibleAdapter;

    private List<K> itemList = new ArrayList<>();

    private long userId;

    public AbstractFragment() {
        //empty constructor for fragments
    }

    public abstract int getLayout();

    public abstract int getRecyclerView();

    public abstract int getLoadingBar();

    public abstract List<K> getItems(DaoSession daoSession);

    public long getUserId() {
        return userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);

        if (getActivity() != null) {
            DaoSession daoSession = ((WulkanowyApp) getActivity().getApplication()).getDaoSession();
            userId = getActivity().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                    .getLong("userId", 0);

            if (itemList.isEmpty()) {
                itemList = getItems(daoSession);
                initiationFlexibleAdapter();
                setFlexibleAdapterOnRecyclerView(view);
                setLoadingBarInvisible(view);
            } else {
                setFlexibleAdapterOnRecyclerView(view);
                setLoadingBarInvisible(view);
            }
        }
        return view;
    }

    protected void setLoadingBarInvisible(View mainView) {
        mainView.findViewById(getLoadingBar()).setVisibility(View.INVISIBLE);
    }

    protected void initiationFlexibleAdapter() {
        flexibleAdapter = new FlexibleAdapter<>(itemList)
                .setDisplayHeadersAtStartUp(true)
                .setAutoCollapseOnExpand(false)
                .setAutoScrollOnExpand(true)
                .expandItemsAtStartUp();
    }

    protected void setFlexibleAdapterOnRecyclerView(View mainView) {
        RecyclerView recyclerView = mainView.findViewById(getRecyclerView());
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(mainView.getContext()));
        recyclerView.setAdapter(flexibleAdapter);
    }
}
