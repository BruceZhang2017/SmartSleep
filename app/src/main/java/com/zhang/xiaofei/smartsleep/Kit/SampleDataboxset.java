package com.zhang.xiaofei.smartsleep.Kit;

import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.zhang.xiaofei.smartsleep.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hesk on 7/1/2015.
 */
public class SampleDataboxset {

    public static List<String> newList() {
        final List<String> stringList = new ArrayList<>();
        stringList.add("111");
        stringList.add("TYAT");
        stringList.add("BMW");
        stringList.add("3M");
        stringList.add("Apple");
        stringList.add("Organe");
        stringList.add("Nike");
        stringList.add("Addos");
        stringList.add("76 RE");
        return stringList;
    }

    public static List<String> newList(int longh) {
        final List<String> j = newList();
        genItems(longh, j);
        return j;
    }

    public static List<String> newListFromGen(int n) {
        final List<String> s = new ArrayList<>();
        genItems(n, s);
        return s;
    }

    public static List<String> newListFromGen() {
        final List<String> s = new ArrayList<>();
        genItems(38, s);
        return s;
    }

    public static void genItems(final int howmany, final List<String> list) {
        for (int i = 0; i < howmany; i++) {
            Random e = new Random();
            list.add("大雅健康睡眠纽扣");
        }
    }

    public static void insertMore(easyRegularAdapter sd, int howmany) {
        for (int i = 0; i < howmany; i++) {
            sd.insertLast("More ** " + i);
        }
    }

    public static void insertMoreWhole(easyRegularAdapter sd, int howmany) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < howmany; i++) {
            items.add("More ** " + i);
        }
        sd.insert(items);
    }
}
