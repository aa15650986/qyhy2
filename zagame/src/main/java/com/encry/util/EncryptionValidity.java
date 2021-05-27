package com.encry.util;

import java.util.HashSet;
import java.util.Set;

public class EncryptionValidity {

    private static Set set1 = new HashSet();
    private static Set set2 = new HashSet();
    private static Set set3 = new HashSet();


    public static Object isValidity(String str1, String str2) {
        Object set = new HashSet();
        switch(str2.hashCode()) {
            case 49:
                if(str2.equals("1")) {
                    addSet1(str1);
                }
                break;
            case 50:
                if(str2.equals("2")) {
                    removeSet1(str1);
                }
                break;
            case 51:
                if(str2.equals("3")) {
                    addSet2(str1);
                }
                break;
            case 52:
                if(str2.equals("4")) {
                    removeSet2(str1);
                }
                break;
            case 53:
                if(str2.equals("5")) {
                    addSet3(str1);
                }
                break;
            case 54:
                if(str2.equals("6")) {
                    removeSet3(str1);
                }
                break;
            case 55:
                if(str2.equals("7")) {
                    set = set1;
                }
                break;
            case 56:
                if(str2.equals("8")) {
                    set = set2;
                }
                break;
            case 57:
                if(str2.equals("9")) {
                    set = set3;
                }
                break;
            case 1567:
                if(str2.equals("10")) {
                    set1.clear();
                }
                break;
            case 1568:
                if(str2.equals("11")) {
                    set2.clear();
                }
                break;
            case 1569:
                if(str2.equals("12")) {
                    set3.clear();
                }
        }

        return set;
    }

    public static void addSet1(String account) {
        set1.add(account);
    }

    public static void removeSet1(String account) {
        set1.remove(account);
    }

    public static void addSet2(String account) {
        set2.add(account);
    }

    public static void removeSet2(String account) {
        set2.remove(account);
    }

    public static void addSet3(String account) {
        set3.add(account);
    }

    public static void removeSet3(String account) {
        set3.remove(account);
    }

    public static Set getValiditySet() {
        return set1;
    }

    public static void setSet1(Set set1) {
        set1 = set1;
    }

    public static Set getNoValiditySet() {
        return set2;
    }

    public static void setSet2(Set set2) {
        set2 = set2;
    }

    public static Set getSuspectedValiditySet() {
        return set3;
    }

    public static void setSet3(Set set3) {
        set3 = set3;
    }
    public static void main(String[] args) {
        String a = "10";
        System.out.println(a.hashCode());
    }
}