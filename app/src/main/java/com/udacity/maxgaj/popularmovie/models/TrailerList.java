package com.udacity.maxgaj.popularmovie.models;

import java.util.List;

public class TrailerList {
    private int id;
    private List<Trailer> results;

    public TrailerList(){}

    public TrailerList(int id, List<Trailer> results){
        this.id = id;

        this.results = results;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }
}
