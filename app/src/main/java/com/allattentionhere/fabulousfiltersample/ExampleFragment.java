package com.allattentionhere.fabulousfiltersample;

import android.content.res.Configuration;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;


public class ExampleFragment extends Fragment implements AAH_FabulousFragment.Callbacks {
    MySampleFabFragment dialogFrag;

    public static ExampleFragment newInstance() {
        ExampleFragment fragment = new ExampleFragment();
        return fragment;
    }

    public ExampleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_example, container, false);
        // Log.d("k9crash", "result not null: ");
        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        dialogFrag = MySampleFabFragment.newInstance();
        dialogFrag.setParentFab(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogFrag.setCallbacks(ExampleFragment.this);
                dialogFrag.show(getActivity().getSupportFragmentManager(), dialogFrag.getTag());
            }
        });
        return rootView;
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (dialogFrag.isAdded()) {
            dialogFrag.dismiss();
            dialogFrag.show(getActivity().getSupportFragmentManager(), dialogFrag.getTag());
        }

    }
}
