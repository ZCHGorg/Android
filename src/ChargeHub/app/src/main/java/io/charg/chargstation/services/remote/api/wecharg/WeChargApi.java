package io.charg.chargstation.services.remote.api.wecharg;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

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
    Call<List<Object>> getBrainTreeTokenAsync(@Header("Authorization") String auth);
}
