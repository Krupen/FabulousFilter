package com.allattentionhere.fabulousfiltersample;

import android.content.res.Configuration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
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

public class MainActivity extends AppCompatActivity
    implements AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener {

  FloatingActionButton fab, fab2;
  RecyclerView recyclerView;
  MovieData movieData;
  MoviesAdapter moviesAdapter;
  Picasso picasso;
  LinearLayout linearLayout;
  List<SingleMovie> singleMovieList = new ArrayList<>();
  private final ArrayMap<String, List<String>> appliedFilters = new ArrayMap<>();
  MyFabFragment dialogFragment, dialogFragment1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    fab = findViewById(R.id.fab);
    fab2 = findViewById(R.id.fab2);
    recyclerView = findViewById(R.id.recyclerView);
    linearLayout = findViewById(R.id.ll);

    movieData = Util.getMovies();
    picasso = Picasso.with(this);
    singleMovieList.addAll(movieData.getAllMovies());
    moviesAdapter = new MoviesAdapter(singleMovieList, picasso, MainActivity.this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(moviesAdapter);

    if (getIntent().getIntExtra("fab", 1) == 2) {
      fab2.setVisibility(View.VISIBLE);
      fab.setVisibility(View.GONE);
      linearLayout.setVisibility(View.VISIBLE);
    } else {
      fab2.setVisibility(View.GONE);
      fab.setVisibility(View.VISIBLE);
      linearLayout.setVisibility(View.GONE);
    }

    dialogFragment1 = MyFabFragment.newInstance();
    dialogFragment1.setParentFab(fab);
    fab.setOnClickListener(
        v -> dialogFragment1.show(getSupportFragmentManager(), dialogFragment1.getTag()));

    dialogFragment = MyFabFragment.newInstance();
    dialogFragment.setParentFab(fab2);
    fab2.setOnClickListener(
        v -> dialogFragment.show(getSupportFragmentManager(), dialogFragment.getTag()));
  }

  @Override
  public void onResult(Object result) {
    if (result.toString().equalsIgnoreCase("swiped_down")) {
      // do something or nothing
    } else {
      if (result != null) {
        ArrayMap<String, List<String>> applied_filters = (ArrayMap<String, List<String>>) result;
        if (applied_filters.size() != 0) {
          List<SingleMovie> filteredList = movieData.getAllMovies();
          // iterate over arraymap
          for (Map.Entry<String, List<String>> entry : applied_filters.entrySet()) {
            switch (entry.getKey()) {
              case "genre":
                filteredList = movieData.getGenreFilteredMovies(entry.getValue(), filteredList);
                break;
              case "rating":
                filteredList = movieData.getRatingFilteredMovies(entry.getValue(), filteredList);
                break;
              case "year":
                filteredList = movieData.getYearFilteredMovies(entry.getValue(), filteredList);
                break;
              case "quality":
                filteredList = movieData.getQualityFilteredMovies(entry.getValue(), filteredList);
                break;
            }
          }
          singleMovieList.clear();
          singleMovieList.addAll(filteredList);
          moviesAdapter.notifyDataSetChanged();

        } else {
          singleMovieList.addAll(movieData.getAllMovies());
          moviesAdapter.notifyDataSetChanged();
        }
      }
      // handle result
    }
  }

  public ArrayMap<String, List<String>> getAppliedFilters() {
    return appliedFilters;
  }

  public MovieData getMovieData() {
    return movieData;
  }

  @Override
  public void onConfigurationChanged(@NonNull Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (dialogFragment.isAdded()) {
      dialogFragment.dismiss();
      dialogFragment.show(getSupportFragmentManager(), dialogFragment.getTag());
    }
    if (dialogFragment1.isAdded()) {
      dialogFragment1.dismiss();
      dialogFragment1.show(getSupportFragmentManager(), dialogFragment1.getTag());
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
