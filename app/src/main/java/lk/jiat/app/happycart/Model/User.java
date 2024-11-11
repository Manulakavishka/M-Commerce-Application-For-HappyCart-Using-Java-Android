package lk.jiat.app.happycart.Model;

public class User {
    private String firstName;
    private String lastName;
    private String gender;
    private String image;

    public User(String firstName, String lastName, String gender, String image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.image = image;
    }

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
