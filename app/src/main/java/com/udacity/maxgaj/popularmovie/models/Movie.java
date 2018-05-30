package com.udacity.maxgaj.popularmovie.models;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class Movie implements Parcelable {
    private int id;
    private String title;
    private String originalTitle;
    private String originalLanguage;
    private String releaseDate;
    private String moviePoster;
    private Double voteAverage;
    private String synopsis;
    private Bitmap moviePosterBitmap;

    public Movie(){}

    public Movie(int id, String title, String originalTitle, String originalLanguage, String releaseDate, String moviePoster, Double voteAverage, String synopsis){
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.releaseDate = releaseDate;
        this.moviePoster = moviePoster;
        this.voteAverage = voteAverage;
        this.synopsis = synopsis;
    }

    private Movie(Parcel in){
        this.id = in.readInt();
        this.title = in.readString();
        this.originalTitle = in.readString();
        this.originalLanguage = in.readString();
        this.releaseDate = in.readString();
        this.moviePoster = in.readString();
        this.voteAverage = in.readDouble();
        this.synopsis = in.readString();
        this.moviePosterBitmap = Bitmap.CREATOR.createFromParcel(in);
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Bitmap getMoviePosterBitmap() {
        return moviePosterBitmap;
    }

    public void setMoviePosterBitmap(Bitmap moviePosterBitmap) {
        this.moviePosterBitmap = moviePosterBitmap;
    }

    // https://developer.android.com/reference/android/os/Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(originalTitle);
        parcel.writeString(originalLanguage);
        parcel.writeString(releaseDate);
        parcel.writeString(moviePoster);
        parcel.writeDouble(voteAverage);
        parcel.writeString(synopsis);
        moviePosterBitmap.writeToParcel(parcel, 0);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size){
            return new Movie[size];
        }
    };
}
