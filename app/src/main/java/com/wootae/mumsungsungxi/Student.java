package com.wootae.mumsungsungxi;

import java.util.UUID;

/**
 * Created by Alan on 8/7/2018.
 */

public class Student {
    private static int numOfStudents = 0;

    private String uid;
    private String name;
//    private String email;
    private String phoneNumber;
    private String section;
//    private String mProfileImageUrl;

    public Student(String name, String phoneNumber, String section) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.section = section;
        this.uid = UUID.randomUUID().toString();

        numOfStudents++;
    }

    // getters & setters
    public void setName(String name) {
        this.name = name;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setSection(String section) {
        this.section = section;
    }

    public String getName() {
        return name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getSection() {
        return section;
    }
    public String getUid() {
        return uid;
    }

    public static int getNumOfStudents() {
        return numOfStudents;
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    // Debug
    @Override
    public String toString() {
        return "UID: " + uid + "Name: " + name + ", " + "Phone Number: " + phoneNumber + ", " + "Section: " + section;
    }

}
