package com.allattentionhere.fabulousfiltersample;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;

public class FragmentExampleActivity extends AppCompatActivity
    implements AAH_FabulousFragment.Callbacks {

  FrameLayout frameLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fragment_example);
    frameLayout = findViewById(R.id.fl);

    ExampleFragment exampleFragment = ExampleFragment.newInstance();
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fl, exampleFragment, "tag");
    transaction.commitAllowingStateLoss();
  }

  @Override
  public void onResult(Object result) {
    if (result.toString().equalsIgnoreCase("swiped_down")) {
      // do something or nothing
    } else {
      // handle result
    }
  }
}
