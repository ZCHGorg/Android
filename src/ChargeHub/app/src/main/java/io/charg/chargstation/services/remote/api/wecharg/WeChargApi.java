package io.charg.chargstation.services.remote.api.wecharg;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeChargApi {

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
