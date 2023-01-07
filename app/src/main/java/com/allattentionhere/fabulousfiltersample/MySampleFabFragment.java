package com.allattentionhere.fabulousfiltersample;

import android.app.Dialog;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;

/** Created by krupenghetiya on 23/06/17. */
public class MySampleFabFragment extends AAH_FabulousFragment {

  public static MySampleFabFragment newInstance() {
    return new MySampleFabFragment();
  }

  @Override
  public void setupDialog(@NonNull Dialog dialog, int style) {
    View contentView = View.inflate(getContext(), R.layout.filter_sample_view, null);

    RelativeLayout contentContainer = contentView.findViewById(R.id.rl_content);
    LinearLayout buttonsContainer = contentView.findViewById(R.id.ll_buttons);
    contentView.findViewById(R.id.btn_close).setOnClickListener(v -> closeFilter("closed"));

    // params to set
    setAnimationDuration(600); // optional; default 500ms
    setInterpolator(new DecelerateInterpolator()); // optional;
    setPeekHeight(300); // optional; default 400dp
    setCallbacks((Callbacks) getActivity()); // optional; to get back result
    setViewgroupStatic(buttonsContainer); // optional; layout to stick at bottom on slide
    //        setViewPager(vp_types); //optional; if you use viewpager that has scrollview
    setViewMain(contentContainer); // necessary; main bottomsheet view
    setMainContentView(contentView); // necessary; call at end before super
    super.setupDialog(dialog, style); // call super at last
  }
}
