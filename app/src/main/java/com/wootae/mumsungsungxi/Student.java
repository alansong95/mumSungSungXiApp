package com.wootae.mumsungsungxi;

import android.net.Uri;

import java.util.UUID;

/**
 * Created by Alan on 8/7/2018.
 */

public class Student {
//    private static int numOfStudents = 0;

    private String uid;
    private String name;
//    private String email;
    private String phoneNumber;
    private String section;
//    private String mProfileImageUrl;
    private String pictureUri;
    private String updatedDate;
    private StudentStatus status;

    public Student() {

    }

    public Student(String name, String phoneNumber, String section) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.section = section;
        this.uid = UUID.randomUUID().toString();
        this.pictureUri = "";
        this.updatedDate = "";
        this.status = StudentStatus.NONE;
//        numOfStudents++;
    }

    public Student(String name, String phoneNumber, String section, String pictureUri) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.section = section;
        this.uid = UUID.randomUUID().toString();
        this.pictureUri = pictureUri;
        this.updatedDate = "";
        this.status = StudentStatus.NONE;
//        numOfStudents++;
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
    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }
    public void setStatus(StudentStatus status) { this.status = status; }

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
    public String getPictureUri() { return pictureUri; };
    public String getUpdatedDate() { return updatedDate; }
    public StudentStatus getStatus() { return status; }

//    public static int getNumOfStudents() {
//        return numOfStudents;
//    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Student)) {
            return false;
        }

        Student s = (Student) obj;

        if (this.uid.equals(s.getUid())) {
            return true;
        } else {
            return false;
        }
    }

    // Debug
    @Override
    public String toString() {
        return "UID: " + uid + "Name: " + name + ", " + "Phone Number: " + phoneNumber + ", " + "Section: " + section + "pictureUrl: " + pictureUri +  "updated date: " + updatedDate;
    }

}
