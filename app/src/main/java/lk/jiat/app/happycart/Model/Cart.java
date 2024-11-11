package lk.jiat.app.happycart.Model;

public class Cart {
    private String userId;
    private String catagoryDocId;
    private String productDocId;
    private String price;
    private String name;
    private String image;

    public Cart(String userId, String catagoryDocId, String productDocId, String price, String name, String image) {
        this.userId = userId;
        this.catagoryDocId = catagoryDocId;
        this.productDocId = productDocId;
        this.price = price;
        this.name = name;
        this.image = image;
    }

    public Cart() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
