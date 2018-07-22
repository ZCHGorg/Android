package io.charg.chargstation.services.remote.api.wecharg;

import com.google.gson.annotations.SerializedName;

public class PaymentInformationDto {

    @SerializedName("quote_id")
    public String QuoteId;

    @SerializedName("payment_method")
    public PaymentMethodDto PaymentMethod;

    @SerializedName("billing_address")
    public BillingAddressDto BillingAddress;

    @SerializedName("email")
    public String Email;

    public PaymentInformationDto(String quoteId) {
        QuoteId = quoteId;
        PaymentMethod = new PaymentMethodDto();
        BillingAddress = new BillingAddressDto();
        Email = "jdoe@example.com";
    }

    public class PaymentMethodDto {

        public PaymentMethodDto() {
            this.AdditionalData = new AdditionalDataDto();
        }

        @SerializedName("method")
        public String Method;

        @SerializedName("additional_data")
        public AdditionalDataDto AdditionalData;

        public class AdditionalDataDto {

            @SerializedName("payment_method_nonce")
            public String Nonce;
        }
    }

    public class BillingAddressDto {

        public BillingAddressDto() {
            Email = "user@user.com";
            Region = "g";
            RegionId = 43;
            RegionCode = "NY";
            CountryId = "US";
            Street = new String[]{"Street"};
            PostCode = "123";
            City = "London";
            Telephone = "123-33";
            FirstName = "User";
            LastName = "User";
        }

        @SerializedName("email")
        public String Email;

        @SerializedName("region")
        public String Region;

        @SerializedName("region_id")
        public int RegionId;

        @SerializedName("region_code")
        public String RegionCode;

        @SerializedName("country_id")
        public String CountryId;

        @SerializedName("street")
        public String[] Street;

        @SerializedName("postcode")
        public String PostCode;

        @SerializedName("city")
        public String City;

        @SerializedName("telephone")
        public String Telephone;

        @SerializedName("firstname")
        public String FirstName;

        @SerializedName("lastname")
        public String LastName;

    }
}
