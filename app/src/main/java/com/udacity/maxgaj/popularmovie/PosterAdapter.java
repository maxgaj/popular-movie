package com.udacity.maxgaj.popularmovie;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.maxgaj.popularmovie.data.PopularMovieContract;
import com.udacity.maxgaj.popularmovie.data.PopularMovieDbHelper;
import com.udacity.maxgaj.popularmovie.utilities.JsonUtils;
import com.udacity.maxgaj.popularmovie.utilities.NetworkUtils;

import java.util.List;


public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder>  {
    private List<String> mMovieData;
    private final PosterAdapterOnClickHandler mClickHandler;
    private SQLiteDatabase mDb;

    public PosterAdapter(PosterAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

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
        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(context);
        mDb = dbHelper.getReadableDatabase();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutID = R.layout.poster_list_item;
        View view = inflater.inflate(layoutID, parent, false);

        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        if (!NetworkUtils.isFavoriteSorting()) {
            String movieJson = mMovieData.get(position);
            String imagePath = JsonUtils.getImagePathFromMovieJson(movieJson);
            String movieTitle = JsonUtils.getTitleFromMovieJson(movieJson);
            String imageUri = NetworkUtils.buildImageUri(imagePath);
            holder.posterListItemImageView.setContentDescription(movieTitle);
            Picasso.with(holder.posterListItemImageView.getContext())
                    .load(imageUri)
                    .into(holder.posterListItemImageView);
        }
        else {
            String movieJson = mMovieData.get(position);
            String movieTitle = JsonUtils.getTitleFromMovieJson(movieJson);
            holder.posterListItemImageView.setContentDescription(movieTitle);
            int movieId = JsonUtils.getIdFromMovieJson(movieJson);
            Cursor cursor = mDb.query(
                    PopularMovieContract.MovieEntry.TABLE_MOVIE,
                    new String[]{PopularMovieContract.MovieEntry.COLUMN_POSTER_BLOB},
                    PopularMovieContract.MovieEntry.COLUMN_ID + "=" + movieId,
                    null,
                    null,
                    null,
                    null
            );
            try {
                if (cursor != null) {
                    cursor.moveToFirst();
                    byte[] moviePosterBlob = cursor.getBlob(cursor.getColumnIndex(PopularMovieContract.MovieEntry.COLUMN_POSTER_BLOB));
                    Bitmap moviePosterBitmap = BitmapFactory.decodeByteArray(moviePosterBlob, 0, moviePosterBlob.length);
                    holder.posterListItemImageView.setImageBitmap(moviePosterBitmap);
                }
            } finally {
                cursor.close();
            }
        }
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
