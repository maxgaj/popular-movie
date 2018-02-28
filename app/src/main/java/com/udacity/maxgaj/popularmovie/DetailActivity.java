package com.udacity.maxgaj.popularmovie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.maxgaj.popularmovie.models.Movie;
import com.udacity.maxgaj.popularmovie.utilities.JsonUtils;
import com.udacity.maxgaj.popularmovie.utilities.MovieUtils;
import com.udacity.maxgaj.popularmovie.utilities.NetworkUtils;

import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {
    private Movie mMovie;

    private TextView mTitleTextView;
    private ImageView mMoviePosterImageView;
    private TextView mOriginalTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mVoteAverageTextView;
    private TextView mOverviewTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mMoviePosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);
        mOriginalTitleTextView = (TextView) findViewById(R.id.tv_movie_original_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_release_date);
        mVoteAverageTextView = (TextView) findViewById(R.id.tv_movie_vote_average);
        mOverviewTextView = (TextView) findViewById(R.id.tv_movie_overview);

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



        }
    }
}
