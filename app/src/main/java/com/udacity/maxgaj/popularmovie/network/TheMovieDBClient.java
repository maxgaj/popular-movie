package com.udacity.maxgaj.popularmovie.network;

import com.udacity.maxgaj.popularmovie.models.Review;
import com.udacity.maxgaj.popularmovie.models.ReviewList;
import com.udacity.maxgaj.popularmovie.models.TrailerList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface TheMovieDBClient {

    @GET("3/movie/{id}/reviews")
    Call<ReviewList> loadReviews(
            @Path("id") String id,
            @Query("api_key") String api_key
    );

    @GET("3/movie/{id}/videos")
    Call<TrailerList> loadTrailers(
            @Path("id") String id,
            @Query("api_key") String api_key
    );
}
