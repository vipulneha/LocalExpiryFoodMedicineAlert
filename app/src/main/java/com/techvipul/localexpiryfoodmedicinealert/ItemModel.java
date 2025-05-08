package com.techvipul.localexpiryfoodmedicinealert;

public class ItemModel {
    private int id;
    private String name;
    private String expiryDate;
    private String description;
    private String imageUri;

    public ItemModel(int id, String name, String expiryDate, String description, String imageUri) {
        this.id = id;
        this.name = name;
        this.expiryDate = expiryDate;
        this.description = description;
        this.imageUri = imageUri;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}