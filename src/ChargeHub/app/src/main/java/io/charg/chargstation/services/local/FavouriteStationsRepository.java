package io.charg.chargstation.services.local;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavouriteStationsRepository {

    private static final String FAVOURITE_STATIONS = "FAVOURITE_STATIONS";

    private LocalDB mLocalDb;

    public FavouriteStationsRepository(Context context) {
        mLocalDb = new LocalDB(context);
    }

    public void addToFavorites(String ethAddress) {

        List<String> ids = getItems();
        ids.add(ethAddress);

        mLocalDb.putValue(FAVOURITE_STATIONS, new Gson().toJson(ids));
    }

    public void removeFromFavorites(String ethAddress) {
        List<String> ids = getItems();
        ids.remove(ethAddress);
        mLocalDb.putValue(FAVOURITE_STATIONS, new Gson().toJson(ids));
    }

    public boolean isFavourite(String ethAddress) {
        return getItems().contains(ethAddress);
    }

    public List<String> getItems() {
        String json = mLocalDb.getString(FAVOURITE_STATIONS);
        List<String> ids = new ArrayList<>();

        if (!json.isEmpty()) {
            Collections.addAll(ids, new Gson().fromJson(json, String[].class));
        }

        return ids;
    }
}
