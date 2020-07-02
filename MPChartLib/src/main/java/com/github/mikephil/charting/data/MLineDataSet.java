package com.github.mikephil.charting.data;

import java.util.List;

public class MLineDataSet extends LineDataSet {

    public MLineDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public int getCircleColor(int index) {
        //set1.setCircleColors(0xFF5DF2FF, 0x626AEA, 0xFF499BE5, 0xFFF3D032, 0xFFE92C2C);
        // 6EE1CA 100分 / 499BE5 80分 / 626AEA 60分 / F3D032 40分 / E92C2C 20分
        Entry entry = getValues().get(index);
        if (entry.getY() >= 85) {
            return 0xFF6EE1CA;
        } else if (entry.getY() >= 70) {
            return 0xFF499BE5;
        } else if (entry.getY() >= 50) {
            return 0xFF626AEA;
        } else if (entry.getY() > 0) {
            return 0xFFF3D032;
        } else if (entry.getY() == 0) {
            return 0xFFE92C2C;
        }
        return super.getCircleColor(index);
    }
}
