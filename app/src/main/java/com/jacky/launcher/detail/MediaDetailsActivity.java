package com.jacky.launcher.detail;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jacky.launcher.R;

/**
 * @author jacky
 * @version v1.0
 * @since 16/8/28
 */
public class MediaDetailsActivity extends AppCompatActivity {

    public static final String MEDIA = "Media";
    public static final String SHARED_ELEMENT_NAME = "hero";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.details_fragment, new MediaDetailsFragment()).commitNow();
        }
    }
}