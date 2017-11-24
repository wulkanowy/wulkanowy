package io.github.wulkanowy.activity.dashboard.timetable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.expandable.ExpandableExtension;

import io.github.wulkanowy.R;

public class TimetableFragment extends Fragment {

    private FastItemAdapter<IItem> fastItemAdapter;
    private ExpandableExtension<IItem> expandableExtension;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        fastItemAdapter = new FastItemAdapter<>();
        fastItemAdapter.withSelectable(true);
        expandableExtension = new ExpandableExtension<>();
        fastItemAdapter.addExtension(expandableExtension);

        RecyclerView recyclerView = view.findViewById(R.id.timetable_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fastItemAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState = fastItemAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
