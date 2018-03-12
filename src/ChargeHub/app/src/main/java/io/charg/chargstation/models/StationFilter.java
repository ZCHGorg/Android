package io.charg.chargstation.models;

/**
 * Created by worker on 22.12.2017.
 */

public class StationFilter {

    private int mIndex;
    private String mTitle;
    private boolean mChecked;

    public StationFilter(int index, String title) {
        mIndex = index;
        mTitle = title;
        mChecked = true;
    }

    public int getIndex() {
        return mIndex;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
