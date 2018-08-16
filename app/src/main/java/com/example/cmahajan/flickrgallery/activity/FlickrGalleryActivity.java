package com.example.cmahajan.flickrgallery.activity;

import android.support.v4.app.Fragment;

import com.example.cmahajan.flickrgallery.fragments.FlickrGalleryFragment;

public class FlickrGalleryActivity extends FlickrFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return FlickrGalleryFragment.newInstance();
    }

}
