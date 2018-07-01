package io.charg.chargstation.services;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.charg.chargstation.models.firebase.NodeDto;

public class FavouriteService {

    private static final String FAVOURITE_STATIONS = "FAVOURITE_STATIONS";

    private LocalDB mLocalDb;

    public FavouriteService(Context context) {
        mLocalDb = new LocalDB(context);
    }

    public void addToFavorites(NodeDto station) {
        if (station == null) {
            return;
        }

        List<String> ids = getIds();
        ids.add(station.getEth_address());

        mLocalDb.putValue(FAVOURITE_STATIONS, new Gson().toJson(ids));
    }

    public void removeFromFavorites(NodeDto mStation) {
        if (mStation == null) {
            return;
        }

        List<String> ids = getIds();
        ids.remove(mStation.getEth_address());

        mLocalDb.putValue(FAVOURITE_STATIONS, new Gson().toJson(ids));
    }

    public boolean isFavourite(NodeDto mStation) {
        if (mStation == null) {
            return false;
        }

        return getIds().contains(mStation.getEth_address());
    }

    public List<String> getIds() {
        String json = mLocalDb.getString(FAVOURITE_STATIONS);
        List<String> ids = new ArrayList<>();

        if (!json.isEmpty()) {
            Collections.addAll(ids, new Gson().fromJson(json, String[].class));
        }

        return ids;
    }
}
