package io.github.wulkanowy.activity.dashboard.timetable;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.wulkanowy.dao.entities.Lesson;

public class TimetableDialogFragment extends DialogFragment {

    private Lesson lesson;

    public static final TimetableDialogFragment newInstance(Lesson lesson) {
        return new TimetableDialogFragment().setLesson(lesson);
    }

    public TimetableDialogFragment() {
        setRetainInstance(true);
    }

    private TimetableDialogFragment setLesson(Lesson lesson) {
        this.lesson = lesson;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
