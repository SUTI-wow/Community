package com.suti.community.java;

public class Hungry {
    private Hungry(){};

    private final static Hungry HUNGRY = new Hungry();

    public static Hungry getInstance(){
        return HUNGRY;
    }
}
