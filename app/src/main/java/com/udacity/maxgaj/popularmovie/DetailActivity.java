package com.udacity.maxgaj.popularmovie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.udacity.maxgaj.popularmovie.models.Movie;
import com.udacity.maxgaj.popularmovie.utilities.JsonUtils;

import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {
    private Movie mMovie;

    private TextView mTitleTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = (TextView) findViewById(R.id.tv_movie_title);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String movieJson = intent.getStringExtra(Intent.EXTRA_TEXT);
            mMovie = JsonUtils.parseMovieJson(movieJson);
        }

        hydrateUI();
    }

    public void hydrateUI(){
        if (mMovie != null) {
            mTitleTextView.setText(mMovie.getTitle());
        }
    }
}
