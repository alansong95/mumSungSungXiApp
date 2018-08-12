package com.wootae.mumsungsungxi;

import java.time.DayOfWeek;
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
        setThisWeekStatus(mon);
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
        thisWeekStatus[1] = hashMap.get(mon.with(DayOfWeek.TUESDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        thisWeekStatus[2] = hashMap.get(mon.with(DayOfWeek.WEDNESDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        thisWeekStatus[3] = hashMap.get(mon.with(DayOfWeek.THURSDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        thisWeekStatus[4] = hashMap.get(mon.with(DayOfWeek.FRIDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
//        thisWeekStatus[5] = hashMap.get(mon.with(DayOfWeek.SATURDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
    }

    public StudentStatus[] getThisWeekStatus() {
        return thisWeekStatus;
    }



}
