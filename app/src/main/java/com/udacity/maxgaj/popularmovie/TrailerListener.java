package com.udacity.maxgaj.popularmovie;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class TrailerListener implements View.OnClickListener {

    private String key;

    public TrailerListener(String key){
        this.key=key;
    }

    @Override
    public void onClick(View view) {
        String uriString ="http://www.youtube.com/watch?v="+key;
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        view.getContext().startActivity(intent);
    }
}
