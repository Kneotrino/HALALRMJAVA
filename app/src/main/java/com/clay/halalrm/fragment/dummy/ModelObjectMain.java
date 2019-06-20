package com.clay.halalrm.fragment.dummy;

import com.clay.halalrm.R;

public enum ModelObjectMain {

    RED(R.id.View1, R.layout.view_1),
    BLUE(R.id.View2, R.layout.view_2),
    GREEN(R.id.View3, R.layout.view_3);

    private int mImageViewId;
    private int mLayoutResId;

    ModelObjectMain(int ImageViewId, int layoutResId) {
        mImageViewId = ImageViewId;
        mLayoutResId = layoutResId;
    }

    public int getImageViewId() {
        return mImageViewId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}
