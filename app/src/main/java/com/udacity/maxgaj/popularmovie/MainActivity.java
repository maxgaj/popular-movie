package com.udacity.maxgaj.popularmovie;

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements
        PosterAdapter.PosterAdapterOnClickHandler,
        LoaderCallbacks<String> {

    private Page mPage;
    private PosterAdapter mAdapter;

    private RecyclerView mPosterList;
    private View mErrorView;
    private ProgressBar mLoadingProgressBar;

    private static final int LOADER_ID = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //handling button
        //according to documentation
        //https://developer.android.com/reference/android/widget/Button.html
        final Button errorButton = findViewById(R.id.error_button);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshData();
            }
        });

        mPosterList = (RecyclerView) findViewById(R.id.rv_posters);
        mErrorView = findViewById(R.id.error_view);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mAdapter = new PosterAdapter(this);
        mPosterList.setLayoutManager(layoutManager);
        mPosterList.setHasFixedSize(true);
        mPosterList.setAdapter(mAdapter);

        int loaderId = LOADER_ID;
        LoaderCallbacks<String> callbacks = MainActivity.this;
        getSupportLoaderManager().initLoader(loaderId, null, callbacks);
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
}
