package io.charg.chargstation.services.remote.api.wecharg;

import com.google.gson.annotations.SerializedName;

public class CartItemRequestDto {

    public CartItemRequestDto(String quoteId) {
        this.CartItem = new CartItemDto();
        this.CartItem.Count = 1;
        this.CartItem.Sku = "0xa3Be8Cc84B76f3162a14690A90d00b7704288e91";
        this.CartItem.QuoteId = quoteId;
    }

    @SerializedName("cartItem")
    public CartItemDto CartItem;

    public class CartItemDto {

        @SerializedName("sku")
        public String Sku;

        @SerializedName("qty")
        public Integer Count;

        @SerializedName("quote_id")
        public String QuoteId;

    }
}
