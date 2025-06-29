package com.example.lossreductionsystemandroid;

public class Asset {
    public final int id;
    public final String name;
    public final String category;
    public final int quantity;
    public final double value;
    public final String imageUrl;
    public final String statusCode;
    public final String statusLabel;
    public final String statusColor;


    public Asset(int imageResId, String name, String category, int quantity, double value,  String imageUrl, String statusCode, String statusLabel, String statusColor){
        this.id = imageResId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.value = value;
        this.imageUrl = imageUrl;
        this.statusCode = statusCode;
        this.statusLabel = statusLabel;
        this.statusColor = statusColor;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getValue() {
        return value;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatusCode(){
        return statusCode;
    }

    public String getStatusLabel(){
        return statusLabel;
    }

    public String getStatusColor(){
        return statusColor;
    }
}
