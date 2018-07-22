package io.charg.chargstation.services.remote.api.wecharg;

import com.google.gson.annotations.SerializedName;

public class CartItemResponseDto {

    @SerializedName("item_id")
    public int ItemId;

    @SerializedName("sku")
    public String Sku;

    @SerializedName("qty")
    public int Count;

    @SerializedName("name")
    public String Name;

    @SerializedName("price")
    public double Price;

    @SerializedName("product_type")
    public String ProductType;

    @SerializedName("quote_id")
    public String QuoteId;
}
