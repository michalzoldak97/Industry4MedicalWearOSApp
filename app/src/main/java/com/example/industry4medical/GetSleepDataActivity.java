package com.example.industry4medical;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.industry4medical.databinding.ActivityGetSleepDataBinding;
import com.example.industry4medical.model.API.AbstractAPIListener;
import com.example.industry4medical.model.DataContainerType;
import com.example.industry4medical.model.Model;
import com.example.industry4medical.model.SleepDataContainer;
import com.example.industry4medical.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GetSleepDataActivity extends Activity implements SensorEventListener {

    private static final int MAX_DATA_CONTAINER_LEN = 124;

    private TextView accText, hrText;
    private ActivityGetSleepDataBinding binding;

    private SensorManager mySensorManager;
    private Sensor accSensor, hrSensor;

    private List<SleepDataContainer> hrDataContainer = new ArrayList<>();
    private List<SleepDataContainer> accDataContainer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGetSleepDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        hrSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        configViewComponents();
    }
    private void configViewComponents(){
        accText = findViewById(R.id.accText);
        hrText = findViewById(R.id.hrText);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == hrSensor) {
            hrSensorActions(event);
        }else if(event.sensor == accSensor) {
            accSensorActions(event);
        }
    }

    private void hrSensorActions(SensorEvent event){
        hrText.setText(String.valueOf(event.values[0]));
        String currTime = (new Timestamp(System.currentTimeMillis())).toString();
        List<Float> currSData = new ArrayList<>();
        currSData.add(event.values[0]);
        SleepDataContainer sample = new SleepDataContainer(currTime, currSData);
        passDataToContainer(sample, DataContainerType.HEART_RATE);
        //System.out.println(sample.getSampleTime() + " " + sample.getSensorData());
    }
    private void passDataToContainer(SleepDataContainer dataSample, DataContainerType dType){
        switch (dType) {
            case HEART_RATE:
                if (hrDataContainer.size() < MAX_DATA_CONTAINER_LEN) {
                    hrDataContainer.add(dataSample);
                } else {
                    sendHRDataToAPI();
                    hrDataContainer.clear();
                }
                break;
            case ACCELERATION:
                if (accDataContainer.size() < MAX_DATA_CONTAINER_LEN) {
                    accDataContainer.add(dataSample);
                } else {
                    sendACCDataToAPI();
                    accDataContainer.clear();
                }
                break;
        }
    }

    private void sendHRDataToAPI() {
        //JSONObject finalJsonObj = new JSONObject();
        JSONArray finalJsonObj = new JSONArray();
        try{
            //finalJsonObj.put("DataType", "HR");
            JSONObject bodyHead = new JSONObject();
            bodyHead.put("DataType", "HR");
            finalJsonObj.put(bodyHead);
            for (SleepDataContainer dataContainer : hrDataContainer){
                JSONObject dataSampleObj = new JSONObject();
                dataSampleObj.put(dataContainer.getSampleTime(), dataContainer.getSensorData());
                finalJsonObj.put(dataSampleObj);
            }
            final Model model = Model.getInstance(GetSleepDataActivity.this.getApplication());
            model.sendData(finalJsonObj, new AbstractAPIListener() {
                @Override
                public void onPackageSent() {
                    System.out.println("On response actions");
                }
            });
        }catch (JSONException e){
            System.out.println("sendHRDataToAPI JSON exception");
        }
    }

    private void sendACCDataToAPI() {

        final Model model = Model.getInstance(GetSleepDataActivity.this.getApplication());
    }

    private void accSensorActions(SensorEvent event){
        accText.setText(String.format("%s %s %s", event.values[0],
                event.values[1], event.values[2]));
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenersRegistration(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        listenersRegistration(false);
    }

    private void listenersRegistration(boolean shouldRegister){
        try{
            if(shouldRegister) {
                mySensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
                mySensorManager.registerListener(this, hrSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else{
                mySensorManager.unregisterListener(this, accSensor);
                mySensorManager.unregisterListener(this, hrSensor);
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
}