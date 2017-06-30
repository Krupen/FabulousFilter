package com.allattentionhere.fabulousfiltersample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.allattentionhere.fabulousfiltersample.R.id.fab2;
import static com.allattentionhere.fabulousfiltersample.R.id.ll;
import static com.allattentionhere.fabulousfiltersample.R.id.recyclerView;


public class MainSampleActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks {

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sample);
        fab = (FloatingActionButton) findViewById(R.id.fab);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySampleFabFragment dialogFrag = MySampleFabFragment.newInstance();
                dialogFrag.setParentFab(fab);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });

    }


    @Override
    public void onResult(Object result) {
        Log.d("k9res", "onResult: " + result.toString());
        if (result.toString().equalsIgnoreCase("swiped_down")) {
            //do something or nothing
        } else {
            //handle result
        }
    }


}
