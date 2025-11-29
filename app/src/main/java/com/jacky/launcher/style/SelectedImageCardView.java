package com.jacky.launcher.style;

import android.content.Context;
import android.view.View;

import androidx.leanback.widget.ImageCardView;

import com.jacky.launcher.R;

import org.jspecify.annotations.NonNull;

public class SelectedImageCardView extends ImageCardView {
    private final View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int selected_background = getResources().getColor(R.color.detail_background);
            int default_background = getResources().getColor(R.color.default_background);
            int color = hasFocus ? selected_background : default_background;
            setInfoAreaBackgroundColor(color);
        }
    };

    public SelectedImageCardView(@NonNull Context context) {
        super(context);
        setOnFocusChangeListener(onFocusChangeListener);
    }

}
