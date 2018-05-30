package com.udacity.maxgaj.popularmovie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.udacity.maxgaj.popularmovie.models.Movie;


import java.util.List;


public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder>  {
    private List<Movie> mMovieData;
    private final PosterAdapterOnClickHandler mClickHandler;

    public PosterAdapter(PosterAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public void setMovieData(List<Movie> movieData){
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
        Movie movie = mMovieData.get(position);
        String movieTitle = movie.getTitle();
        holder.posterListItemImageView.setContentDescription(movieTitle);
        holder.posterListItemImageView.setImageBitmap(movie.getMoviePosterBitmap());
    }

    public interface PosterAdapterOnClickHandler {
        void onClick(int clickedItemIndex);
    }


    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView posterListItemImageView;

        public PosterViewHolder(View itemView){
            super(itemView);
            posterListItemImageView = (ImageView) itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        public void onClick(View view){
            int clickedItemIndex = getAdapterPosition();
            mClickHandler.onClick(clickedItemIndex);
        }
    }
}
