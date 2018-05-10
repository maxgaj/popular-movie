package com.udacity.maxgaj.popularmovie;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.udacity.maxgaj.popularmovie.models.*;
import com.udacity.maxgaj.popularmovie.network.TheMovieDBClient;
import com.udacity.maxgaj.popularmovie.utilities.JsonUtils;
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

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private Movie mMovie;
    private ReviewList mReviewList;
    private TrailerList mTrailerList;

    @BindView(R.id.tv_movie_title) TextView mTitleTextView;
    @BindView(R.id.iv_movie_poster) ImageView mMoviePosterImageView;
    @BindView(R.id.tv_movie_original_title) TextView mOriginalTitleTextView;
    @BindView(R.id.tv_movie_release_date) TextView mReleaseDateTextView;
    @BindView(R.id.tv_movie_vote_average) TextView mVoteAverageTextView;
    @BindView(R.id.tv_movie_overview) TextView mOverviewTextView;
    @BindView(R.id.tv_trailer_error) TextView mTrailerError;
    @BindView(R.id.tv_review_error) TextView mReviewError;
    @BindView(R.id.ll_trailer) LinearLayout mTrailerView;
    @BindView(R.id.ll_review) LinearLayout mReviewView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String movieJson = intent.getStringExtra(Intent.EXTRA_TEXT);
            mMovie = JsonUtils.parseMovieJson(movieJson);
        }

        hydrateUI();
    }

    public void hydrateUI(){
        if (mMovie != null) {
            String title = mMovie.getTitle();
            String imageUri = NetworkUtils.buildImageUri(mMovie.getMoviePoster());
            String contentDescription = title+" movie poster";

            mTitleTextView.setText(title);

            mMoviePosterImageView.setContentDescription(contentDescription);
            Picasso.with(this)
                    .load(imageUri)
                    .into(mMoviePosterImageView);

            mOriginalTitleTextView.setText(MovieUtils.formatOriginalTitle(mMovie.getOriginalTitle(), mMovie.getOriginalLanguage()));
            mReleaseDateTextView.setText(MovieUtils.formatReleaseDate(mMovie.getReleaseDate()));
            mVoteAverageTextView.setText(MovieUtils.formatNote(mMovie.getVoteAverage()));
            mOverviewTextView.setText(mMovie.getSynopsis());
            fetchTrailers();
            fetchReviews();
        }
    }

    /* FETCHING TRAILERS */
    private void fetchTrailers(){
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
                if(response.isSuccessful()) {
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
                if(response.isSuccessful()) {
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
}
