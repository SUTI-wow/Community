package com.suti.community.questions;

/*
1.开发网站时，对于密码有一些规则
1.密码长度至少为 10
2.只能包含大小写字母 和 数字
3.至少出现 大写字母、小写字母、数字 这3种类型里的 2 种给定一个密码 mypassword，判断密码是否符合规则?竹口返回 true，否则返回 false。
 */

public class PassWordVerfication {
    public static void main(String[] args) {
        String pw1 = "12gfw9";
        String pw2 = "djADI0079Djo!diwo";
        String pw3 = "ACSSF108E98392DDFLS";
        String pw4 = "jsoihofjsi";
        String pw5 ="jdoahfHIFHh134";
        String pw6 = "12490320284";
        String pw7 ="DSUHFOIWFEFWF";
        boolean res1 = passwordVerfication(pw1);
        System.out.println(res1);
        boolean res2 = passwordVerfication(pw2);
        System.out.println(res2);
        boolean res3 = passwordVerfication(pw3);
        System.out.println(res3);
        boolean res4 = passwordVerfication(pw4);
        System.out.println(res4);
        boolean res5 = passwordVerfication(pw5);
        System.out.println(res5);
        boolean res6 = passwordVerfication(pw6);
        System.out.println(res6);
        boolean res7 = passwordVerfication(pw7);
        System.out.println(res7);
    }

    public static boolean passwordVerfication(String str){
        //if(str.length()<10)
        //    return false;
        //int flag1 = 0,flag2=0,flag3=0;
        //for(char c : str.toCharArray()){
        //    if(!((c<='z'&&c>='a') || (c<='Z' && c>='A') || Character.isDigit(c))){
        //        return false;
        //    }
        //    else if(c<='z'&&c>='a'){
        //        flag1 = 1;
        //    }
        //    else if(c<='Z' && c>='A'){
        //        flag2=1;
        //    }
        //    else if(Character.isDigit(c)){
        //        flag3=1;
        //    }
        //}
        //if(flag1+flag2+flag3<2){
        //    return false;
        //}
        //return true;

        String regex1 = "^(?=(.+[a-z]))(?=(.+[A-Z]))[a-zA-Z\\d]{10,}$";
        String regex2 = "^(?=(.+[a-z]))(?=(.+\\d))[a-zA-Z\\d]{10,}$";
        String regex3 = "^(?=(.+[A-Z]))(?=(.+\\d))[a-zA-Z\\d]{10,}$";
        return str.matches(regex1) || str.matches(regex2)||str.matches(regex3);
    }
}
