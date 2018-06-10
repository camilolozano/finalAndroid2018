package com.lozano.camilo.shop.RecyclerApp;

public class Item_products {
    private int idP;
    private String nameP;
    private String descriptionP;
    private String priceP;
    private Integer stockP;
    private Integer qualificationP;
    private String UrlImg;
    private String latitud;
    private String longitud;

    public Item_products(int idP, String nameP, String descriptionP, String priceP, Integer stockP, Integer qualificationP, String urlImg, String latitud, String longitud) {
        this.idP = idP;
        this.nameP = nameP;
        this.descriptionP = descriptionP;
        this.priceP = priceP;
        this.stockP = stockP;
        this.qualificationP = qualificationP;
        UrlImg = urlImg;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getIdP() {
        return idP;
    }

    public String getNameP() {
        return nameP;
    }

    public String getDescriptionP() {
        return descriptionP;
    }

    public String getPriceP() {
        return priceP;
    }

    public Integer getStockP() {
        return stockP;
    }

    public Integer getQualificationP() {
        return qualificationP;
    }

    public String getUrlImg() {
        return UrlImg;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }
}