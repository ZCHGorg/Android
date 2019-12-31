package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class OrdersDto {

    @SerializedName("buyOrders")
    public Map<String, OrderDto> BuyOrders;

    public class OrderDto {

        @SerializedName("give")
        public double Give;

        @SerializedName("get")
        public double Get;

        @SerializedName("rate")
        public double Rate;

        @SerializedName("expire")
        public int Expire;

        @SerializedName("hash")
        public String Hash;

        @SerializedName("seller")
        public String Seller;

    }

}
