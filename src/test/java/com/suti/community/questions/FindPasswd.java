package com.suti.community.questions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
小王有一个银行卡密码(存在字符串中)，只包含数字和大写字母，现知道密码的规则如下
1.必须是一串连续的数字
2.如果数字之间有”BAC“字符串的话，可以消除掉
3.数字可能会很大
现需要从字符串中找到密码，找到的话输出符合规则的最大的数，如果字符串中没有数字的话返回 1.。
 */
public class FindPasswd {
    public static void main(String[] args) {
        String str ="12479BAC39BAC2990";
        System.out.println(findPasswd(str));
        String str2= "AFOJ792BAC3420";
        System.out.println(findPasswd(str2));
        String str3= "DA234BAC2FI380BAC2DFW";
        System.out.println(findPasswd(str3));
    }

    public static long findPasswd(String str){
        String regex = "BAC";
        String strnew = str.replaceAll(regex,"");
        //System.out.println(strnew);

        String regex1= "\\d+";
        Pattern pattern = Pattern.compile(regex1);
        Matcher matcher = pattern.matcher(strnew);

        Long max = 0L;
        while(matcher.find()){
            //System.out.println("ddf");
            String nums = matcher.group();
            //System.out.println(nums);
            Long res = Long.parseLong(nums);
            max = Math.max(max,res);
        }

        return max==0?-1:max;
    }
}
