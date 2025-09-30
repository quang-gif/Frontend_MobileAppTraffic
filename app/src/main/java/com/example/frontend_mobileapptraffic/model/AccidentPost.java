package com.example.frontend_mobileapptraffic.model;

import java.util.List;

public class AccidentPost {
    private Long idAcPost;
    private String content;
    private String location;
    private String username;
    private int likeTotal;
    private List<String> imageUrls;
    private String createdAt;

    private boolean likedByUser;

    public void setIdAcPost(Long idAcPost) {
        this.idAcPost = idAcPost;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLikeTotal(int likeTotal) {
        this.likeTotal = likeTotal;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Long getIdAcPost() {
        return idAcPost;
    }

    public String getContent() {
        return content;
    }

    public String getLocation() {
        return location;
    }

    public String getUsername() {
        return username;
    }

    public int getLikeTotal() {
        return likeTotal;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }

}
