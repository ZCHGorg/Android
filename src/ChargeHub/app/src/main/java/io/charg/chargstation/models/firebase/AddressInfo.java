package io.charg.chargstation.models.firebase;

/**
 * Created by worker on 01.11.2017.
 */

public class AddressInfo {

    private Integer ID;
    private String Title;
    private String AddressLine1;
    private String Town;
    private String StateOrProvince;
    private String Postcode;
    private Integer CountryID;
    private Double Latitude;
    private Double Longitude;
    private Integer DistanceUnit;
    private String ContactTelephone1;
    private String RelatedURL;
    private String AddressLine2;
    private String AccessComments;
    private String ContactEmail;
    private String ContactTelephone2;

    public AddressInfo() {
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAddressLine1() {
        return AddressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        AddressLine1 = addressLine1;
    }

    public String getTown() {
        return Town;
    }

    public void setTown(String town) {
        Town = town;
    }

    public String getStateOrProvince() {
        return StateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        StateOrProvince = stateOrProvince;
    }

    public String getPostcode() {
        return Postcode;
    }

    public void setPostcode(String postcode) {
        Postcode = postcode;
    }

    public Integer getCountryID() {
        return CountryID;
    }

    public void setCountryID(Integer countryID) {
        CountryID = countryID;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Integer getDistanceUnit() {
        return DistanceUnit;
    }

    public void setDistanceUnit(Integer distanceUnit) {
        DistanceUnit = distanceUnit;
    }

    public String getContactTelephone1() {
        return ContactTelephone1;
    }

    public void setContactTelephone1(String contactTelephone1) {
        ContactTelephone1 = contactTelephone1;
    }

    public String getRelatedURL() {
        return RelatedURL;
    }

    public void setRelatedURL(String relatedURL) {
        RelatedURL = relatedURL;
    }

    public String getAddressLine2() {
        return AddressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        AddressLine2 = addressLine2;
    }

    public String getAccessComments() {
        return AccessComments;
    }

    public void setAccessComments(String accessComments) {
        AccessComments = accessComments;
    }

    public String getContactEmail() {
        return ContactEmail;
    }

    public void setContactEmail(String contactEmail) {
        ContactEmail = contactEmail;
    }

    public String getContactTelephone2() {
        return ContactTelephone2;
    }

    public void setContactTelephone2(String contactTelephone2) {
        ContactTelephone2 = contactTelephone2;
    }
}