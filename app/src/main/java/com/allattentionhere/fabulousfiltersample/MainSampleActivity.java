package com.allattentionhere.fabulousfiltersample;

import android.content.res.Configuration;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.RelativeLayout;
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;

public class MainSampleActivity extends AppCompatActivity
    implements AAH_FabulousFragment.Callbacks {

  FloatingActionButton fab;
  MySampleFabFragment dialogFragment;
  RelativeLayout root;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_sample);
    fab = findViewById(R.id.fab);
    root = findViewById(R.id.root);
    dialogFragment = MySampleFabFragment.newInstance();
    dialogFragment.setParentFab(fab);
    fab.setOnClickListener(
        v -> dialogFragment.show(getSupportFragmentManager(), dialogFragment.getTag()));
  }

  @Override
  public void onResult(Object result) {
    if (result.toString().equalsIgnoreCase("swiped_down")) {
      // do something or nothing
    } else {
      // handle result
    }
  }

  @Override
  public void onConfigurationChanged(@NonNull Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (dialogFragment.isAdded()) {
      dialogFragment.dismiss();
      dialogFragment.show(getSupportFragmentManager(), dialogFragment.getTag());
    }
  }
}
