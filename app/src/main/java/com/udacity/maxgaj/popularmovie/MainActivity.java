package com.udacity.maxgaj.popularmovie;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.udacity.maxgaj.popularmovie.models.Page;
import com.udacity.maxgaj.popularmovie.utilities.JsonUtils;
import com.udacity.maxgaj.popularmovie.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Page mPage;
    TextView mPageTextView;
    TextView mTotalResultsTextView;
    TextView mTotalPagesTextView;
    TextView mFirstResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPageTextView = (TextView) findViewById(R.id.tv_page);
        mTotalResultsTextView = (TextView) findViewById(R.id.tv_total_results);
        mTotalPagesTextView = (TextView) findViewById(R.id.tv_total_pages);
        mFirstResultTextView = (TextView) findViewById(R.id.tv_first_result);
        URL url = NetworkUtils.buildURL();
        new queryTask().execute(url);
    }

    protected void HydrateUI(){
        if (mPage != null) {
            mPageTextView.setText(Integer.toString(mPage.getPageNumber()));
            mTotalResultsTextView.setText(Integer.toString(mPage.getTotalResults()));
            mTotalPagesTextView.setText(Integer.toString(mPage.getTotalPages()));
            mFirstResultTextView.setText(mPage.getResult(0));
        }
    }

    public class queryTask extends AsyncTask<URL, Void, String>{

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
            if (s != null && !s.equals("")) {
                mPage = JsonUtils.parsePageJson(s);
                HydrateUI();
            }
        }
    }
}
