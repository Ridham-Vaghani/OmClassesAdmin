package com.vd.omclasses;

public class Student {

    public Student() {
    }

    public Student(String id, String name, String gender, String standard, String number, String number1, String occupation, String address, String email, String password) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.standard = standard;
        this.number = number;
        this.number1 = number1;
        this.occupation = occupation;
        this.address = address;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getStandard() {
        return standard;
    }

    public String getNumber() {
        return number;
    }

    public String getNumber1() {
        return number1;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String id, name, gender, standard, number, number1, occupation, address, email, password;

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setNumber1(String number1) {
        this.number1 = number1;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
