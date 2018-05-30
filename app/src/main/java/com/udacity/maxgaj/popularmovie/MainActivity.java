package com.udacity.maxgaj.popularmovie;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.udacity.maxgaj.popularmovie.data.PopularMovieContract;
import com.udacity.maxgaj.popularmovie.models.Movie;
import com.udacity.maxgaj.popularmovie.models.Page;
import com.udacity.maxgaj.popularmovie.utilities.JsonUtils;
import com.udacity.maxgaj.popularmovie.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        PosterAdapter.PosterAdapterOnClickHandler,
        LoaderCallbacks<Page>,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private Page mPage;
    private PosterAdapter mAdapter;

    @BindView(R.id.rv_posters) RecyclerView mPosterList;
    @BindView(R.id.error_view) View mErrorView;
    @BindView(R.id.pb_loading) ProgressBar mLoadingProgressBar;

    private static final int LOADER_ID = 10;
    private static boolean UPDATED_PREFERENCES = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Using ButterKnife as suggested by my first reviewer
        ButterKnife.bind(this);

        //handling button according to documentation
        //https://developer.android.com/reference/android/widget/Button.html
        final Button errorButton = findViewById(R.id.error_button);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshData();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mAdapter = new PosterAdapter(this);
        mPosterList.setLayoutManager(layoutManager);
        mPosterList.setHasFixedSize(true);
        mPosterList.setAdapter(mAdapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        setSortPreference(sharedPreferences.getString(getResources().getString(R.string.pref_sort_key), getResources().getString(R.string.pref_sort_popular)));

        LoaderCallbacks<Page> callbacks = MainActivity.this;
        getSupportLoaderManager().initLoader(LOADER_ID, null, callbacks);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (UPDATED_PREFERENCES){
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
            UPDATED_PREFERENCES = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshData();
    }

    private void refreshData(){
        showDataView();
        mAdapter.setMovieData(null);
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void showDataView(){
        mErrorView.setVisibility(View.INVISIBLE);
        mPosterList.setVisibility(View.VISIBLE);
    }

    private void showErrorView(){
        mErrorView.setVisibility(View.VISIBLE);
        mPosterList.setVisibility(View.INVISIBLE);
    }

    private void setSortPreference(String sortPreference){
        if (sortPreference.equals(getResources().getString(R.string.pref_sort_popular)))
            NetworkUtils.setSortingToPopular();
        else if (sortPreference.equals(getResources().getString(R.string.pref_sort_top_rated)))
            NetworkUtils.setSortingToTopRated();
        else if (sortPreference.equals(getResources().getString(R.string.pref_sort_favorite)))
            NetworkUtils.setSortingToFavorite();
    }

    @Override
    public Loader<Page> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<Page>(this) {
            Page pageData = null;

            @Override
            protected void onStartLoading() {
                if (pageData != null){
                    deliverResult(pageData);
                }
                else {
                    mLoadingProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public Page loadInBackground() {
                if (!NetworkUtils.isFavoriteSorting()) {
                    URL url = NetworkUtils.buildURL();
                    String json;
                    try {
                        json = NetworkUtils.getJsonFromUrl(url);
                        Page page = JsonUtils.parsePageJson(json);
                        for (Movie movie : page.getResults()) {
                            String imageUri = NetworkUtils.buildImageUri(movie.getMoviePoster());
                            Bitmap posterBitmap = null;
                            try {
                                posterBitmap = Picasso.with(getContext())
                                        .load(imageUri)
                                        .get();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            movie.setMoviePosterBitmap(posterBitmap);
                        }
                        return page;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                else {
                    try {
                        Cursor cursor = getContentResolver().query(PopularMovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
                        return getPageFromCursor(cursor);
                    } catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }
            }

            @Override
            public void deliverResult(Page data) {
                pageData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Page> loader, Page data) {
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        if (data != null) {
            showDataView();
            mPage = data;
            mAdapter.setMovieData(mPage.getResults());
        } else {
            showErrorView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Page> loader) {}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_refresh:
                refreshData();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onClick(int clickedItemIndex) {
        Context context = this;
        Class destination = DetailActivity.class;
        Movie movie = mPage.getResult(clickedItemIndex);
        Intent intent = new Intent(context, destination);
        intent.putExtra("Movie", movie);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
       if (s.equals(getString(R.string.pref_sort_key))){
           UPDATED_PREFERENCES = true;
           setSortPreference(sharedPreferences.getString(s, getResources().getString(R.string.pref_sort_popular)));
       }
    }

    private Page getPageFromCursor(Cursor cursor){
        List<Movie> results = new ArrayList<>();
        try {
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(PopularMovieContract.MovieEntry.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(PopularMovieContract.MovieEntry.COLUMN_TITLE));
                String originalTitle = cursor.getString(cursor.getColumnIndex(PopularMovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
                String originalLanguage = cursor.getString(cursor.getColumnIndex(PopularMovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE));
                String releaseDate = cursor.getString(cursor.getColumnIndex(PopularMovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                String moviePoster = cursor.getString(cursor.getColumnIndex(PopularMovieContract.MovieEntry.COLUMN_POSTER));
                Double voteAverage = cursor.getDouble(cursor.getColumnIndex(PopularMovieContract.MovieEntry.COLUMN_VOTE));
                String synopsis = cursor.getString(cursor.getColumnIndex(PopularMovieContract.MovieEntry.COLUMN_SYNOPSIS));
                Movie movie = new Movie(id, title, originalTitle, originalLanguage, releaseDate, moviePoster, voteAverage, synopsis);
                byte[] moviePosterBlob = cursor.getBlob(cursor.getColumnIndex(PopularMovieContract.MovieEntry.COLUMN_POSTER_BLOB));
                Bitmap moviePosterBitmap = BitmapFactory.decodeByteArray(moviePosterBlob, 0, moviePosterBlob.length);
                movie.setMoviePosterBitmap(moviePosterBitmap);
                results.add(movie);
            }
        } finally {
            cursor.close();
        }
        int pageNumber = 1;
        int totalPages = 1;
        int totalResults = results.size();
        return new Page(pageNumber, totalResults, totalPages, results);
    }

}
