package com.jacky.launcher.main;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.jacky.launcher.R;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 如果保存的状态为空，则加载主浏览切片
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, new MainFragment()).commitNow();
        }
    }
}
