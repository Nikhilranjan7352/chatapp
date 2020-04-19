package com.example.chatapp;

public class Model {
    private String name,satus,image,userId;
    public Model()
    {

    }

    public Model(String name, String satus, String image) {
        this.name = name;
        this.satus = satus;
        this.image = image;
        this.userId=userId;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSatus() {
        return satus;
    }

    public void setSatus(String satus) {
        this.satus = satus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
