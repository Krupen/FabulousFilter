package com.allattentionhere.fabulousfiltersample;

import android.content.res.Configuration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.collection.ArrayMap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener {

    FloatingActionButton fab, fab2;
    RecyclerView recyclerView;
    MovieData mData;
    MoviesAdapter mAdapter;
    Picasso p;
    LinearLayout ll;
    List<SingleMovie> mList = new ArrayList<>();
    private ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();
    MyFabFragment dialogFrag, dialogFrag1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ll = (LinearLayout) findViewById(R.id.ll);

        mData = Util.getMovies();
        p = Picasso.with(this);
        mList.addAll(mData.getAllMovies());
        mAdapter = new MoviesAdapter(mList, p, MainActivity.this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if (getIntent().getIntExtra("fab", 1) == 2) {
            fab2.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            ll.setVisibility(View.VISIBLE);
        } else {
            fab2.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            ll.setVisibility(View.GONE);
        }

        dialogFrag1 = MyFabFragment.newInstance();
        dialogFrag1.setParentFab(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFrag1.show(getSupportFragmentManager(), dialogFrag1.getTag());
            }
        });

        dialogFrag = MyFabFragment.newInstance();
        dialogFrag.setParentFab(fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            if (result != null) {
                ArrayMap<String, List<String>> applied_filters = (ArrayMap<String, List<String>>) result;
                if (applied_filters.size() != 0) {
                    List<SingleMovie> filteredList = mData.getAllMovies();
                    //iterate over arraymap
                    for (Map.Entry<String, List<String>> entry : applied_filters.entrySet()) {
                        Log.d("k9res", "entry.key: " + entry.getKey());
                        switch (entry.getKey()) {
                            case "genre":
                                filteredList = mData.getGenreFilteredMovies(entry.getValue(), filteredList);
                                break;
                            case "rating":
                                filteredList = mData.getRatingFilteredMovies(entry.getValue(), filteredList);
                                break;
                            case "year":
                                filteredList = mData.getYearFilteredMovies(entry.getValue(), filteredList);
                                break;
                            case "quality":
                                filteredList = mData.getQualityFilteredMovies(entry.getValue(), filteredList);
                                break;
                        }
                    }
                    Log.d("k9res", "new size: " + filteredList.size());
                    mList.clear();
                    mList.addAll(filteredList);
                    mAdapter.notifyDataSetChanged();

                } else {
                    mList.addAll(mData.getAllMovies());
                    mAdapter.notifyDataSetChanged();
                }
            }
            //handle result
        }
    }

    public ArrayMap<String, List<String>> getApplied_filters() {
        return applied_filters;
    }

    public MovieData getmData() {
        return mData;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (dialogFrag.isAdded()) {
            dialogFrag.dismiss();
            dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
        }
        if (dialogFrag1.isAdded()) {
            dialogFrag1.dismiss();
            dialogFrag1.show(getSupportFragmentManager(), dialogFrag1.getTag());
        }

    }

    @Override
    public void onOpenAnimationStart() {
        Log.d("aah_animation", "onOpenAnimationStart: ");
    }

    @Override
    public void onOpenAnimationEnd() {
        Log.d("aah_animation", "onOpenAnimationEnd: ");

    }

    @Override
    public void onCloseAnimationStart() {
        Log.d("aah_animation", "onCloseAnimationStart: ");

    }

    @Override
    public void onCloseAnimationEnd() {
        Log.d("aah_animation", "onCloseAnimationEnd: ");

    }
}
