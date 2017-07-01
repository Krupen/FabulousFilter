package com.allattentionhere.fabulousfiltersample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;

import static com.allattentionhere.fabulousfiltersample.R.id.fab;


public class FragmentExampleActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks {

    FrameLayout fl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_example);
        fl = (FrameLayout) findViewById(R.id.fl);

        ExampleFragment f = ExampleFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl, f, "tag");
        transaction.commitAllowingStateLoss();
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
