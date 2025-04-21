package com.jh.education.course.info.util;

import java.util.HashMap;
import java.util.Map;

public class CourseUtil {

    private CourseUtil() {}

    private static Map<String, Integer> gradeMap = new HashMap<>(12);

    static {
        gradeMap.put("一年级", 1);
        gradeMap.put("二年级", 2);
        gradeMap.put("三年级", 3);
        gradeMap.put("四年级", 4);
        gradeMap.put("五年级", 5);
        gradeMap.put("六年级", 6);
        gradeMap.put("初一", 7);
        gradeMap.put("初二", 8);
        gradeMap.put("初三", 9);
        gradeMap.put("高一", 10);
        gradeMap.put("高二", 11);
        gradeMap.put("高三", 12);
    }

    public static Integer getGrade(String grade) {
        return gradeMap.get(grade);
    }
}
