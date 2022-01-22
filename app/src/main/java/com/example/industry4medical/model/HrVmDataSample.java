package com.example.industry4medical.model;

import java.util.ArrayList;

public class HrVmDataSample {
    private final String heartRate;
    private final String vectorMagnitude;

    public String getHeartRate() {
        return heartRate;
    }

    public String getVectorMagnitude() {
        return vectorMagnitude;
    }

    public HrVmDataSample(ArrayList<String> hrVmData){
        heartRate = hrVmData.get(0);
        vectorMagnitude = hrVmData.get(1);
    }
}
