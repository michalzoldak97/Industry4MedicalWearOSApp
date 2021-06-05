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
import com.example.industry4medical.model.Model;
import com.example.industry4medical.model.SleepDataContainer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GetSleepDataActivity extends Activity implements SensorEventListener {

    private static final int MAX_DATA_CONTAINER_LEN = 1024;

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
        accText =  (TextView) findViewById(R.id.accText);
        hrText =  (TextView) findViewById(R.id.hrText);
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
        passHRDataToContainer(sample);
        System.out.println(sample.getSampleTime() + " " + sample.getSensorData());
    }

    private void passHRDataToContainer(SleepDataContainer dataSample){
        if(hrDataContainer.size() < MAX_DATA_CONTAINER_LEN){
            hrDataContainer.add(dataSample);
        }else{
            sendHRDataToAPI();
            hrDataContainer.clear();
        }
    }

    private void sendHRDataToAPI() {

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