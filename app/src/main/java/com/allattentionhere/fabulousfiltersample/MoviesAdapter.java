package com.allattentionhere.fabulousfiltersample;

import android.app.Activity;
import android.graphics.Bitmap;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krupenghetiya on 27/06/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    List<SingleMovie> mList = new ArrayList<>();
    Picasso picasso;
    Activity _activity;

    public MoviesAdapter(List<SingleMovie> list_urls, Picasso p,Activity a) {
        this.mList = list_urls;
        this.picasso = p;
        this._activity = a;
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_movie, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (position == 0){
            layoutParams.setMargins((int) _activity.getResources().getDimension(R.dimen.card_margin),(int) _activity.getResources().getDimension(R.dimen.card_margin),(int) _activity.getResources().getDimension(R.dimen.card_margin),(int) _activity.getResources().getDimension(R.dimen.card_margin));
        }else {
            layoutParams.setMargins((int) _activity.getResources().getDimension(R.dimen.card_margin),0,(int) _activity.getResources().getDimension(R.dimen.card_margin),(int) _activity.getResources().getDimension(R.dimen.card_margin));
        }
        holder.card_view.setLayoutParams(layoutParams);

        picasso.load(mList.get(position).getMedium_cover_image()).placeholder(android.R.color.darker_gray).config(Bitmap.Config.RGB_565).into(holder.iv_cover);
        holder.tv_title.setText(mList.get(position).getTitle());
        holder.tv_genre.setText("Genre: " +mList.get(position).getGenre());
        holder.tv_rating.setText("Rating: " + mList.get(position).getRating());
        holder.tv_year.setText("Year: " + mList.get(position).getYear());
        holder.tv_quality.setText("Quality: " +mList.get(position).getQuality());
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("k9","clicked");
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_cover;
        private TextView tv_title;
        private TextView tv_genre;
        private TextView tv_rating;
        private TextView tv_year;
        private TextView tv_quality;
        private CardView card_view;

        public MovieViewHolder(View x) {
            super(x);
            iv_cover = (ImageView) x.findViewById(R.id.iv_cover);
            tv_title = (TextView) x.findViewById(R.id.tv_title);
            tv_genre = (TextView) x.findViewById(R.id.tv_genre);
            tv_rating = (TextView) x.findViewById(R.id.tv_rating);
            tv_year = (TextView) x.findViewById(R.id.tv_year);
            tv_quality = (TextView) x.findViewById(R.id.tv_quality);
            card_view = (CardView) x.findViewById(R.id.card_view);
        }

    }



}
