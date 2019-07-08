package com.moory.myweightgoal.modules;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String name;
    private String age;
    private String email;
    private String gender;
    private String urlToImage;
    private float height;
    private float weight;
    private float bmi;

    public User() {
    }

    public User(String name, String age, String email, String gender) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.gender = gender;
    }

    public User(String name, String age, String email, String gender, String urlToImage, float height, float weight, float bmi) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.gender = gender;
        this.urlToImage = urlToImage;
        this.height = height;
        this.weight = weight;
        this.bmi = bmi;
    }

    protected User(Parcel in) {
        name = in.readString();
        age = in.readString();
        email = in.readString();
        gender = in.readString();
        urlToImage = in.readString();
        height = in.readFloat();
        weight = in.readFloat();
        bmi = in.readFloat();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(age);
        dest.writeString(email);
        dest.writeString(gender);
        dest.writeString(urlToImage);
        dest.writeFloat(height);
        dest.writeFloat(weight);
        dest.writeFloat(bmi);
    }
}
