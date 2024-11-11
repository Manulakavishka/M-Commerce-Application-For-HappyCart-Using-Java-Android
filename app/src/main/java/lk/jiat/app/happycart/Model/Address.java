package lk.jiat.app.happycart.Model;

public class Address {
    private String id;
    private String fullName;
    private String mobileNumber;
    private String province;
    private String district;
    private String area;
    private String address;
    private String landMark;
    private String addressType;

    public Address(String id,String fullName, String mobileNumber, String province, String district, String area, String address, String landMark, String addressType) {
        this.id = id;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.province = province;
        this.district = district;
        this.area = area;
        this.address = address;
        this.landMark = landMark;
        this.addressType = addressType;
    }
    public Address(String fullName, String mobileNumber, String province, String district, String area, String address, String landMark, String addressType) {
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.province = province;
        this.district = district;
        this.area = area;
        this.address = address;
        this.landMark = landMark;
        this.addressType = addressType;
    }

    public Address() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandMark() {
        return landMark;
    }

    public void setLandMark(String landMark) {
        this.landMark = landMark;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
}
