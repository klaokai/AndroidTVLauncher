package com.jacky.launcher.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jacky.launcher.R;
import com.jacky.launcher.app.AppCardPresenter;
import com.jacky.launcher.app.AppDataManage;
import com.jacky.launcher.app.AppModel;
import com.jacky.launcher.detail.ImgCardPresenter;
import com.jacky.launcher.detail.MediaDetailsActivity;
import com.jacky.launcher.detail.MediaModel;
import com.jacky.launcher.function.FunctionCardPresenter;
import com.jacky.launcher.function.FunctionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends BrowseSupportFragment {
    private static final String TAG = "MainFragment";

    private static final int SETTINGS_ACTION_ID = 1;

    private static final int BACKGROUND_UPDATE_DELAY = 300;

    /**
     * 每一行数据的适配器
     */
    private ArrayObjectAdapter rowsAdapter;
    private final Handler mHandler = new Handler(Looper.myLooper());
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    private BackgroundManager mBackgroundManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        loadRows();

        setupEventListeners();

        // prepareEntranceTransition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer);
            mBackgroundTimer.cancel();
        }
    }


    private void loadRows() {
        rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        addPhotoRow();
        addVideoRow();
        addAppRow();
        addFunctionRow();
        // 将行适配器绑定此Fragment
        setAdapter(rowsAdapter);
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private void addPhotoRow() {
        String headerName = getResources().getString(R.string.app_header_photo_name);
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new ImgCardPresenter());

        for (MediaModel mediaModel : MediaModel.getPhotoModels()) {
            listRowAdapter.add(mediaModel);
        }
        HeaderItem header = new HeaderItem(0, headerName);
        rowsAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void addVideoRow() {
        String headerName = getResources().getString(R.string.app_header_video_name);
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new ImgCardPresenter());
        for (MediaModel mediaModel : MediaModel.getVideoModels()) {
            listRowAdapter.add(mediaModel);
        }
        HeaderItem header = new HeaderItem(0, headerName);
        rowsAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void addAppRow() {
        String headerName = getResources().getString(R.string.app_header_app_name);
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new AppCardPresenter());
        FragmentActivity mContext = MainFragment.this.getActivity();
        ArrayList<AppModel> appDataList = new AppDataManage(mContext).getLaunchAppList();
        int cardCount = appDataList.size();

        for (int i = 0; i < cardCount; i++) {
            listRowAdapter.add(appDataList.get(i));
        }
        HeaderItem header = new HeaderItem(0, headerName);
        rowsAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void addFunctionRow() {
        String headerName = getResources().getString(R.string.app_header_function_name);
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new FunctionCardPresenter());
        FragmentActivity mContext = MainFragment.this.getActivity();
        List<FunctionModel> functionModels = FunctionModel.getFunctionList(mContext);
        int cardCount = functionModels.size();
        for (int i = 0; i < cardCount; i++) {
            listRowAdapter.add(functionModels.get(i));
        }
        HeaderItem header = new HeaderItem(0, headerName);
        rowsAdapter.add(new ListRow(header, listRowAdapter));
    }

    /**
     * 用于设置背景
     */
    private void prepareBackgroundManager() {
        // 获取当前背景管理器实例并将其绑定到当前活动的窗口
        FragmentActivity context = requireActivity();
        mBackgroundManager = BackgroundManager.getInstance(context);
        mBackgroundManager.attach(context.getWindow());

        // 从资源文件中加载默认背景图片
        mDefaultBackground = ContextCompat.getDrawable(context, R.drawable.default_background);
        // 获取显示器的尺寸指标信息，用于后续可能界面布局和适配
        mMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    /**
     * 设置UI组件信息
     */
    private void setupUIElements() {
        setTitle(getString(R.string.app_name));
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(ContextCompat.getColor(requireActivity(), R.color.fastlane_background));
        setSearchAffordanceColor(ContextCompat.getColor(requireActivity(), R.color.search_opaque));
        setHeaderPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object o) {
                return new IconHeaderItemPresenter();
            }
        });
    }


    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG).show();
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                // 按照指定链接加载图片
                .load(uri)
                // 居中
                .centerCrop()
                // 错了就加载默认背景图
                .error(mDefaultBackground)
                // 转为普通资源
                .into(new SimpleTarget<Drawable>(width, height) {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                        mBackgroundManager.setDrawable(drawable);
                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            FragmentActivity mContext = MainFragment.this.requireActivity();
            if (item instanceof MediaModel) {
                MediaModel mediaModel = (MediaModel) item;
                Intent intent = new Intent(mContext, MediaDetailsActivity.class);
                intent.putExtra(MediaDetailsActivity.MEDIA, mediaModel);

                assert ((ImageCardView) itemViewHolder.view).getMainImageView() != null;
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, ((ImageCardView) itemViewHolder.view).getMainImageView(), MediaDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                startActivity(intent, bundle);
            } else if (item instanceof AppModel) {
                AppModel appBean = (AppModel) item;
                Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(appBean.getPackageName());
                if (launchIntent != null) {
                    mContext.startActivity(launchIntent);
                }
            } else if (item instanceof FunctionModel) {
                FunctionModel functionModel = (FunctionModel) item;
                Intent intent = functionModel.getIntent();
                if (intent != null) {
                    startActivity(intent);
                }
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof MediaModel) {
                FragmentActivity mContext = MainFragment.this.requireActivity();
                MediaModel mediaModel = (MediaModel) item;
                int width = mMetrics.widthPixels;
                int height = mMetrics.heightPixels;

                Glide.with(mContext).asBitmap().load(mediaModel.getImageUrl()).centerCrop().into(new SimpleTarget<Bitmap>(width, height) {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation) {
                        mBackgroundManager.setBitmap(resource);
                    }
                });
            } else {
                mBackgroundManager.setBitmap(null);
            }
        }
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateBackground(mBackgroundUri);
                }
            });
        }
    }

}