package io.charg.chargstation.models.firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by worker on 01.11.2017.
 */

public class ChargeStation {

    private Integer ID;
    private String UUID;
    private Integer DataProviderID;
    private Integer OperatorID;
    private String OperatorsReference;
    private Integer UsageTypeID;
    private String UsageCost;
    private AddressInfo AddressInfo;
    private Integer NumberOfPoints;
    private Integer StatusTypeID;
    private String DateLastStatusUpdate;
    private Integer DataQualityLevel;
    private String DateCreated;
    private Integer SubmissionStatusTypeID;
    private List<Connection> Connections;
    private Boolean IsRecentlyVerified;
    private String DateLastVerified;
    private String GeneralComments;
    private List<MediaItem> MediaItems = new ArrayList<>();
    private List<Comment> UserComments;
    private double CostCharge;
    private double CostPark;
    private String EthAddress;

    public ChargeStation() {
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public Integer getDataProviderID() {
        return DataProviderID;
    }

    public void setDataProviderID(Integer dataProviderID) {
        DataProviderID = dataProviderID;
    }

    public Integer getOperatorID() {
        return OperatorID;
    }

    public void setOperatorID(Integer operatorID) {
        OperatorID = operatorID;
    }

    public String getOperatorsReference() {
        return OperatorsReference;
    }

    public void setOperatorsReference(String operatorsReference) {
        OperatorsReference = operatorsReference;
    }

    public Integer getUsageTypeID() {
        return UsageTypeID;
    }

    public void setUsageTypeID(Integer usageTypeID) {
        UsageTypeID = usageTypeID;
    }

    public String getUsageCost() {
        return UsageCost;
    }

    public void setUsageCost(String usageCost) {
        UsageCost = usageCost;
    }

    public io.charg.chargstation.models.firebase.AddressInfo getAddressInfo() {
        return AddressInfo;
    }

    public void setAddressInfo(io.charg.chargstation.models.firebase.AddressInfo addressInfo) {
        AddressInfo = addressInfo;
    }

    public Integer getNumberOfPoints() {
        return NumberOfPoints;
    }

    public void setNumberOfPoints(Integer numberOfPoints) {
        NumberOfPoints = numberOfPoints;
    }

    public Integer getStatusTypeID() {
        return StatusTypeID;
    }

    public void setStatusTypeID(Integer statusTypeID) {
        StatusTypeID = statusTypeID;
    }

    public String getDateLastStatusUpdate() {
        return DateLastStatusUpdate;
    }

    public void setDateLastStatusUpdate(String dateLastStatusUpdate) {
        DateLastStatusUpdate = dateLastStatusUpdate;
    }

    public Integer getDataQualityLevel() {
        return DataQualityLevel;
    }

    public void setDataQualityLevel(Integer dataQualityLevel) {
        DataQualityLevel = dataQualityLevel;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }

    public Integer getSubmissionStatusTypeID() {
        return SubmissionStatusTypeID;
    }

    public void setSubmissionStatusTypeID(Integer submissionStatusTypeID) {
        SubmissionStatusTypeID = submissionStatusTypeID;
    }

    public List<Connection> getConnections() {
        return Connections;
    }

    public void setConnections(List<Connection> connections) {
        Connections = connections;
    }

    public Boolean getIsRecentlyVerified() {
        return IsRecentlyVerified;
    }

    public void setIsRecentlyVerified(Boolean recentlyVerified) {
        IsRecentlyVerified = recentlyVerified;
    }

    public String getDateLastVerified() {
        return DateLastVerified;
    }

    public void setDateLastVerified(String dateLastVerified) {
        DateLastVerified = dateLastVerified;
    }

    public String getGeneralComments() {
        return GeneralComments;
    }

    public void setGeneralComments(String generalComments) {
        GeneralComments = generalComments;
    }

    public List<MediaItem> getMediaItems() {
        return MediaItems;
    }

    public void setMediaItems(List<MediaItem> mediaItems) {
        MediaItems = mediaItems;
    }

    public List<Comment> getUserComments() {
        return UserComments;
    }

    public void setUserComments(List<Comment> userComments) {
        UserComments = userComments;
    }

    public double getCostCharge() {
        return CostCharge;
    }

    public void setCostCharge(double costCharge) {
        CostCharge = costCharge;
    }

    public double getCostPark() {
        return CostPark;
    }

    public void setCostPark(double costPark) {
        CostPark = costPark;
    }

    public String getEthAddress() {
        return EthAddress;
    }

    public void setEthAddress(String ethAddress) {
        EthAddress = ethAddress;
    }
}
