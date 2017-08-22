package io.github.wulkanowy.activity.dashboard.grades;

import android.os.Parcel;
import android.os.Parcelable;

import io.github.wulkanowy.R;
import io.github.wulkanowy.api.grades.Grade;


public class GradeItem extends Grade implements Parcelable {

    protected GradeItem(Parcel source) {
        value = source.readString();
    }

    public GradeItem() {
        // empty constructor
    }

    public int getValueColor() {
        if (value.equals("6") || value.equals("6-") || value.equals("6+")) {
            return R.color.six_grade;
        } else if (value.equals("5") || value.equals("5-") || value.equals("5+")) {
            return R.color.five_grade;
        } else if (value.equals("4") || value.equals("4-") || value.equals("4+")) {
            return R.color.four_grade;
        } else if (value.equals("3") || value.equals("3-") || value.equals("3+")) {
            return R.color.three_grade;
        } else if (value.equals("2") || value.equals("2-") || value.equals("2+")) {
            return R.color.two_grade;
        } else if (value.equals("1") || value.equals("1-") || value.equals("1+")) {
            return R.color.one_grade;
        } else {
            return R.color.default_grade;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
    }

    public static final Creator<GradeItem> CREATOR = new Creator<GradeItem>() {
        @Override
        public GradeItem createFromParcel(Parcel source) {
            return new GradeItem(source);
        }

        @Override
        public GradeItem[] newArray(int size) {
            return new GradeItem[size];
        }
    };
}
