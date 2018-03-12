package io.charg.chargstation.models.firebase;

/**
 * Created by worker on 02.11.2017.
 */

public class Comment {

    private Integer ChargePointID;
    private Integer CheckinStatusTypeID;
    private String Comment;
    private Integer CommentTypeID;
    private String DateCreated;
    private Integer ID;
    private String RelatedURL;
    private User User;
    private String UserName;

    public Comment() {
    }

    public Integer getChargePointID() {
        return ChargePointID;
    }

    public void setChargePointID(Integer chargePointID) {
        ChargePointID = chargePointID;
    }

    public Integer getCheckinStatusTypeID() {
        return CheckinStatusTypeID;
    }

    public void setCheckinStatusTypeID(Integer checkinStatusTypeID) {
        CheckinStatusTypeID = checkinStatusTypeID;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public Integer getCommentTypeID() {
        return CommentTypeID;
    }

    public void setCommentTypeID(Integer commentTypeID) {
        CommentTypeID = commentTypeID;
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

    public String getRelatedURL() {
        return RelatedURL;
    }

    public void setRelatedURL(String relatedURL) {
        RelatedURL = relatedURL;
    }

    public io.charg.chargstation.models.firebase.User getUser() {
        return User;
    }

    public void setUser(io.charg.chargstation.models.firebase.User user) {
        User = user;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
