package com.example.myapplication.Classes;

public class Settings {


    private static double real_width;
    private static double real_height;
    private static boolean isAI;


    public static double getReal_width() {
        return real_width;
    }

    public static void setReal_width(double real_width) {
        Settings.real_width = real_width;
    }

    public static double getReal_height() {
        return real_height;
    }

    public static void setReal_height(double real_height) {
        Settings.real_height = real_height;
    }

    public static boolean isIsAI() {
        return isAI;
    }

    public static void setIsAI(boolean isAI) {
        Settings.isAI = isAI;
    }



}
