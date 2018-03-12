package io.charg.chargstation.models.firebase;

/**
 * Created by worker on 02.11.2017.
 */

public class MediaItem {

    private Integer ChargePointID;
    private String Comment;
    private String DateCreated;
    private Integer ID;
    private Boolean IsEnabled;
    private Boolean IsExternalResource;
    private Boolean IsFeaturedItem;
    private Boolean IsVideo;
    private String ItemThumbnailURL;
    private String ItemURL;
    private User User;

    public MediaItem() {
    }

    public Integer getChargePointID() {
        return ChargePointID;
    }

    public void setChargePointID(Integer chargePointID) {
        ChargePointID = chargePointID;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Boolean getIsEnabled() {
        return IsEnabled;
    }

    public void setIsEnabled(Boolean enabled) {
        IsEnabled = enabled;
    }

    public Boolean getIsExternalResource() {
        return IsExternalResource;
    }

    public void setIsExternalResource(Boolean externalResource) {
        IsExternalResource = externalResource;
    }

    public Boolean getIsFeaturedItem() {
        return IsFeaturedItem;
    }

    public void setIsFeaturedItem(Boolean featuredItem) {
        IsFeaturedItem = featuredItem;
    }

    public Boolean getIsVideo() {
        return IsVideo;
    }

    public void setIsVideo(Boolean video) {
        IsVideo = video;
    }

    public String getItemThumbnailURL() {
        return ItemThumbnailURL;
    }

    public void setItemThumbnailURL(String itemThumbnailURL) {
        ItemThumbnailURL = itemThumbnailURL;
    }

    public String getItemURL() {
        return ItemURL;
    }

    public void setItemURL(String itemURL) {
        ItemURL = itemURL;
    }

    public io.charg.chargstation.models.firebase.User getUser() {
        return User;
    }

    public void setUser(io.charg.chargstation.models.firebase.User user) {
        User = user;
    }
}
