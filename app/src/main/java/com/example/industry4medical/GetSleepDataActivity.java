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
import com.example.industry4medical.model.SleepDataSample;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetSleepDataActivity extends Activity implements SensorEventListener {

    private static final int MAX_DATA_CONTAINER_LEN = 10;

    private TextView accText, hrText;
    private ActivityGetSleepDataBinding binding;

    private SensorManager mySensorManager;
    private Sensor accSensor, hrSensor;

    private final List<SleepDataSample> sleepDataSamples = new ArrayList<>();

    private float[] previousAccState = new float[3];
    private String previousSecond = getCurrentTimeInSec();

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

    private String getCurrentTimeInSec(){
        String timeNow = (new Timestamp(System.currentTimeMillis())).toString();
        String[] splitDate = timeNow.split("\\.");
        return splitDate[0];
    }

    private boolean validateAccSensor(SensorEvent event){
        if(Arrays.equals(event.values, previousAccState)){
            return false;
        } else{
            previousAccState = Arrays.copyOf(event.values, event.values.length);
            return true;
        }
    }
    private static double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private String calcVectorMagnitude(List<Float> sensorData){
        double powSum = 0;
        for (Float var : sensorData){
            powSum += Math.pow(var, 2);
        }
        return String.valueOf(round(Math.sqrt(powSum)));
    }

    private SleepDataSample calcDataSample(
            String currTime
            ,List<Float> sensorData
            ,SensorType sensorType){
        ArrayList<String> parsedSensorData = new ArrayList<>();
        switch (sensorType) {
            case ACCELERATION:
                parsedSensorData.add(hrText.getText().toString());
                parsedSensorData.add(calcVectorMagnitude(sensorData));
                break;
            case HEART_RATE:
                parsedSensorData.add(String.valueOf(Math.round(sensorData.get(0))));
                parsedSensorData.add(accText.getText().toString());
        }
        return new SleepDataSample(currTime, parsedSensorData);
    }

    private void appendSleepData(SleepDataSample sleepDataSample){
        if (sleepDataSamples.size() < MAX_DATA_CONTAINER_LEN){
            sleepDataSamples.add(sleepDataSample);
        } else {
            sendDataToAPI();
            sleepDataSamples.clear();
        }
    }

    private void sensorActions(SensorEvent event, SensorType sensorType){
        String currTime = getCurrentTimeInSec();
        if (currTime.equals(previousSecond)){
            return;
        }
        else {
            previousSecond = currTime;
        }
        List<Float> currSData = new ArrayList<>();
        for(Float eventVal : event.values) {
            currSData.add(eventVal);
        }
        SleepDataSample sleepDataSample = calcDataSample(currTime, currSData, sensorType);
        System.out.println("Sleep sample -> Time: " + sleepDataSample.getSampleTimestamp() + " HR: "
                + sleepDataSample.getHrVmDataSample().getHeartRate() + " ACC: " +
                sleepDataSample.getHrVmDataSample().getVectorMagnitude());

        appendSleepData(sleepDataSample);

        switch (sensorType){
            case HEART_RATE: hrText.setText(String.format("%s",sleepDataSample.getHrVmDataSample()
                    .getHeartRate()));
                break;
            case ACCELERATION: accText.setText(String.format("%s",sleepDataSample.getHrVmDataSample()
                    .getVectorMagnitude()));
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == hrSensor) {
            sensorActions(event, SensorType.HEART_RATE);
        }else if(event.sensor == accSensor && validateAccSensor(event)) {
            sensorActions(event, SensorType.ACCELERATION);
        }
    }

    private void sendDataToAPI() {
        JSONObject objToSend = new JSONObject();
        try{
            for(SleepDataSample sleepDataSample : sleepDataSamples){
                JSONObject sensorData = new JSONObject();
                sensorData.put("HR", sleepDataSample.getHrVmDataSample().getHeartRate());
                sensorData.put("VM", sleepDataSample.getHrVmDataSample().getVectorMagnitude());
                objToSend.put(sleepDataSample.getSampleTimestamp(), sensorData);
            }
            final Model model = Model.getInstance(GetSleepDataActivity.this.getApplication());
            model.sendData(objToSend, new AbstractAPIListener() {
                @Override
                public void onPackageSent() {
                    System.out.println("On response actions");
                }
            });
        }catch (JSONException e){
            System.out.println("sendHRDataToAPI JSON exception");
        }
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

