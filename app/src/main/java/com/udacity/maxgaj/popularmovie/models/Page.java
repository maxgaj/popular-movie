package com.udacity.maxgaj.popularmovie.models;


import java.util.List;

public class Page {
    private int pageNumber;
    private int totalResults;
    private int totalPages;
    private List<Movie> results;

    public Page(){}

    public Page (int pageNumber, int totalResults, int totalPages, List<Movie> results){
        this.pageNumber = pageNumber;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.results = results;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<Movie> getResults() {
        return results;
    }

    public Movie getResult (int index){
        return this.results.get(index);
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
