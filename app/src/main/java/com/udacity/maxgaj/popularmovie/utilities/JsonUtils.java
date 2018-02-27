package com.udacity.maxgaj.popularmovie.utilities;


import android.util.Log;

import com.udacity.maxgaj.popularmovie.models.Page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class JsonUtils {

    public static Page parsePageJson(String json){
        try {
            JSONObject pageData = new JSONObject(json);
            JSONArray resultsArray = pageData.optJSONArray("results");

            int pageNumber = pageData.optInt("page");
            int totalResults = pageData.optInt("total_results");
            int totalPages = pageData.optInt("total_pages");
            List<String> results = parseJsonArrayToList(resultsArray);
            return new Page(pageNumber, totalResults, totalPages, results);

        }
        catch (JSONException e) {
            e.printStackTrace();
            return new Page(1,1,1,new ArrayList<String>());
        }
    }

    public static String getImagePathFromMovieJson (String json){
        try {
            JSONObject movieData = new JSONObject(json);
            String imagePath = movieData.optString("poster_path");
            return imagePath;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Using the same code I created for Sandwich club project
    // using optString as suggested by my reviewer
    private static List<String> parseJsonArrayToList(JSONArray array){
        List<String> list = new ArrayList<>();
        int arrayLength = array.length();
        if (arrayLength >0){
            for (int i=0; i<arrayLength; i++)
                list.add(array.optString(i));
        }
        return list;
    }


}
