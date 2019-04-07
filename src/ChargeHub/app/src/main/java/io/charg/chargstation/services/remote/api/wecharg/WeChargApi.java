package io.charg.chargstation.services.remote.api.wecharg;

import io.charg.chargstation.services.remote.api.socketio.BraintreeTokenDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WeChargApi {

    @POST("integration/customer/token")
    Call<String> getTokenAsync(@Body TokenRequestDto tokenRequest);

    @POST("carts/mine")
    Call<String> createQuoteAsync(@Header("Authorization") String auth);

    @POST("carts/mine/items")
    Call<CartItemResponseDto> addItemToCart(@Header("Authorization") String auth, @Body CartItemRequestDto body);

    @POST("carts/mine/payment-information")
    Call<String> createOrderAsync(@Header("Authorization") String auth, @Body PaymentInformationDto body);

    @POST("charg/payment/generate")
    Call<BraintreeTokenDto> getBrainTreeTokenAsync(@Header("Authorization") String auth);

    @GET("api/getPaymentData?gateway=braintree&currency=USD")
    Call<PaymentDataResponseDto> getPaymentDataAsync();

    @GET("api/confirmPayment?gateway=braintree&currency=USD")
    Call<PaymentResultResponseDto> confirmPaymentAsync(
            @Query("station") String station,
            @Query("orderHash") String orderHash,
            @Query("amount") int amount,
            @Query("paymentId") String nonce
    );

}
