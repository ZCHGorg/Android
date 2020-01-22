package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class BestSellOrderDto {

    @SerializedName("bestSellOrder")
    public OrdersDto.OrderDto BestSellOrder;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
