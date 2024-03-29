package com.allattentionhere.fabulousfilter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

/** Created by krupenghetiya on 26/06/17. */
public class AAH_FilterView extends FrameLayout {
  @NonNull FrameLayout fabContainer;
  @NonNull FloatingActionButton fab;

  public AAH_FilterView(Context context) {
    super(context);
    init();
  }

  public AAH_FilterView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public AAH_FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public AAH_FilterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  public void init() {
    fabContainer = new FrameLayout(getContext());
    fabContainer.setTag("aah_fl");
    fab = new FloatingActionButton(getContext());
    fab.setTag("aah_fab");
    fab.setCompatElevation(0);
    fabContainer.addView(fab);
    this.addView(fabContainer);
  }
}
