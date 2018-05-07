package com.udacity.maxgaj.popularmovie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.udacity.maxgaj.popularmovie.models.Movie;
import com.udacity.maxgaj.popularmovie.models.Review;
import com.udacity.maxgaj.popularmovie.models.ReviewList;
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

public class DetailActivity extends AppCompatActivity implements Callback<ReviewList> {
    private Movie mMovie;
    private ReviewList reviewList;

    @BindView(R.id.tv_movie_title) TextView mTitleTextView;
    @BindView(R.id.iv_movie_poster) ImageView mMoviePosterImageView;
    @BindView(R.id.tv_movie_original_title) TextView mOriginalTitleTextView;
    @BindView(R.id.tv_movie_release_date) TextView mReleaseDateTextView;
    @BindView(R.id.tv_movie_vote_average) TextView mVoteAverageTextView;
    @BindView(R.id.tv_movie_overview) TextView mOverviewTextView;
    @BindView(R.id.tv_review_content_1) TextView mFirstReviewContent;
    @BindView(R.id.tv_review_author_1) TextView mFirstReviewAuthor;
    @BindView(R.id.tv_review_content_2) TextView mSecondReviewContent;
    @BindView(R.id.tv_review_author_2) TextView mSecondReviewAuthor;
    @BindView(R.id.tv_review_error) TextView mReviewError;


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
            fetchReviews();
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
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<ReviewList> call, Response<ReviewList> response) {
        if(response.isSuccessful()) {
            this.reviewList = response.body();
            List<Review> reviews = this.reviewList.getResults();
            int size = reviews.size();
            if (size == 0){
                this.mReviewError.setVisibility(View.VISIBLE);
                this.mFirstReviewAuthor.setVisibility(View.INVISIBLE);
                this.mFirstReviewContent.setVisibility(View.INVISIBLE);
                this.mSecondReviewAuthor.setVisibility(View.INVISIBLE);
                this.mSecondReviewContent.setVisibility(View.INVISIBLE);
            }
            else {
                this.mReviewError.setVisibility(View.INVISIBLE);
                Review review1 = reviews.get(0);
                this.mFirstReviewContent.setText(review1.getContent());
                this.mFirstReviewAuthor.setText("- "+review1.getAuthor());
                if (size > 1 ){
                    Review review2 = reviews.get(1);
                    this.mSecondReviewContent.setText(review2.getContent());
                    this.mSecondReviewAuthor.setText("- "+review2.getAuthor());
                }
                else {
                    this.mSecondReviewAuthor.setVisibility(View.INVISIBLE);
                    this.mSecondReviewContent.setVisibility(View.INVISIBLE);
                }
            }

        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<ReviewList> call, Throwable t) { t.printStackTrace(); }
}
