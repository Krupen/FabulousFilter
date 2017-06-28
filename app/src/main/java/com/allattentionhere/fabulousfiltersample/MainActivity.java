package com.allattentionhere.fabulousfiltersample;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class MainActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks {

    FloatingActionButton fab,fab2;
    RecyclerView recyclerView;
    MovieData mData;
    MoviesAdapter mAdapter;
    Picasso p;
    LinearLayout ll;
    List<SingleMovie> mList = new ArrayList<>();
    private ArrayMap<String, List<String>> applied_filters =new ArrayMap<>();


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

        if (getIntent().getIntExtra("fab",1)==2){
            fab2.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            ll.setVisibility(View.VISIBLE);
        }else {
            fab2.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            ll.setVisibility(View.GONE);
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFabFragment dialogFrag = MyFabFragment.newInstance();
                dialogFrag.setParentFab(fab);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFabFragment dialogFrag = MyFabFragment.newInstance();
                dialogFrag.setParentFab(fab2);
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
                        Log.d("k9res", "entry.key: "+entry.getKey());
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
                    Log.d("k9res", "new size: "+filteredList.size());
                    mList.clear();
                    mList.addAll(filteredList);
                    mAdapter.notifyDataSetChanged();

                }else {
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
}
