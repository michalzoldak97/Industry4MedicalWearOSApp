package com.example.industry4medical;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.industry4medical.databinding.ActivityGetSleepDataBinding;
import com.example.industry4medical.model.API.AbstractAPIListener;
import com.example.industry4medical.model.SensorType;
import com.example.industry4medical.model.Model;
import com.example.industry4medical.model.SleepDataContainer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetSleepDataActivity extends Activity implements SensorEventListener {

    private static final int MAX_DATA_CONTAINER_LEN = 128;

    private TextView accText, hrText;
    private ActivityGetSleepDataBinding binding;

    private SensorManager mySensorManager;
    private Sensor accSensor, hrSensor;

    private List<SleepDataContainer> hrDataContainer = new ArrayList<>();
    private List<SleepDataContainer> accDataContainer = new ArrayList<>();

    private float[] previousAccState = new float[3];

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
            sensorActions(event, SensorType.HEART_RATE);
        }else if(event.sensor == accSensor && validateAccSensor(event)) {
            sensorActions(event, SensorType.ACCELERATION);
        }
    }
    private boolean validateAccSensor(SensorEvent event){
        if(Arrays.equals(event.values, previousAccState)){
            return false;
        } else{
            previousAccState = Arrays.copyOf(event.values, event.values.length);
            return true;
        }
    }

    private void sensorActions(SensorEvent event, SensorType sensorType){
        String currTime = (new Timestamp(System.currentTimeMillis())).toString();
        List<Float> currSData = new ArrayList<>();
        for(Float eventVal : event.values) {
            currSData.add(eventVal);
        }
        SleepDataContainer sample = new SleepDataContainer(currTime, currSData);
        passDataToContainer(sample, sensorType);

        switch (sensorType){
            case HEART_RATE: hrText.setText(String.format("%s",currSData.get(0)));
                break;
            case ACCELERATION: accText.setText(String.format("%s %s %s",currSData.get(0),
                    currSData.get(1), currSData.get(2)));
                break;
        }
    }
    private void passDataToContainer(SleepDataContainer dataSample, SensorType sensorType){
        switch (sensorType) {
            case HEART_RATE:
                if (hrDataContainer.size() < MAX_DATA_CONTAINER_LEN) {
                    hrDataContainer.add(dataSample);
                } else {
                    sendDataToAPI(sensorType);
                    hrDataContainer.clear();
                }
                break;
            case ACCELERATION:
                if (accDataContainer.size() < MAX_DATA_CONTAINER_LEN) {
                    accDataContainer.add(dataSample);
                } else {
                    sendDataToAPI(sensorType);
                    accDataContainer.clear();
                }
                break;
        }
        System.out.println("HR: " + hrDataContainer.size() + "\nACC " + accDataContainer.size());
    }

    private void sendDataToAPI(SensorType sensorType) {
        String dataType = "";
        List<SleepDataContainer> dataToPass = new ArrayList<>();
        switch (sensorType){
            case HEART_RATE:
                dataType = "HR";
                dataToPass = hrDataContainer;
                break;
            case ACCELERATION:
                dataType = "ACC";
                dataToPass = accDataContainer;
        }
        JSONArray finalJsonObj = new JSONArray();
        try{
            JSONObject bodyHead = new JSONObject();
            bodyHead.put("DataType", dataType);
            finalJsonObj.put(bodyHead);
            for (SleepDataContainer dataContainer : dataToPass){
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