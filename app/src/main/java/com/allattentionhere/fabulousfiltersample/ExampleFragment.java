package com.allattentionhere.fabulousfiltersample;

import android.content.res.Configuration;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;

public class ExampleFragment extends Fragment implements AAH_FabulousFragment.Callbacks {
  MySampleFabFragment dialogFragment;

  public static ExampleFragment newInstance() {
    return new ExampleFragment();
  }

  public ExampleFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_example, container, false);
    final FloatingActionButton fab = rootView.findViewById(R.id.fab);
    dialogFragment = MySampleFabFragment.newInstance();
    dialogFragment.setParentFab(fab);
    fab.setOnClickListener(
        v -> {
          dialogFragment.setCallbacks(ExampleFragment.this);
          if (getActivity() != null && !getActivity().isFinishing()) {
            dialogFragment.show(getActivity().getSupportFragmentManager(), dialogFragment.getTag());
          }
        });
    return rootView;
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
      if (getActivity() != null && !getActivity().isFinishing()) {
        dialogFragment.show(getActivity().getSupportFragmentManager(), dialogFragment.getTag());
      }
    }
  }
}
