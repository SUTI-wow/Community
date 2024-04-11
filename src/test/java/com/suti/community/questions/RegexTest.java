package com.suti.community.questions;

public class RegexTest {

    public static void main(String[] args) {
        String regex = "1[38]\\d{9}";
        String number = "13138574081";
        String number2 = "16989638023";
        System.out.println(isValid(number));
        System.out.println(isValid(number2));

        String str = "sodfh2380bv";
        System.out.println(replace(str));
    }
    public static boolean isValid(String number){
        String regex="1[38]\\d{9}";
        return number.matches(regex);
    }
    public static String replace(String str){
        String regex= "\\d";
        String replace = "*";
        return str.replaceAll(regex,replace);
    }
}
