package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IChargCoinServiceApi {

    @GET("api/getConfig")
    Call<ConfigDto> getConfig();

    @GET("api/getLocation")
    Call<LocationDto> getLocation();

    @GET("api/getFees")
    Call<Map<String, FeesDto>> getFees();

    @GET("api/getRates")
    Call<RatesDto> getRates();

    @GET("api/getNodes")
    Call<Map<String, NodeDto>> getNodes(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("radius") int radius
    );

    @GET("api/getBuyOrders")
    Call<BuyOrdersDto> getBuyOrders();

}
