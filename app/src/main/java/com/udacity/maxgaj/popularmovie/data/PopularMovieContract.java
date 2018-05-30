package com.udacity.maxgaj.popularmovie.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class PopularMovieContract {

    public static final String AUTHORITY = "com.udacity.maxgaj.popularmovie";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_REVIEW = "reviews";
    public static final String PATH_TRAILER = "trailers";

    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI=
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();
        public static final String TABLE_TRAILER = "trailer";
        public static final String COLUMN_MOVIE = "id_movie";
        public static final String COLUMN_ID = "id_tmdb";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";

        public static Uri buildUriWithMovieId(int id){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }

    }

    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI=
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
        public static final String TABLE_REVIEW = "review";
        public static final String COLUMN_MOVIE = "id_movie";
        public static final String COLUMN_ID = "id_tmdb";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";

        public static Uri buildUriWithMovieId(int id){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }
    }

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI=
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String TABLE_MOVIE = "movie";
        public static final String COLUMN_ID = "id_tmdb";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_POSTER_BLOB = "poster_blob";
        public static final String COLUMN_VOTE = "vote";
        public static final String COLUMN_SYNOPSIS = "synopsis";

        public static Uri buildCountUri(int id){
            Uri uri= CONTENT_URI.buildUpon().appendPath("count").build();
            return uri.buildUpon().appendPath(Integer.toString(id)).build();
        }
    }
}
