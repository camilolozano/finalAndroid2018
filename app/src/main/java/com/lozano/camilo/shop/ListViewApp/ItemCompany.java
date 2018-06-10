package com.lozano.camilo.shop.ListViewApp;

public class ItemCompany {
    Integer idDocument;
    int idComp;
    Integer idMaster;
    String NameComp;
    String urimgComp;
    String descriptionComp;
    String SearchInfo;
    boolean estatusimgCheck;
    String latitud;
    String longitud;

    public ItemCompany(Integer idDocument, int idComp, Integer idMaster, String nameComp, String urimgComp, String descriptionComp, String searchInfo, boolean estatusimgCheck, String latitud, String longitud) {
        this.idDocument = idDocument;
        this.idComp = idComp;
        this.idMaster = idMaster;
        NameComp = nameComp;
        this.urimgComp = urimgComp;
        this.descriptionComp = descriptionComp;
        SearchInfo = searchInfo;
        this.estatusimgCheck = estatusimgCheck;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Integer getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(Integer idDocument) {
        this.idDocument = idDocument;
    }

    public int getIdComp() {
        return idComp;
    }

    public void setIdComp(int idComp) {
        this.idComp = idComp;
    }

    public Integer getIdMaster() {
        return idMaster;
    }

    public void setIdMaster(Integer idMaster) {
        this.idMaster = idMaster;
    }

    public String getNameComp() {
        return NameComp;
    }

    public void setNameComp(String nameComp) {
        NameComp = nameComp;
    }

    public String getUrimgComp() {
        return urimgComp;
    }

    public void setUrimgComp(String urimgComp) {
        this.urimgComp = urimgComp;
    }

    public String getDescriptionComp() {
        return descriptionComp;
    }

    public void setDescriptionComp(String descriptionComp) {
        this.descriptionComp = descriptionComp;
    }

    public String getSearchInfo() {
        return SearchInfo;
    }

    public void setSearchInfo(String searchInfo) {
        SearchInfo = searchInfo;
    }

    public boolean isEstatusimgCheck() {
        return estatusimgCheck;
    }

    public void setEstatusimgCheck(boolean estatusimgCheck) {
        this.estatusimgCheck = estatusimgCheck;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}