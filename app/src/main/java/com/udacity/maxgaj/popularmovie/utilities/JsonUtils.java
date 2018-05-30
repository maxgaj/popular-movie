package com.udacity.maxgaj.popularmovie.utilities;


import android.util.Log;

import com.udacity.maxgaj.popularmovie.models.Movie;
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
            List<String> moviesJson = parseJsonArrayToList(resultsArray);
            List<Movie> results = new ArrayList<>();
            for (String movieJson : moviesJson){
                Movie movie = parseMovieJson(movieJson);
                results.add(movie);
            }
            return new Page(pageNumber, totalResults, totalPages, results);

        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Movie parseMovieJson(String json){
        try {
            JSONObject movieData = new JSONObject(json);
            int id = movieData.optInt("id");
            String title = movieData.optString("title");
            String originalTitle = movieData.optString("original_title");
            String originalLanguage = movieData.optString("original_language");
            String releaseDate = movieData.optString("release_date");
            String moviePoster = movieData.optString("poster_path");
            Double voteAverage = movieData.optDouble("vote_average");
            String synopsis = movieData.optString("overview");
            return new Movie(id, title, originalTitle, originalLanguage, releaseDate, moviePoster, voteAverage, synopsis);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String parseMovieToJson(Movie movie){
        JSONObject movieData = new JSONObject();
        try {
            movieData.put("id", movie.getId());
            movieData.put("title", movie.getTitle());
            movieData.put("original_title", movie.getOriginalTitle());
            movieData.put("original_language", movie.getOriginalLanguage());
            movieData.put("release_date", movie.getReleaseDate());
            movieData.put("poster_path", movie.getMoviePoster());
            movieData.put("vote_average", movie.getVoteAverage());
            movieData.put("overview", movie.getSynopsis());
            return movieData.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
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

    public static String getTitleFromMovieJson (String json){
        try {
            JSONObject movieData = new JSONObject(json);
            String title = movieData.optString("title");
            return title;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getIdFromMovieJson (String json){
        try {
            JSONObject movieData = new JSONObject(json);
            int id = movieData.optInt("id");
            return id;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return -1;
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
