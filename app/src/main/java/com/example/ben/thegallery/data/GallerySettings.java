package com.example.ben.thegallery.data;

import android.graphics.Color;

/**
 * Created by dropwisepop on 3/10/2017.
 */

public class GallerySettings {

    //region Member Variables
    private static final int DIRECTION_ASCENDING = 1; private static int DIRECTION_DESCENDING = -1;
    private static int sDirection = DIRECTION_ASCENDING;
    private static int sToolbarColor = Color.BLACK;
    private static boolean sToolbarShown = false;

    private static GallerySettings mSettings = new GallerySettings();
    //endregion

    //region Getters and Setters
    public static int getDirection() {
        return sDirection;
    }

    public static void reverseDirection() {
        sDirection *= -1;
    }

    public static void setDirectionAscending() {
        sDirection = DIRECTION_ASCENDING;
    }

    public static void setDirectionDescending(){
        sDirection = DIRECTION_DESCENDING;
    }

    public static int getToolbarColor() {
        return sToolbarColor;
    }

    public static void setToolbarColor(int toolbarColor) {
        sToolbarColor = toolbarColor;
    }

    public static boolean toolbarShown() {
        return sToolbarShown;
    }

    public static void setToolbarShown(boolean toolbarShown) {
        sToolbarShown = toolbarShown;
    }

    //endregion
}
