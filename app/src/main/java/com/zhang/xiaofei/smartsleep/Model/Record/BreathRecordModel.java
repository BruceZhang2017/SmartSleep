package com.zhang.xiaofei.smartsleep.Model.Record;

import com.zhang.xiaofei.smartsleep.Model.Login.BaseProtocol;

import java.io.Serializable;

public class BreathRecordModel extends BaseProtocol implements Serializable {
    float exhaleStressNice;
    int dayCount;
    float stressNice;
    float inhaleStressNice;
    float respiratoryRate;
    float exhaleStress;
    float minuThroughput;
    int tidalVolume;
    float minuThroughputNice;
    float respiratoryRateNice;
    float tidalVolumeNice;
    String useTime;
    float cureStress;
    String avgUseTime;
    int useDay;
    float inhaleStress;
    BreathMaxAvg maxAvg;
    String startDate = "";
    String endDate = "";

    public float getExhaleStressNice() {
        return exhaleStressNice;
    }

    public void setExhaleStressNice(float exhaleStressNice) {
        this.exhaleStressNice = exhaleStressNice;
    }

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    public float getStressNice() {
        return stressNice;
    }

    public void setStressNice(float stressNice) {
        this.stressNice = stressNice;
    }

    public float getInhaleStressNice() {
        return inhaleStressNice;
    }

    public void setInhaleStressNice(float inhaleStressNice) {
        this.inhaleStressNice = inhaleStressNice;
    }

    public float getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(float respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public float getExhaleStress() {
        return exhaleStress;
    }

    public void setExhaleStress(float exhaleStress) {
        this.exhaleStress = exhaleStress;
    }

    public float getMinuThroughput() {
        return minuThroughput;
    }

    public void setMinuThroughput(float minuThroughput) {
        this.minuThroughput = minuThroughput;
    }

    public int getTidalVolume() {
        return tidalVolume;
    }

    public void setTidalVolume(int tidalVolume) {
        this.tidalVolume = tidalVolume;
    }

    public float getMinuThroughputNice() {
        return minuThroughputNice;
    }

    public void setMinuThroughputNice(float minuThroughputNice) {
        this.minuThroughputNice = minuThroughputNice;
    }

    public float getRespiratoryRateNice() {
        return respiratoryRateNice;
    }

    public void setRespiratoryRateNice(float respiratoryRateNice) {
        this.respiratoryRateNice = respiratoryRateNice;
    }

    public float getTidalVolumeNice() {
        return tidalVolumeNice;
    }

    public void setTidalVolumeNice(float tidalVolumeNice) {
        this.tidalVolumeNice = tidalVolumeNice;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public float getCureStress() {
        return cureStress;
    }

    public void setCureStress(float cureStress) {
        this.cureStress = cureStress;
    }

    public String getAvgUseTime() {
        return avgUseTime;
    }

    public void setAvgUseTime(String avgUseTime) {
        this.avgUseTime = avgUseTime;
    }

    public int getUseDay() {
        return useDay;
    }

    public void setUseDay(int useDay) {
        this.useDay = useDay;
    }

    public float getInhaleStress() {
        return inhaleStress;
    }

    public void setInhaleStress(float inhaleStress) {
        this.inhaleStress = inhaleStress;
    }

    public BreathMaxAvg getMaxAvg() {
        return maxAvg;
    }

    public void setMaxAvg(BreathMaxAvg maxAvg) {
        this.maxAvg = maxAvg;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
