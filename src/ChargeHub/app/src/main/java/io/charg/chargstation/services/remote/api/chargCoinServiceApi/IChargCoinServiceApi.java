package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
    Call<OrdersDto> getBuyOrders();

    @GET("api/getSellOrders")
    Call<OrdersDto> getSellOrders();

    @GET("api/getBestSellOrder")
    Call<BestSellOrderDto> getBestSellOrder(
            @Query("amountCHG") int amountChg
    );

    @GET("api/getPaymentData")
    Call<PaymentDataDto> getPaymentData(
            @Query("currency") String currency
    );

    @POST("api/confirmPayment")
    Call<ConfirmPaymentResponseDto> postConfirmPayment(
            @Query("currency") String currency,
            @Query("nodeAddress") String nodeAddress,
            @Query("orderHash") String orderHash,
            @Query("amount") float amount,
            @Query("paymentId") String paymentId,
            @Query("payerId") String payerId
    );

    @POST("api/serviceOn")
    Call<ServiceOnResponseDto> postServiceOn(
            @Query("payerId") String payerId,
            @Query("txHash") String txHash,
            @Query("paymentId") String paymentId
    );

    @POST("api/serviceOff")
    Call<ServiceOnResponseDto> postServiceOff();

    @GET("api/serviceStatus")
    Call<ServiceOnResponseDto> getServiceStatus();

    @GET("api/nodeStatus")
    Call<NodeStatusDto> getNodeStatus();

    @GET("api/getBlockNumber")
    Call<BlockNumberDto> getBlockNumber();

}
