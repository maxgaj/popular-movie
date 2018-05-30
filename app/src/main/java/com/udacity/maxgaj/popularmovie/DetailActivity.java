package com.udacity.maxgaj.popularmovie;

import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.udacity.maxgaj.popularmovie.data.PopularMovieContract;
import com.udacity.maxgaj.popularmovie.models.*;
import com.udacity.maxgaj.popularmovie.network.TheMovieDBClient;
import com.udacity.maxgaj.popularmovie.utilities.MovieUtils;
import com.udacity.maxgaj.popularmovie.utilities.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements
        LoaderCallbacks<Cursor> {
    private Movie mMovie;
    private ReviewList mReviewList;
    private TrailerList mTrailerList;

    @BindView(R.id.tv_movie_title) TextView mTitleTextView;
    @BindView(R.id.tb_favorite) ToggleButton mFavoriteToggleButton;
    @BindView(R.id.iv_movie_poster) ImageView mMoviePosterImageView;
    @BindView(R.id.tv_movie_original_title) TextView mOriginalTitleTextView;
    @BindView(R.id.tv_movie_release_date) TextView mReleaseDateTextView;
    @BindView(R.id.tv_movie_vote_average) TextView mVoteAverageTextView;
    @BindView(R.id.tv_movie_overview) TextView mOverviewTextView;
    @BindView(R.id.tv_trailer_error) TextView mTrailerError;
    @BindView(R.id.tv_review_error) TextView mReviewError;
    @BindView(R.id.ll_trailer) LinearLayout mTrailerView;
    @BindView(R.id.ll_review) LinearLayout mReviewView;

    private static final int TRAILERS_LOADER_ID = 11;
    private static final int REVIEWS_LOADER_ID = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("Movie")) {
            mMovie = intent.getParcelableExtra("Movie");
        }

        hydrateUI();
        getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);
    }

    private void hydrateUI(){
        if (mMovie != null) {
            String title = mMovie.getTitle();
            String contentDescription = title+" movie poster";
            mMoviePosterImageView.setImageBitmap(mMovie.getMoviePosterBitmap());
            mMoviePosterImageView.setContentDescription(contentDescription);

            mTitleTextView.setText(title);

            mOriginalTitleTextView.setText(MovieUtils.formatOriginalTitle(mMovie.getOriginalTitle(), mMovie.getOriginalLanguage()));
            mReleaseDateTextView.setText(MovieUtils.formatReleaseDate(mMovie.getReleaseDate()));
            mVoteAverageTextView.setText(MovieUtils.formatNote(mMovie.getVoteAverage()));
            mOverviewTextView.setText(mMovie.getSynopsis());

            // Based on
            // https://developer.android.com/guide/topics/ui/controls/togglebutton
            // https://stackoverflow.com/questions/11499574/toggle-button-using-two-image-on-different-state
            mFavoriteToggleButton.setChecked(isFavoriteChecked());
            mFavoriteToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        addMovieToFavorite();
                        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.favorite_add), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        removeMovieFromFavorite();
                        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.favorite_removed), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });

            fetchTrailers();
            fetchReviews();
        }
    }

    /* FETCHING TRAILERS */
    private void fetchTrailers(){
        if (!NetworkUtils.isFavoriteSorting()) {
            // Fetching from network
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            Gson gson = new GsonBuilder().setLenient().create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getResources().getString(R.string.base_url_tmdb))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();
            TheMovieDBClient movieDBClient = retrofit.create(TheMovieDBClient.class);
            Call<TrailerList> call = movieDBClient.loadTrailers(Integer.toString(mMovie.getId()), BuildConfig.API_KEY);
            call.enqueue(new Callback<TrailerList>() {
                @Override
                public void onResponse(Call<TrailerList> call, Response<TrailerList> response) {
                    if (response.isSuccessful()) {
                        mTrailerList = response.body();
                        List<Trailer> trailers = mTrailerList.getResults();
                        int size = trailers.size();
                        if (size <= 0)
                            mTrailerError.setVisibility(View.VISIBLE);
                        else {
                            mTrailerError.setVisibility(View.INVISIBLE);
                            addTrailersToUI(trailers);
                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<TrailerList> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void addTrailersToUI(List<Trailer> trailers){
        LayoutInflater inflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
        for(Trailer trailer : trailers){
            ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.trailer_list_item, null);
            TextView contentTextView = (TextView) layout.getChildAt(1);
            contentTextView.setText(trailer.getName());
            layout.setOnClickListener(new TrailerListener(trailer.getKey()));
            this.mTrailerView.addView(layout);
        }
    }


    /* FETCHING REVIEWS */
    private void fetchReviews(){
        if (!NetworkUtils.isFavoriteSorting()) {
            // Fetching from network
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            Gson gson = new GsonBuilder().setLenient().create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getResources().getString(R.string.base_url_tmdb))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();
            TheMovieDBClient movieDBClient = retrofit.create(TheMovieDBClient.class);
            Call<ReviewList> call = movieDBClient.loadReviews(Integer.toString(mMovie.getId()), BuildConfig.API_KEY);
            call.enqueue(new Callback<ReviewList>() {
                @Override
                public void onResponse(Call<ReviewList> call, Response<ReviewList> response) {
                    if (response.isSuccessful()) {
                        mReviewList = (ReviewList) response.body();
                        List<Review> reviews = mReviewList.getResults();
                        int size = reviews.size();
                        if (size <= 0)
                            mReviewError.setVisibility(View.VISIBLE);
                        else {
                            mReviewError.setVisibility(View.INVISIBLE);
                            addReviewsToUI(reviews);
                        }

                    } else {
                        System.out.println(response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ReviewList> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void addReviewsToUI(List<Review> reviews){
        LayoutInflater inflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
        for(Review review : reviews){
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.review_list_item, null);
            TextView contentTextView = (TextView) layout.getChildAt(0);
            TextView authorTextView = (TextView) layout.getChildAt(1);
            contentTextView.setText(review.getContent());
            authorTextView.setText("- "+review.getAuthor());
            this.mReviewView.addView(layout);
        }
    }

    /* Data */
    private boolean isFavoriteChecked(){
        int movieId = mMovie.getId();
        Uri uri = PopularMovieContract.MovieEntry.buildCountUri(movieId);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        boolean isChecked = false;
        try {
            cursor.moveToFirst();
            int nb = cursor.getInt(cursor.getColumnIndex("nb"));
            isChecked = nb > 0;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally {
            cursor.close();
        }
        return isChecked;
    }

    private void addMovieToFavorite(){
        // https://stackoverflow.com/questions/6341977/convert-drawable-to-blob-datatype
        Bitmap  moviePosterBitmap = mMovie.getMoviePosterBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        moviePosterBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] moviePosterBlob = stream.toByteArray();



        ContentValues cv = new ContentValues();
        cv.put(PopularMovieContract.MovieEntry.COLUMN_ID, mMovie.getId());
        cv.put(PopularMovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
        cv.put(PopularMovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.getOriginalTitle());
        cv.put(PopularMovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, mMovie.getOriginalLanguage());
        cv.put(PopularMovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
        cv.put(PopularMovieContract.MovieEntry.COLUMN_POSTER, mMovie.getMoviePoster());
        cv.put(PopularMovieContract.MovieEntry.COLUMN_POSTER_BLOB, moviePosterBlob);
        cv.put(PopularMovieContract.MovieEntry.COLUMN_VOTE, mMovie.getVoteAverage());
        cv.put(PopularMovieContract.MovieEntry.COLUMN_SYNOPSIS, mMovie.getSynopsis());
        getContentResolver().insert(PopularMovieContract.MovieEntry.CONTENT_URI, cv);

        int movieID = mMovie.getId();
        if (mTrailerList != null) {
            List<Trailer> trailers = mTrailerList.getResults();
            for (Trailer trailer : trailers) {
                ContentValues cvTrailer = new ContentValues();
                cvTrailer.put(PopularMovieContract.TrailerEntry.COLUMN_MOVIE, movieID);
                cvTrailer.put(PopularMovieContract.TrailerEntry.COLUMN_ID, trailer.getId());
                cvTrailer.put(PopularMovieContract.TrailerEntry.COLUMN_KEY, trailer.getKey());
                cvTrailer.put(PopularMovieContract.TrailerEntry.COLUMN_NAME, trailer.getName());
                getContentResolver().insert(PopularMovieContract.TrailerEntry.CONTENT_URI, cvTrailer);
            }
        }

        if (mReviewList != null) {
            List<Review> reviews = mReviewList.getResults();
            for (Review review : reviews) {
                ContentValues cvReview = new ContentValues();
                cvReview.put(PopularMovieContract.ReviewEntry.COLUMN_MOVIE, movieID);
                cvReview.put(PopularMovieContract.ReviewEntry.COLUMN_ID, review.getId());
                cvReview.put(PopularMovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                cvReview.put(PopularMovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
                cvReview.put(PopularMovieContract.ReviewEntry.COLUMN_URL, review.getUrl());
                getContentResolver().insert(PopularMovieContract.ReviewEntry.CONTENT_URI, cvReview);
            }
        }

    }

    private void removeMovieFromFavorite(){
        int movieId = mMovie.getId();
        Uri uri = PopularMovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(movieId)).build();
        getContentResolver().delete(uri, null, null);
    }



    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        int movieId = mMovie.getId();
        Uri uri;
        CursorLoader cursorLoader;
        switch (i){
            case TRAILERS_LOADER_ID:
                uri = PopularMovieContract.TrailerEntry.buildUriWithMovieId(movieId);
                cursorLoader = new CursorLoader(this, uri, null, null, null, null);
                break;
            case REVIEWS_LOADER_ID:
                uri = PopularMovieContract.ReviewEntry.buildUriWithMovieId(movieId);
                cursorLoader = new CursorLoader(this, uri, null, null, null, null);
                break;
            default:
                cursorLoader = null;
                break;
        }
        return cursorLoader;
    }


    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        int LoaderId = loader.getId();
        int movieId = mMovie.getId();
        switch (LoaderId){
            case TRAILERS_LOADER_ID:
                List<Trailer> trailers = new ArrayList<>();
                try {
                    while (cursor.moveToNext()){
                        String id = cursor.getString(cursor.getColumnIndex(PopularMovieContract.TrailerEntry.COLUMN_ID));
                        String key = cursor.getString(cursor.getColumnIndex(PopularMovieContract.TrailerEntry.COLUMN_KEY));
                        String name = cursor.getString(cursor.getColumnIndex(PopularMovieContract.TrailerEntry.COLUMN_NAME));

                        Trailer trailer = new Trailer(id, key, name);
                        trailers.add(trailer);
                    }
                } finally {
                    cursor.close();
                }
                mTrailerList = new TrailerList(movieId, trailers);
                if (trailers.size() <= 0 )
                    mTrailerError.setVisibility(View.VISIBLE);
                else {
                    mTrailerError.setVisibility(View.INVISIBLE);
                    addTrailersToUI(trailers);
                }
                break;
            case REVIEWS_LOADER_ID:
                List<Review> reviews = new ArrayList<>();
                try {
                    while (cursor.moveToNext()){
                        String id = cursor.getString(cursor.getColumnIndex(PopularMovieContract.ReviewEntry.COLUMN_ID));
                        String author = cursor.getString(cursor.getColumnIndex(PopularMovieContract.ReviewEntry.COLUMN_AUTHOR));
                        String content = cursor.getString(cursor.getColumnIndex(PopularMovieContract.ReviewEntry.COLUMN_CONTENT));
                        String url = cursor.getString(cursor.getColumnIndex(PopularMovieContract.ReviewEntry.COLUMN_URL));

                        Review review = new Review(author, content, id, url);
                        reviews.add(review);
                    }
                } finally {
                    cursor.close();
                }
                mReviewList = new ReviewList(movieId, 1, reviews, 1, reviews.size());
                if (reviews.size() <= 0 )
                    mReviewError.setVisibility(View.VISIBLE);
                else {
                    mReviewError.setVisibility(View.INVISIBLE);
                    addReviewsToUI(reviews);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {}
}
