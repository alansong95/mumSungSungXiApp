package com.wootae.mumsungsungxi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Created by Alan on 8/11/2018.
 */

public class Attendance {
    HashMap<String, StudentStatus> hashMap;
    String name;
    StudentStatus[] thisWeekStatus;

    public Attendance(String name, HashMap<String, StudentStatus> map) {
        hashMap = map;
        this.name = name;

        thisWeekStatus = new StudentStatus[6];
    }

    public Attendance(String name, HashMap<String, StudentStatus> map, LocalDateTime mon) {
        hashMap = map;
        this.name = name;

        thisWeekStatus = new StudentStatus[6];
    }

    public void add(String date, StudentStatus status) {
        hashMap.put(date, status);
    }

    public HashMap<String, StudentStatus> getMap() {
        return hashMap;
    }

    public String getName() {
        return name;
    }

    public void setThisWeekStatus(LocalDateTime mon) {
        String monStr = mon.format(DateTimeFormatter.ofPattern("yyyy--MM-dd"));

        thisWeekStatus[0] = hashMap.get(monStr);


    }


}
