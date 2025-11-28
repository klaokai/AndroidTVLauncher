package com.jacky.launcher.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewLogoPresenter;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.SparseArrayObjectAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

/**
 * @author jacky
 * @version v1.0
 * @since 16/8/28
 */
public class MediaDetailsFragment extends DetailsSupportFragment {

    private ArrayObjectAdapter mRowsAdapter;
    private MediaModel mMediaModel;
    private Context mContext;
    private static final int ACTION_WATCH_TRAILER = 1;

    private BackgroundManager mBackgroundManager;
    private DisplayMetrics mMetrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        mMediaModel = getActivity().getIntent().getParcelableExtra(MediaDetailsActivity.MEDIA);

        prepareBackgroundManager();
        buildDetails();
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void buildDetails() {
        ClassPresenterSelector selector = new ClassPresenterSelector();
        FullWidthDetailsOverviewRowPresenter rowPresenter = new FullWidthDetailsOverviewRowPresenter(new MediaDetailsDescriptionPresenter(), new DetailsOverviewLogoPresenter());

        selector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        ListRowPresenter presenter = new ListRowPresenter();
        selector.addClassPresenter(ListRow.class, presenter);
        mRowsAdapter = new ArrayObjectAdapter(selector);

        // Hook up transition element.
        FullWidthDetailsOverviewSharedElementHelper sharedElementHelper =
                new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(
                getActivity(), MediaDetailsActivity.SHARED_ELEMENT_NAME);
        rowPresenter.setListener(sharedElementHelper);
        rowPresenter.setParticipatingEntranceTransition(true);


        final DetailsOverviewRow detailsOverview = new DetailsOverviewRow(mMediaModel);
        RequestManager context = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context = Glide.with(getContext());
        } else {
            context = Glide.with(getActivity());
        }
        context.asBitmap().load(mMediaModel.getImageUrl()).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                // 处理加载失败的情况
                return false; // 返回false表示不处理，可能会触发默认的错误处理
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                // 资源准备完成，这里可以做一些操作，但注意，如果你在这里设置了图片，那么target中也会设置，可能会重复
                // 如果你返回true，那么就会阻止传递到Target中，所以如果你在这里设置了图片，并且不想让Glide再设置到Target中，可以返回true
                // 但是，我们这里使用了CustomViewTarget，所以通常我们在Target中设置图片，这里可以返回false
                return false;
            }
        }).into(new SimpleTarget<Bitmap>(mMetrics.widthPixels, mMetrics.heightPixels) {
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                detailsOverview.setImageBitmap(mContext, resource);
            }
        });

        updateBackground(mMediaModel.getImageUrl());

        SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();
        if (!mMediaModel.getVideoUrl().isEmpty()) {
            adapter.set(ACTION_WATCH_TRAILER, new Action(ACTION_WATCH_TRAILER, "播放"));
        }
        detailsOverview.setActionsAdapter(adapter);
        mRowsAdapter.add(detailsOverview);

        setAdapter(mRowsAdapter);
    }

    private void updateBackground(String uri) {
        Glide.with(this).asBitmap().load(uri).centerCrop().into(new SimpleTarget<Bitmap>(mMetrics.widthPixels, mMetrics.heightPixels) {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation) {
                mBackgroundManager.setBitmap(resource);
            }
        });
    }

    @Override
    public void onStop() {
        mBackgroundManager.release();
        super.onStop();
    }
}