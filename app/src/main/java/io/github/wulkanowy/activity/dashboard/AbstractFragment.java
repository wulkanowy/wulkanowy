package io.github.wulkanowy.activity.dashboard;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.utilities.ConnectionUtilities;

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

    public abstract int getRefreshLayout();

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

            setUpRefreshLayout(view, daoSession);

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

    protected void initiationFlexibleAdapter() {
        flexibleAdapter = new FlexibleAdapter<>(itemList)
                .setDisplayHeadersAtStartUp(true)
                .setAutoCollapseOnExpand(false)
                .setAutoScrollOnExpand(true)
                .expandItemsAtStartUp();
    }

    protected final SwipeRefreshLayout.OnRefreshListener getDefaultRefreshListener(final SwipeRefreshLayout refreshLayout){
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(ConnectionUtilities.isOnline(getContext())) {
                    Toast.makeText(getContext(), R.string.app_name, Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                    // TODO: 01.12.2017 Task for refresh
                }else {
                    Toast.makeText(getContext(), R.string.noInternet_text, Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                }
            }
        };
    }

    protected void setUpRefreshLayout(View mainView, DaoSession daoSession){
        SwipeRefreshLayout swipeRefreshLayout = mainView.findViewById(getRefreshLayout());
        swipeRefreshLayout.setColorSchemeResources(android.R.color.black,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(getDefaultRefreshListener(swipeRefreshLayout));
    }

    protected void setLoadingBarInvisible(View mainView) {
        mainView.findViewById(getLoadingBar()).setVisibility(View.INVISIBLE);
    }

    protected void setFlexibleAdapterOnRecyclerView(View mainView) {
        RecyclerView recyclerView = mainView.findViewById(getRecyclerView());
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(mainView.getContext()));
        recyclerView.setAdapter(flexibleAdapter);
    }
}
