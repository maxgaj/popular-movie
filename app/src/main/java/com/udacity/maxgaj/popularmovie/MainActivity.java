package com.udacity.maxgaj.popularmovie;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.udacity.maxgaj.popularmovie.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView mUrlTextView;
    TextView mJsonTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUrlTextView = (TextView) findViewById(R.id.tv_url);
        mJsonTextView = (TextView) findViewById(R.id.tv_json);
        URL url = NetworkUtils.buildURL();
        mUrlTextView.setText(url.toString());
        new queryTask().execute(url);
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
            if (s != null && !s.equals(""))
                mJsonTextView.setText(s);
        }
    }
}
