package io.github.wulkanowy.activity.dashboard.grades;

import android.os.Parcel;
import android.os.Parcelable;

import io.github.wulkanowy.api.grades.Grade;


public class GradeChild extends Grade implements Parcelable {

    protected GradeChild(Parcel source) {
        value = source.readString();
    }

    public GradeChild() {
        // empty constructor
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
    }

    public static final Creator<GradeChild> CREATOR = new Creator<GradeChild>() {
        @Override
        public GradeChild createFromParcel(Parcel source) {
            return new GradeChild(source);
        }

        @Override
        public GradeChild[] newArray(int size) {
            return new GradeChild[size];
        }
    };
}
