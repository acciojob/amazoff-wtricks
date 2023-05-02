package com.driver;

import java.util.Arrays;
import java.util.List;

public class TimeUtils {
    public static int convertTime(String deliveryTime) {
        List<String> list = Arrays.asList(deliveryTime.split(":")); //String[] -> List<String>
        int HH = Integer.parseInt(list.get(0)); //String "123" -> int 123
        int MM = Integer.parseInt(list.get(1));
        return HH * 60 + MM;
    }

    public static String convertTime(int deliveryTime) {
        //565 in int -> 09:25
        int HH = deliveryTime/60;
        int MM = deliveryTime%60;
        String hh = String.valueOf(HH); //9 //11
        String mm = String.valueOf(MM); //25 //2

        if(hh.length() == 1) {
            hh = '0' + hh;
        }
        if(mm.length() == 1) {
            mm = '0' +mm;
        }

        return hh + ":" + mm;
    }
}
