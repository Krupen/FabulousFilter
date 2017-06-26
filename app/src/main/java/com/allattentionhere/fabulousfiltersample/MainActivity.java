package com.allattentionhere.fabulousfiltersample;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;


import static com.allattentionhere.fabulousfiltersample.R.id.fab;

public class MainActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks {

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                fab.getLocationInWindow(location);
                int x = location[0];
                int y = location[1];
                Log.d("k9xy", "fab width: " + fab.getWidth() + " | h=" + fab.getHeight());
                MyFabFragment dialogFrag = MyFabFragment.newInstance(x, y, fab.getHeight());
//        dialogFrag.setTargetFragment(this, FILTERDIALOG_FRAGMENT);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });
    }

    @Override
    public void setFabVisibility(int visibility) {
        fab.setVisibility(visibility);
    }

    @Override
    public void onResult(Object result) {
        Log.d("k9", "onResult: "+result.toString());
        //dismissed fabulous frag
    }
}
