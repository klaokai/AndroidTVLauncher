package com.jacky.launcher.main;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.jacky.launcher.R;

public class MainActivity extends FragmentActivity {

    public static final String TAG = "DeviceTypeRuntimeCheck";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 判断是否运行在电视设备上面
        boolean isTelevision = getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK);
        if (isTelevision) {
            Log.d(TAG, "Running on a TV Device");
        } else {
            Log.d(TAG, "Running on a non-TV Device");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 如果保存的状态为空，则加载主浏览切片
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, new MainFragment()).commitNow();
        }
    }
}
