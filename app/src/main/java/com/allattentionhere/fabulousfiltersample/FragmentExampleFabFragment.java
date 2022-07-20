package com.allattentionhere.fabulousfiltersample;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;


/**
 * Created by krupenghetiya on 23/06/17.
 */

public class FragmentExampleFabFragment extends AAH_FabulousFragment {

    Button btn_close;

    public static FragmentExampleFabFragment newInstance() {
        FragmentExampleFabFragment f = new FragmentExampleFabFragment();
        return f;
    }

    @Override

    public void setupDialog(@NonNull Dialog dialog, int style) {
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
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }

}
