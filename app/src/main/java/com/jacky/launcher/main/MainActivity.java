package com.jacky.launcher.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.jacky.launcher.R;
import com.jacky.launcher.ui.OnboardingActivity;
import com.jacky.launcher.ui.OnboardingFragment;

public class MainActivity extends AppCompatActivity {

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
        // 第一次打开应用，则显示应用的介绍界面
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        /*SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(OnboardingFragment.COMPLETED_ONBOARDING, false);
        sharedPreferencesEditor.apply(); */
        if (!sharedPreferences.getBoolean(OnboardingFragment.COMPLETED_ONBOARDING, false)) {
            // This is the first time running the app, let's go to onboarding
            startActivity(new Intent(this, OnboardingActivity.class));
        }
        // 检查是否已经存在Fragment，避免重叠
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, new MainFragment()).commitNow();
        }
    }
}
