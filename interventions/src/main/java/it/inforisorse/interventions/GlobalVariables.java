package it.inforisorse.interventions;

import android.app.Application;

import it.inforisorse.interventions.db.DatabaseManager;

/**
 * Created by amedeo on 07/05/15.
 */
public class GlobalVariables extends Application {
    private int flagListFilter = DatabaseManager.FLAG_BILLED_ALL;
    private String searchListFilter = "";

    public int getFlagListFilter() {
        return flagListFilter;
    }

    public void setFlagListFilter(int value) {
        flagListFilter = value;
    }

    public String getSearchListFilter() {
        return searchListFilter;
    }

    public void setSearchListFilter(String value) {
        searchListFilter = value;
    }
}
