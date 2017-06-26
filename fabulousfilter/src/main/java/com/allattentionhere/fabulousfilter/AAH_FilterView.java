package com.allattentionhere.fabulousfilter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by krupenghetiya on 26/06/17.
 */

public class AAH_FilterView extends FrameLayout {
    FloatingActionButton fab;
    LinearLayout ll;

    public AAH_FilterView(@NonNull Context context) {
        super(context);
        init();
    }

    public AAH_FilterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public AAH_FilterView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AAH_FilterView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    public FloatingActionButton getFab() {
        return fab;
    }

    public LinearLayout getLl() {
        return ll;
    }

    public void init() {
        fab = new FloatingActionButton(getContext());
        fab.setTag("aah_fab");
        ll= new LinearLayout(getContext());
        ll.setTag("aah_ll");
        ll.setVisibility(GONE);
        this.addView(fab);
        this.addView(ll);

    }
}
