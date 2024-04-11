package com.suti.community.questions;

import java.util.ArrayList;
import java.util.List;

/*
在数据库中，为了方便存储ip 地址，一般会把ip 地址转化为一个十进制数字进行存储。现需要把一个十进制的数字变回为ip 地址的形式，规则如下:
1.首先把十进制的数字变成十六进制
2.每 2 位十六进制为一段，将其变成十进制，再加上点'，填入到ip 结果中。
现对于一个数字，需要输出其对应的ip 地址字符串。如果ip地址非法，则输出“invalid"。
 */
public class IPstore {
    public static void main(String[] args) {
        long ip1 = 4311744512L;
        System.out.println(getIP(ip1));

        long ip2 = 2130706433L;
        System.out.println(getIP(ip2));
    }

    public static String getIP(long num){
        String hexString = Long.toHexString(num);
        if(hexString.length()>8){
            return "invalid";
        }
        else if(hexString.length()<8){
            hexString = "0"+hexString;
        }
        StringBuilder ipAddress = new StringBuilder();
        for(int i=0;i<8;i+=2){
            String ip = hexString.substring(i,i+2);
            if(ip==null)  return "invalid";
            int ipNum = Integer.parseInt(hexString.substring(i,i+2),16);
            if(ipNum<0 || ipNum>255){
                return "invalid";
            }
            ipAddress.append(ipNum);
            if(i!=6){
                ipAddress.append(".");
            }
        }
        return ipAddress.toString();
    }


}
