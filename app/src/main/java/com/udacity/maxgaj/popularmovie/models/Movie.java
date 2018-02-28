package com.udacity.maxgaj.popularmovie.models;



public class Movie {
    private String title;
    private String originalTitle;
    private String originalLanguage;
    private String releaseDate;
    private String moviePoster;
    private Double voteAverage;
    private String synopsis;

    public Movie(){}

    public Movie(String title, String originalTitle, String originalLanguage, String releaseDate, String moviePoster, Double voteAverage, String synopsis){
        this.title = title;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.releaseDate = releaseDate;
        this.moviePoster = moviePoster;
        this.voteAverage = voteAverage;
        this.synopsis = synopsis;
    }

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
}
