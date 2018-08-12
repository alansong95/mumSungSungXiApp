package com.wootae.mumsungsungxi;

import android.util.Log;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Created by Alan on 8/11/2018.
 */

public class Attendance {
    HashMap<String, String> hashMap;
    String name;
    String[] thisWeekStatus;
    String[] thisMonthStatus;
    LocalDate thisMonthDates;

    public Attendance(String name, HashMap<String, String> map) {
        hashMap = map;
        this.name = name;

        thisWeekStatus = new String[6];
    }

    public Attendance(String name, HashMap<String, String> map, LocalDate mon) {
        hashMap = map;
        this.name = name;

        thisWeekStatus = new String[6];
        setThisWeekStatus(mon);
    }

    public void add(String date, String status) {
        hashMap.put(date, status);
    }

    public HashMap<String, String> getMap() {
        return hashMap;
    }

    public String getName() {
        return name;
    }

    public void setThisWeekStatus(LocalDate mon) {
        String monStr = mon.format(DateTimeFormatter.ofPattern("yyyy--MM-dd"));

        thisWeekStatus[0] = hashMap.get(monStr);
        thisWeekStatus[1] = hashMap.get(mon.with(DayOfWeek.TUESDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        thisWeekStatus[2] = hashMap.get(mon.with(DayOfWeek.WEDNESDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        thisWeekStatus[3] = hashMap.get(mon.with(DayOfWeek.THURSDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        thisWeekStatus[4] = hashMap.get(mon.with(DayOfWeek.FRIDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        thisWeekStatus[5] = hashMap.get(mon.with(DayOfWeek.SATURDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
    }

    public String[] getThisWeekStatus() {
        return thisWeekStatus;
    }

    public void setThisMonthStatus(LocalDate mon) {
        LocalDate fristDay = mon.withDayOfMonth(1);
    }



}
