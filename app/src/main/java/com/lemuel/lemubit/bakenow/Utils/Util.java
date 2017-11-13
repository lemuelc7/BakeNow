package com.lemuel.lemubit.bakenow.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

/**
 * Created by charl on 10/11/2017.
 */

public class Util {


    @NonNull
    public static Boolean emptyString(String value)
    {
        if (value.isEmpty() || value.length() < 1 || value == null || value=="") {
            return true;//if empty or null return true
        } else {
            return false;//return false if not empty
        }

    }



    @NonNull
    public static Boolean isLargeScreen(Activity context)
    {
        //CALCULATE SCREEN SIZE, IN THIS CASE LARGE SCREENS ARE >700
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;
        float smallestWidth = Math.min(widthDp, heightDp);
        if(smallestWidth>=700)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @NonNull
    public static Boolean isMediumScreen(Activity context)
    {
        //CALCULATE SCREEN SIZE, IN THIS CASE MEDIUM SCREENS ARE >=600
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;
        float smallestWidth = Math.min(widthDp, heightDp);
        if(smallestWidth>=600)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @NonNull
    public static Boolean ObjectisNull(Object value) {
        if (value == null) {
            return true;
        } else {
            return false;
        }
    }

    @NonNull
    public static Boolean ObjectisNotNull(Object value) {
        if (value == null) {
            return false;
        } else {
            return true;
        }
    }

}
