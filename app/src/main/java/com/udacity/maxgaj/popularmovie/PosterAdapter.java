package com.udacity.maxgaj.popularmovie;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.maxgaj.popularmovie.utilities.JsonUtils;
import com.udacity.maxgaj.popularmovie.utilities.NetworkUtils;

import java.util.List;


public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder>  {
    private List<String> mMovieData;

    public PosterAdapter(){}

    public void setMovieData(List<String> movieData){
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null)
            return 0;
        return mMovieData.size();
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutID = R.layout.poster_list_item;
        View view = inflater.inflate(layoutID, parent, false);

        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        String movieJson = mMovieData.get(position);
        String imagePath = JsonUtils.getImagePathFromMovieJson(movieJson);
        String imageUri = NetworkUtils.buildImageUri(imagePath);
        Picasso.with(holder.posterListItemImageView.getContext())
                .load(imageUri)
                .into(holder.posterListItemImageView);
    }


    class PosterViewHolder extends RecyclerView.ViewHolder {
        ImageView posterListItemImageView;

        public PosterViewHolder(View itemView){
            super(itemView);
            posterListItemImageView = (ImageView) itemView.findViewById(R.id.iv_poster);
        }
    }
}
