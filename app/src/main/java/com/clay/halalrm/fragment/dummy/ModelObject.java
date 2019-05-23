package com.clay.halalrm.fragment.dummy;

import com.clay.halalrm.R;

public enum ModelObject {

    RED(R.id.imageRumahMakan1, R.layout.view_red),
    BLUE(R.id.imageRumahMakan3, R.layout.view_blue),
    GREEN(R.id.imageRumahMakan2, R.layout.view_green);

    private int mImageViewId;
    private int mLayoutResId;

    ModelObject(int ImageViewId, int layoutResId) {
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
