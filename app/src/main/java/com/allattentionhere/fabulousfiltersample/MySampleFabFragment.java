package com.allattentionhere.fabulousfiltersample;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.allattentionhere.fabulousfiltersample.R.id.imgbtn_apply;
import static com.allattentionhere.fabulousfiltersample.R.id.imgbtn_refresh;
import static com.allattentionhere.fabulousfiltersample.R.id.tabs_types;
import static com.allattentionhere.fabulousfiltersample.R.id.vp_types;


/**
 * Created by krupenghetiya on 23/06/17.
 */

public class MySampleFabFragment extends AAH_FabulousFragment {

    Button btn_close;

    public static MySampleFabFragment newInstance() {
        MySampleFabFragment f = new MySampleFabFragment();
        return f;
    }

    @Override

    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.filter_sample_view, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        contentView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter("closed");
            }
        });

        //params to set
        setAnimationDuration(600); //optional; default 500ms
        setPeekHeight(300); // optional; default 400dp
        setCallbacks((Callbacks) getActivity()); //optional; to get back result
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
//        setViewPager(vp_types); //optional; if you use viewpager that has scrollview
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }

}
