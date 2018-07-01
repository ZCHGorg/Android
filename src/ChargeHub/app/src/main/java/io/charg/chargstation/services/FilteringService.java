package io.charg.chargstation.services;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.charg.chargstation.models.StationFilter;
import io.charg.chargstation.models.firebase.NodeDto;

/**
 * Created by worker on 20.12.2017.
 */

public class FilteringService {

    private LocalDB mLocalDb;

    public FilteringService(Context context) {
        mLocalDb = new LocalDB(context);
    }

    public List<StationFilter> getConnectorFilters() {
        List<StationFilter> res = new ArrayList<>();
        for (int i = -1; i < 37; i++) {
            res.add(loadConnectorFilter(i));
        }
        res.add(loadConnectorFilter(1036));
        res.add(loadConnectorFilter(1037));
        return res;
    }

    public StationFilter loadConnectorFilter(int connector_type) {

        String json = mLocalDb.getString(String.format("CONNECTOR_FILTER_%s", connector_type));
        if (json.isEmpty()) {
            for (StationFilter item : FilteringService.getStaticConnectorFilters()) {
                if (item.getIndex() == connector_type) {
                    return item;
                }
            }
            return new StationFilter(connector_type, String.format("Connector: %s", connector_type));
        } else {
            return new Gson().fromJson(json, StationFilter.class);
        }
    }

    public void saveConnectorFilter(StationFilter item) {
        mLocalDb.putValue(String.format("CONNECTOR_FILTER_%s", item.getIndex()), new Gson().toJson(item));
    }

    public static List<StationFilter> getStaticConnectorFilters() {
        List<StationFilter> res = new ArrayList<>();
        res.add(new StationFilter(-1, "Tesla Battery Swap"));
        res.add(new StationFilter(0, "N/A"));
        res.add(new StationFilter(1, "J1772"));
        res.add(new StationFilter(2, "CHAdeMO"));
        res.add(new StationFilter(3, "BS1363 3 Pin 13 Amp"));
        res.add(new StationFilter(4, "Blue Commando (2P+E)"));
        res.add(new StationFilter(5, "LP Inductive"));
        res.add(new StationFilter(6, "SP Inductive"));
        res.add(new StationFilter(7, "Avcon Connector"));
        res.add(new StationFilter(8, "Tesla (Roadster)"));
        res.add(new StationFilter(9, "NEMA 5-20R"));
        res.add(new StationFilter(10, "NEMA 14-30"));
        res.add(new StationFilter(11, "NEMA 14-50"));
        res.add(new StationFilter(13, "Europlug 2-Pin (CEE 7/16)"));
        res.add(new StationFilter(14, "NEMA 6-20"));
        res.add(new StationFilter(15, "NEMA 6-15"));
        res.add(new StationFilter(16, "CEE 3 Pin"));
        res.add(new StationFilter(17, "CEE 5 Pin"));
        res.add(new StationFilter(18, "CEE+ 7 Pin"));
        res.add(new StationFilter(21, "XLR Plug (4 pin)"));
        res.add(new StationFilter(22, "NEMA 5-15R"));
        res.add(new StationFilter(23, "CEE 7/5"));
        res.add(new StationFilter(24, "Wireless Charging"));
        res.add(new StationFilter(25, "Mennekes (Type 2)"));
        res.add(new StationFilter(26, "SCAME Type 3C (Schnieder-Legrand)"));
        res.add(new StationFilter(27, "Tesla Supercharger"));
        res.add(new StationFilter(28, "CEE 7/4 - Schuko - Type F"));
        res.add(new StationFilter(29, "Type I (AS 3112)"));
        res.add(new StationFilter(30, "Tesla (Model S onwards)"));
        res.add(new StationFilter(32, "SAE Combo (DC Fast Charge J1772)"));
        res.add(new StationFilter(33, "CCS (Type 2 Version of Combined Coupler)"));
        res.add(new StationFilter(34, "IEC 60309 3-pin"));
        res.add(new StationFilter(35, "IEC 60309 5-pin"));
        res.add(new StationFilter(36, "SCAME Type 3A (Low Power)"));
        res.add(new StationFilter(1036, "Mennekes (Type 2, Tethered Connector)"));
        res.add(new StationFilter(1037, "T13 - SEC1011 (Swiss domestic 3-pin) - Type J"));
        return res;
    }

    public boolean isValid(NodeDto station) {
        if (station == null) {
            return true;
        }

        return loadConnectorFilter(station.getConnector_type()).isChecked();
    }
}
