package com.th.swatch.treasurehunt.Models;

import java.util.List;

/**
 * Created by swatch on 01.06.2017.
 */

public class UserModel {
    private String username;
    private String emailAddress;
    private String role;

    private List<ChallengeModel> challenges;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<ChallengeModel> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<ChallengeModel> challenges) {
        this.challenges = challenges;
    }
}
