package com.example.namespace;
import android.os.Parcel;
import android.os.Parcelable;

public class MyDataModel implements Parcelable {
    private String firstName;
    private String lastName;
    private String passportNumber;
    private String product;
    private String status;

    // Constructors
    public MyDataModel() {
        // Default constructor required for Firestore
    }

    public MyDataModel(String firstName, String lastName, String passportNumber, String product, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNumber = passportNumber;
        this.product = product;
        this.status = status;
    }

    // Getters and Setters
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

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Parcelable implementation
    protected MyDataModel(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        passportNumber = in.readString();
        product = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(passportNumber);
        dest.writeString(product);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyDataModel> CREATOR = new Creator<MyDataModel>() {
        @Override
        public MyDataModel createFromParcel(Parcel in) {
            return new MyDataModel(in);
        }

        @Override
        public MyDataModel[] newArray(int size) {
            return new MyDataModel[size];
        }
    };
}
