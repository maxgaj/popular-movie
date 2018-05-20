package com.udacity.maxgaj.popularmovie.data;

import android.provider.BaseColumns;

public class PopularMovieContract {

    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_TRAILER = "trailer";
        public static final String COLUMN_MOVIE = "id_movie";
        public static final String COLUMN_ID = "id_tmdb";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";

    }

    public static final class ReviewEntry implements BaseColumns {
        public static final String TABLE_REVIEW = "review";
        public static final String COLUMN_MOVIE = "id_movie";
        public static final String COLUMN_ID = "id_tmdb";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";
    }

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_MOVIE = "movie";
        public static final String COLUMN_ID = "id_tmdb";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGIBAL_TITLE = "original_title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_POSTER_DATA = "poster_data";
        public static final String COLUMN_VOTE = "vote";
        public static final String COLUMN_SYNOPSIS = "synopsis";
    }
}
