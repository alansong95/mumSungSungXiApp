package com.wootae.mumsungsungxi;

import android.util.Log;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/**
 * Created by Alan on 8/11/2018.
 */

public class Attendance {
    HashMap<String, String> hashMap;
    String name;
    String[] thisWeekStatus;
    String[] thisMonthStatus;

    static LocalDate today = LocalDate.now();
    static LocalDate firstDay = today.withDayOfMonth(1);
    static LocalDate lastDay = today.with(lastDayOfMonth());

    static LocalDate[] thisMonthDates = {
        firstDay,
            firstDay.plusDays(1),
            firstDay.plusDays(2),
            firstDay.plusDays(3),
            firstDay.plusDays(4),
            firstDay.plusDays(5),
            firstDay.plusDays(6),
            firstDay.plusDays(7),
            firstDay.plusDays(8),
            firstDay.plusDays(9),
            firstDay.plusDays(10),
            firstDay.plusDays(11),
            firstDay.plusDays(12),
            firstDay.plusDays(13),
            firstDay.plusDays(14),
            firstDay.plusDays(15),
            firstDay.plusDays(16),
            firstDay.plusDays(17),
            firstDay.plusDays(18),
            firstDay.plusDays(19),
            firstDay.plusDays(20),
            firstDay.plusDays(21),
            firstDay.plusDays(22),
            firstDay.plusDays(23),
            firstDay.plusDays(24),
            firstDay.plusDays(25),
            firstDay.plusDays(26),
            firstDay.plusDays(27),
            firstDay.plusDays(28),
            firstDay.plusDays(29),
            firstDay.plusDays(30),
    };

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

        thisMonthStatus = new String[lastDay.getDayOfMonth()];
        setThisMonthStatus();

        Log.d("TESTING128", getName());
        for (String s : thisMonthStatus) {
            Log.d("TESTING128", "status: " + s);
        }
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
//        String monStr = mon.format(DateTimeFormatter.ofPattern("yyyy--MM-dd"));

//        thisWeekStatus[0] = hashMap.get(monStr);
//        thisWeekStatus[1] = hashMap.get(mon.with(DayOfWeek.TUESDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
//        thisWeekStatus[2] = hashMap.get(mon.with(DayOfWeek.WEDNESDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
//        thisWeekStatus[3] = hashMap.get(mon.with(DayOfWeek.THURSDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
//        thisWeekStatus[4] = hashMap.get(mon.with(DayOfWeek.FRIDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
//        thisWeekStatus[5] = hashMap.get(mon.with(DayOfWeek.SATURDAY).format((DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        thisWeekStatus[0] = hashMap.get(mon.with(DayOfWeek.MONDAY).toString());
        thisWeekStatus[1] = hashMap.get(mon.with(DayOfWeek.TUESDAY).toString());
        thisWeekStatus[2] = hashMap.get(mon.with(DayOfWeek.WEDNESDAY).toString());
        thisWeekStatus[3] = hashMap.get(mon.with(DayOfWeek.THURSDAY).toString());
        thisWeekStatus[4] = hashMap.get(mon.with(DayOfWeek.FRIDAY).toString());
        thisWeekStatus[5] = hashMap.get(mon.with(DayOfWeek.SATURDAY).toString());

    }

    public String[] getThisWeekStatus() {
        return thisWeekStatus;
    }

    public void setThisMonthStatus() {
        for (int i = 0; i < lastDay.getDayOfMonth(); i++) {
            thisMonthStatus[i] = hashMap.get(firstDay.plusDays(i).toString());
        }


    }



}
