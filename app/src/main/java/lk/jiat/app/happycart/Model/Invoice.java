package lk.jiat.app.happycart.Model;

import java.io.Serializable;

public class Invoice implements Serializable {
    private String id;
    private String addressId;
    private String addressLocation;
    private String catagoryDocId;
    private String productDocId;
    private String date;
    private String price;
    private String qty;
    private String status;

    public Invoice() {
    }

    public Invoice(String addressId, String catagoryDocId, String productDocId, String date, String price, String qty, String status) {
        this.addressId = addressId;
        this.catagoryDocId = catagoryDocId;
        this.productDocId = productDocId;
        this.date = date;
        this.price = price;
        this.qty = qty;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(String addressLocation) {
        this.addressLocation = addressLocation;
    }

    public String getCatagoryDocId() {
        return catagoryDocId;
    }

    public void setCatagoryDocId(String catagoryDocId) {
        this.catagoryDocId = catagoryDocId;
    }

    public String getProductDocId() {
        return productDocId;
    }

    public void setProductDocId(String productDocId) {
        this.productDocId = productDocId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
