package io.charg.chargstation.models.firebase;

/**
 * Created by worker on 02.11.2017.
 */

public class User {

    private Integer ID;
    private String ProfileImageURL;
    private Integer ReputationPoints;
    private String Username;

    public User() {
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getProfileImageURL() {
        return ProfileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        ProfileImageURL = profileImageURL;
    }

    public Integer getReputationPoints() {
        return ReputationPoints;
    }

    public void setReputationPoints(Integer reputationPoints) {
        ReputationPoints = reputationPoints;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
