package com.zhang.xiaofei.smartsleep.Model.Record;

import java.io.Serializable;

public class HistoryBreath implements Serializable {
    int id;
    String serialId;
    String useDate;
    String bootType;
    String mode;
    float cureStress;
    float startStress;
    int delayTime;
    int exhaleRelease;
    float maxInhaleStress;
    float minInhaleStress;
    float minStress;
    float maxStress;
    float inhaleStress;
    int tidalVolume;
    float exhaleStress;
    int inhaleSensitivity;
    int exhaleSensitivity;
    int stressUp;
    String softVersion;
    int avaps;
    float respiratoryRate;
    float inhaleTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerialId() {
        return serialId;
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }

    public String getBootType() {
        return bootType;
    }

    public void setBootType(String bootType) {
        this.bootType = bootType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public float getCureStress() {
        return cureStress;
    }

    public void setCureStress(float cureStress) {
        this.cureStress = cureStress;
    }

    public float getStartStress() {
        return startStress;
    }

    public void setStartStress(float startStress) {
        this.startStress = startStress;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public int getExhaleRelease() {
        return exhaleRelease;
    }

    public void setExhaleRelease(int exhaleRelease) {
        this.exhaleRelease = exhaleRelease;
    }

    public float getMaxInhaleStress() {
        return maxInhaleStress;
    }

    public void setMaxInhaleStress(float maxInhaleStress) {
        this.maxInhaleStress = maxInhaleStress;
    }

    public float getMinInhaleStress() {
        return minInhaleStress;
    }

    public void setMinInhaleStress(float minInhaleStress) {
        this.minInhaleStress = minInhaleStress;
    }

    public float getMinStress() {
        return minStress;
    }

    public void setMinStress(float minStress) {
        this.minStress = minStress;
    }

    public float getMaxStress() {
        return maxStress;
    }

    public void setMaxStress(float maxStress) {
        this.maxStress = maxStress;
    }

    public float getInhaleStress() {
        return inhaleStress;
    }

    public void setInhaleStress(float inhaleStress) {
        this.inhaleStress = inhaleStress;
    }

    public int getTidalVolume() {
        return tidalVolume;
    }

    public void setTidalVolume(int tidalVolume) {
        this.tidalVolume = tidalVolume;
    }

    public float getExhaleStress() {
        return exhaleStress;
    }

    public void setExhaleStress(float exhaleStress) {
        this.exhaleStress = exhaleStress;
    }

    public int getInhaleSensitivity() {
        return inhaleSensitivity;
    }

    public void setInhaleSensitivity(int inhaleSensitivity) {
        this.inhaleSensitivity = inhaleSensitivity;
    }

    public int getExhaleSensitivity() {
        return exhaleSensitivity;
    }

    public void setExhaleSensitivity(int exhaleSensitivity) {
        this.exhaleSensitivity = exhaleSensitivity;
    }

    public int getStressUp() {
        return stressUp;
    }

    public void setStressUp(int stressUp) {
        this.stressUp = stressUp;
    }

    public String getSoftVersion() {
        return softVersion;
    }

    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
    }

    public int getAvaps() {
        return avaps;
    }

    public void setAvaps(int avaps) {
        this.avaps = avaps;
    }

    public float getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(float respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public float getInhaleTime() {
        return inhaleTime;
    }

    public void setInhaleTime(float inhaleTime) {
        this.inhaleTime = inhaleTime;
    }
}
