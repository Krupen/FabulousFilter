package com.allattentionhere.fabulousfiltersample;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;


/**
 * Created by krupenghetiya on 23/06/17.
 */

public class MyFabFragment extends AAH_FabulousFragment {

    int x, y, fabsize;

    public static MyFabFragment newInstance(int x, int y, int fabsize) {
        MyFabFragment mff = new MyFabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("x", x);
        bundle.putInt("y", y);
        bundle.putInt("fabsize", fabsize);
        mff.setArguments(bundle);
        return mff;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x = getArguments().getInt("x");
        y = getArguments().getInt("y");
        fabsize = getArguments().getInt("fabsize");
        Log.d("k9xy", "x: " + x + " | y" + y);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        Log.d("k9lib", "setupDialog mine : ");
        View contentView = View.inflate(getContext(), R.layout.filter_view, null);
        RelativeLayout rl_main = (RelativeLayout) contentView.findViewById(R.id.rl_main);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        ImageButton imgbtn_refresh = (ImageButton) contentView.findViewById(R.id.imgbtn_refresh);
        FloatingActionButton fabulous_fab = (FloatingActionButton) contentView.findViewById(R.id.fabulous_fab);
        imgbtn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("k9click", "onClick imgbtn_refresh: ");
                closeFilter();
            }
        });

        setView_main(rl_main);
        setViewgroup_static(ll_buttons);
        setFabulous_fab(fabulous_fab);
        setPeek_height(400);
        setFab_icon_resource(android.R.drawable.ic_input_add);
        setFab_size(fabsize);
        setFab_pos_x(x);
        setFab_pos_y(y);
        setCallbacks((Callbacks) getActivity());

        dialog.setContentView(contentView);
        setMainContentView(contentView);

        super.setupDialog(dialog, style);

    }
}
