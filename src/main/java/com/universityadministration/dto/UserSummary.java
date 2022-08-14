package com.universityadministration.dto;


import lombok.Data;


@Data
public class UserSummary {
    private int userId;
    private String email;

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}