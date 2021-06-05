package com.example.industry4medical.model;

import java.util.ArrayList;
import java.util.List;

public class SleepDataContainer {
    private String sampleTime;
    public String getSampleTime() { return sampleTime; }
    public void setSampleTime(String sampleTime) { this.sampleTime = sampleTime; }
    private List<Float> sensorData;
    public List<Float> getSensorData() { return sensorData; }
    public void setSensorData(List<Float> sensorData) {
        this.sensorData = new ArrayList<>();
        this.sensorData = sensorData;
    }

    public SleepDataContainer(String sampleTime, List<Float> sensorData){
        this.sampleTime = sampleTime;
        this.sensorData = sensorData;
    }
}
