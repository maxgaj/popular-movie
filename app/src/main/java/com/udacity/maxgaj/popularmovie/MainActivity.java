package com.udacity.maxgaj.popularmovie;

import android.content.SharedPreferences;
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

import com.udacity.maxgaj.popularmovie.models.Page;
import com.udacity.maxgaj.popularmovie.utilities.JsonUtils;
import com.udacity.maxgaj.popularmovie.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        PosterAdapter.PosterAdapterOnClickHandler,
        LoaderCallbacks<String>,
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

        int loaderId = LOADER_ID;
        LoaderCallbacks<String> callbacks = MainActivity.this;
        getSupportLoaderManager().initLoader(loaderId, null, callbacks);
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
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {
            String pageData = null;

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
            public String loadInBackground() {
                URL url = NetworkUtils.buildURL();
                String json = null;
                try {
                    json = NetworkUtils.getJsonFromUrl(url);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return json;
            }

            @Override
            public void deliverResult(String data) {
                pageData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        if (s != null && !s.equals("")) {
            showDataView();
            mPage = JsonUtils.parsePageJson(s);
            if (mPage != null)
                mAdapter.setMovieData(mPage.getResults());
        }
        else {
            showErrorView();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {}


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
        String movieJson = mPage.getResult(clickedItemIndex);
        Intent intent = new Intent(context, destination);
        intent.putExtra(Intent.EXTRA_TEXT, movieJson);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
       if (s.equals(getString(R.string.pref_sort_key))){
           UPDATED_PREFERENCES = true;
           setSortPreference(sharedPreferences.getString(s, getResources().getString(R.string.pref_sort_popular)));
       }
    }
}
