package io.github.wulkanowy.activity.dashboard.lessonplan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.VulcanSynchronization;
import io.github.wulkanowy.services.jobs.VulcanJobHelper;

public class LessonPlanFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lessonplan, container, false);

        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession());
                            vulcanSynchronization.loginCurrentUser(getContext(), ((WulkanowyApp) getActivity().getApplication()).getDaoSession(), new Vulcan());
                            vulcanSynchronization.syncTimetable();
                        } catch (Exception e) {
                            Log.e(VulcanJobHelper.DEBUG_TAG, "Error", e);
                        }
                    }
                }).start();
            }
        });

        return view;
    }
}
