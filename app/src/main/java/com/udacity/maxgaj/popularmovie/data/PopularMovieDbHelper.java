package com.udacity.maxgaj.popularmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.udacity.maxgaj.popularmovie.data.PopularMovieContract.*;

public class PopularMovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "popular_movie.db";
    private static final int DATABASE_VERSION = 1;

    public PopularMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_MOVIE + " (" +
                        MovieEntry._ID                      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_ID                + " INTEGER, " +
                        MovieEntry.COLUMN_TITLE             + " TEXT, " +
                        MovieEntry.COLUMN_ORIGIBAL_TITLE    + " TEXT, " +
                        MovieEntry.COLUMN_RELEASE_DATE      + " TEXT, " +
                        MovieEntry.COLUMN_POSTER            + " TEXT, " +
                        MovieEntry.COLUMN_POSTER_DATA       + " BLOB, " +
                        MovieEntry.COLUMN_VOTE              + " REAL, " +
                        MovieEntry.COLUMN_SYNOPSIS          + " TEXT"
                + ");";

        final String SQL_CREATE_REVIEW_TABLE =
                "CREATE TABLE " + ReviewEntry.TABLE_REVIEW + " (" +
                        ReviewEntry._ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ReviewEntry.COLUMN_MOVIE    + " INTEGER, " +
                        ReviewEntry.COLUMN_ID       + " INTEGER, " +
                        ReviewEntry.COLUMN_AUTHOR   + " TEXT, " +
                        ReviewEntry.COLUMN_CONTENT  + " TEXT, " +
                        ReviewEntry.COLUMN_URL      + " TEXT"
                + " )";

        final String SQL_CREATE_TRAILER_TABLE =
                "CREATE TABLE " + TrailerEntry.TABLE_TRAILER + " (" +
                        TrailerEntry._ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TrailerEntry.COLUMN_MOVIE   + " INTEGER, " +
                        TrailerEntry.COLUMN_ID      + " TEXT, " +
                        TrailerEntry.COLUMN_KEY     + " TEXT, " +
                        TrailerEntry.COLUMN_NAME    + " TEXT"
                + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_REVIEW);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_TRAILER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_MOVIE);
        onCreate(sqLiteDatabase);
    }
}
