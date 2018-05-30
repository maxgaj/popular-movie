package com.udacity.maxgaj.popularmovie.data;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.udacity.maxgaj.popularmovie.data.PopularMovieContract.MovieEntry.TABLE_MOVIE;
import static com.udacity.maxgaj.popularmovie.data.PopularMovieContract.ReviewEntry.TABLE_REVIEW;
import static com.udacity.maxgaj.popularmovie.data.PopularMovieContract.TrailerEntry.TABLE_TRAILER;

public class PopularMovieContentProvider extends ContentProvider {
    private PopularMovieDbHelper mDbHelper;

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;
    public static final int MOVIES_COUNT = 102;
    public static final int REVIEWS = 200;
    public static final int REVIEWS_WITH_MOVIE_ID = 201;
    public static final int TRAILERS = 300;
    public static final int TRAILERS_WITH_MOVIE_ID = 301;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PopularMovieContract.AUTHORITY, PopularMovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(PopularMovieContract.AUTHORITY, PopularMovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);
        uriMatcher.addURI(PopularMovieContract.AUTHORITY, PopularMovieContract.PATH_MOVIES + "/count/#", MOVIES_COUNT);
        uriMatcher.addURI(PopularMovieContract.AUTHORITY, PopularMovieContract.PATH_REVIEW, REVIEWS);
        uriMatcher.addURI(PopularMovieContract.AUTHORITY, PopularMovieContract.PATH_REVIEW + "/#", REVIEWS_WITH_MOVIE_ID);
        uriMatcher.addURI(PopularMovieContract.AUTHORITY, PopularMovieContract.PATH_TRAILER, TRAILERS);
        uriMatcher.addURI(PopularMovieContract.AUTHORITY, PopularMovieContract.PATH_TRAILER + "/#", TRAILERS_WITH_MOVIE_ID);
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new PopularMovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match){
            case MOVIES:
                cursor = db.query(TABLE_MOVIE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIES_COUNT:
                String idMovie = uri.getPathSegments().get(2);
                projection = new String[] { "COUNT(*) AS nb" };
                selection = PopularMovieContract.MovieEntry.COLUMN_ID+"=?";
                selectionArgs = new String[]{idMovie};
                cursor = db.query(TABLE_MOVIE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TRAILERS_WITH_MOVIE_ID:
                String idTrailer = uri.getPathSegments().get(1);
                selection = "id_movie=?";
                selectionArgs = new String[]{idTrailer};
                cursor = db.query(TABLE_TRAILER, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case REVIEWS_WITH_MOVIE_ID:
                String idReview = uri.getPathSegments().get(1);
                selection = "id_movie=?";
                selectionArgs = new String[]{idReview};
                cursor = db.query(TABLE_REVIEW, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        long id;
        switch (match){
            case MOVIES:
                id = db.insert(TABLE_MOVIE, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(PopularMovieContract.MovieEntry.CONTENT_URI, id);
                }
                else {
                    throw new SQLException("Failed to insert row into "+uri);
                }
                break;
            case REVIEWS:
                id = db.insert(TABLE_REVIEW, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(PopularMovieContract.ReviewEntry.CONTENT_URI, id);
                }
                else {
                    throw new SQLException("Failed to insert row into "+uri);
                }
                break;
            case TRAILERS:
                id = db.insert(TABLE_TRAILER, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(PopularMovieContract.TrailerEntry.CONTENT_URI, id);
                }
                else {
                    throw new SQLException("Failed to insert row into "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int moviesDeleted;
        switch(match){
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                db.delete(PopularMovieContract.ReviewEntry.TABLE_REVIEW, PopularMovieContract.ReviewEntry.COLUMN_MOVIE + "=" + id, null);
                db.delete(PopularMovieContract.TrailerEntry.TABLE_TRAILER, PopularMovieContract.TrailerEntry.COLUMN_MOVIE + "=" + id, null);
                moviesDeleted = db.delete(PopularMovieContract.MovieEntry.TABLE_MOVIE, PopularMovieContract.MovieEntry.COLUMN_ID + "=" + id, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        if (moviesDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
