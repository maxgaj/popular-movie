package com.udacity.maxgaj.popularmovie.utilities;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class MovieUtils {
    final static String MAX_NOTE = "10";

    public static String formatOriginalTitle(String originalTitle, String originalLanguage) {
        return originalTitle + " (" + originalLanguage + ")";
    }

    public static String formatNote(Double voteAverage) {
        String voteAverageString = voteAverage.toString();
        return voteAverageString + "/" + MAX_NOTE;
    }

    // based on documentation
    // https://developer.android.com/reference/java/text/SimpleDateFormat.html
    public static String formatReleaseDate(String releaseDate){
         try {
             SimpleDateFormat originalFormatter = new SimpleDateFormat("yyyy-MM-dd");
             Date date = originalFormatter.parse(releaseDate);
             SimpleDateFormat newFormatter = new SimpleDateFormat("yyyy, MMMM dd");
             return newFormatter.format(date);
         }
         catch (ParseException e){
             // if formatting fails, return original string
             return releaseDate;
         }
    }
}



