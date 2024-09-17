package com.android.health.data;

public class Patient {

    private String name,phoneNumber,age,gender,password;

    public Patient(){}

    public Patient(String names,String number,String Age,String sex,String pass){
        name = names;
        phoneNumber = number;
        age = Age;
        gender = sex;
        password = pass;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getPassword() {
        return password;
    }
}
