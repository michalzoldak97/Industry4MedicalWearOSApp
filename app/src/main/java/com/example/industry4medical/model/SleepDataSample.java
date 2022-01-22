package com.example.industry4medical.model;

import java.util.ArrayList;

public class SleepDataSample {
    private String sampleTimestamp;

    public String getSampleTimestamp() {
        return sampleTimestamp;
    }

    private final HrVmDataSample hrVmDataSample;

    public HrVmDataSample getHrVmDataSample(){
        return hrVmDataSample;
    }

    public SleepDataSample(String time, ArrayList<String> data){
        sampleTimestamp = time;
        hrVmDataSample = new HrVmDataSample(data);
    }

}
