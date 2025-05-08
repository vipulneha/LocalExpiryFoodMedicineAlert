package com.techvipul.localexpiryfoodmedicinealert;

public class DeletedItemModel {
    private int id;
    private String name;
    private String expiryDate;
    private String description;
    private String imageUri;
    private String deletedTimestamp;

    public DeletedItemModel(int id, String name, String expiryDate, String description, String imageUri, String deletedTimestamp) {
        this.id = id;
        this.name = name;
        this.expiryDate = expiryDate;
        this.description = description;
        this.imageUri = imageUri;
        this.deletedTimestamp = deletedTimestamp;
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

    public String getDeletedTimestamp() {
        return deletedTimestamp;
    }
}