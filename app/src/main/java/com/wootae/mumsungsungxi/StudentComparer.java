package com.wootae.mumsungsungxi;

import java.util.Comparator;

/**
 * Created by Alan on 8/16/2018.
 */

public class StudentComparer implements Comparator<Student> {
    @Override
    public int compare(Student s1, Student s2) {
        return s1.getName().compareTo(s2.getName());
    }
}
