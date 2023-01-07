package com.allattentionhere.fabulousfiltersample;

import android.app.Activity;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/** Created by krupenghetiya on 27/06/17. */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

  List<SingleMovie> singleMovieList;
  Picasso picasso;
  Activity _activity;

  public MoviesAdapter(List<SingleMovie> list_urls, Picasso p, Activity a) {
    this.singleMovieList = list_urls;
    this.picasso = p;
    this._activity = a;
  }

  @NonNull
  @Override
  public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.single_movie, parent, false);
    return new MovieViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
    LinearLayout.LayoutParams layoutParams =
        new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    if (position == 0) {
      layoutParams.setMargins(
          (int) _activity.getResources().getDimension(R.dimen.card_margin),
          (int) _activity.getResources().getDimension(R.dimen.card_margin),
          (int) _activity.getResources().getDimension(R.dimen.card_margin),
          (int) _activity.getResources().getDimension(R.dimen.card_margin));
    } else {
      layoutParams.setMargins(
          (int) _activity.getResources().getDimension(R.dimen.card_margin),
          0,
          (int) _activity.getResources().getDimension(R.dimen.card_margin),
          (int) _activity.getResources().getDimension(R.dimen.card_margin));
    }
    holder.cardView.setLayoutParams(layoutParams);

    picasso
        .load(singleMovieList.get(position).getMedium_cover_image())
        .placeholder(android.R.color.darker_gray)
        .config(Bitmap.Config.RGB_565)
        .into(holder.cover);
    holder.title.setText(singleMovieList.get(position).getTitle());
    holder.genre.setText("Genre: " + singleMovieList.get(position).getGenre());
    holder.rating.setText("Rating: " + singleMovieList.get(position).getRating());
    holder.year.setText("Year: " + singleMovieList.get(position).getYear());
    holder.quality.setText("Quality: " + singleMovieList.get(position).getQuality());
    holder.cardView.setOnClickListener(
        v ->
            Toast.makeText(
                    v.getContext(),
                    "Clicked: " + singleMovieList.get(position).getTitle(),
                    Toast.LENGTH_SHORT)
                .show());
  }

  @Override
  public int getItemCount() {
    return singleMovieList.size();
  }

  public static class MovieViewHolder extends RecyclerView.ViewHolder {
    private final ImageView cover;
    private final TextView title;
    private final TextView genre;
    private final TextView rating;
    private final TextView year;
    private final TextView quality;
    private final CardView cardView;

    public MovieViewHolder(View x) {
      super(x);
      cover = x.findViewById(R.id.iv_cover);
      title = x.findViewById(R.id.tv_title);
      genre = x.findViewById(R.id.tv_genre);
      rating = x.findViewById(R.id.tv_rating);
      year = x.findViewById(R.id.tv_year);
      quality = x.findViewById(R.id.tv_quality);
      cardView = x.findViewById(R.id.card_view);
    }
  }
}
