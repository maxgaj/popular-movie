package com.udacity.maxgaj.popularmovie.utilities;

import android.net.Uri;
import com.udacity.maxgaj.popularmovie.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Utilities to communicate with TMDb
 */
public final class NetworkUtils {
    // Constants to help building the URL
    final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie";
    final static String POPULAR_END_POINT = "popular";
    final static String TOP_RATED_END_POINT = "top_rated";
    final static String API_KEY_PARAM = "api_key";
    final static String TMDB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p";
    final static String TMDB_IMAGE_SIZE_PATH = "/w185";

    private static String sortingPreference = POPULAR_END_POINT;
    private static boolean favoriteSorting = false;

    private static final String api_key = BuildConfig.API_KEY;

    public static URL buildURL(){
        Uri uri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(sortingPreference)
                .appendQueryParameter(API_KEY_PARAM, api_key)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String buildImageUri(String imagePath){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TMDB_IMAGE_BASE_URL);
        stringBuilder.append(TMDB_IMAGE_SIZE_PATH);
        stringBuilder.append(imagePath);
        return stringBuilder.toString();
    }

//    Code based on what we learn in the Sunshine project
//    As well as the suggested discussion
//    https://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
    public static String getJsonFromUrl (URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            // using \A as delimiter ("beginning of the input boundary")
            // gives us only one token for the entire content of the stream
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput)
                return scanner.next();
            else
                return null;
        }
        finally {
            urlConnection.disconnect();
        }
    }

    public static void setSortingToPopular(){
        favoriteSorting = false;
        sortingPreference = POPULAR_END_POINT;
    }
    public static void setSortingToTopRated(){
        favoriteSorting = false;
        sortingPreference = TOP_RATED_END_POINT;
    }
    public static void setSortingToFavorite(){
        favoriteSorting = true;
    }
    public static boolean isFavoriteSorting(){
        return favoriteSorting;
    }
}
