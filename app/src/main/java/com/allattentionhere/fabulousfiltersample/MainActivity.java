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
                MyFabFragment dialogFrag = MyFabFragment.newInstance();
                dialogFrag.setParent_fab(fab);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });
    }


    @Override
    public void onResult(Object result) {
        Log.d("aah", "onResult: " + result.toString());
    }
}
