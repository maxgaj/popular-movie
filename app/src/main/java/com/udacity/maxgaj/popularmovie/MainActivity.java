package com.udacity.maxgaj.popularmovie;

import android.os.AsyncTask;
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

public class MainActivity extends AppCompatActivity {

    private Page mPage;
    private PosterAdapter mAdapter;

    private RecyclerView mPosterList;
    private View mErrorView;
    private ProgressBar mLoadingProgressBar;


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
        mAdapter = new PosterAdapter();
        mPosterList.setLayoutManager(layoutManager);
        mPosterList.setHasFixedSize(true);
        mPosterList.setAdapter(mAdapter);

        loadData();
    }

    private void loadData(){
        showDataView();
        URL url = NetworkUtils.buildURL();
        new queryTask().execute(url);
    }

    private void refreshData(){
        mAdapter.setMovieData(null);
        loadData();
    }

    private void showDataView(){
        mErrorView.setVisibility(View.INVISIBLE);
        mPosterList.setVisibility(View.VISIBLE);
    }

    private void showErrorView(){
        mErrorView.setVisibility(View.VISIBLE);
        mPosterList.setVisibility(View.INVISIBLE);
    }

    public class queryTask extends AsyncTask<URL, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
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
        protected void onPostExecute(String s) {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            if (s != null && !s.equals("")) {
                mPage = JsonUtils.parsePageJson(s);
                mAdapter.setMovieData(mPage.getResults());
            }
            else {
                showErrorView();
            }
        }
    }

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
}
