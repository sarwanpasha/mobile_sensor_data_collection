package com.example.iot_sensor_data_collection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.siegmar.fastcsv.writer.CsvWriter;

public class MainActivity extends AppCompatActivity implements
        SensorEventListener {

    //Sensor related
    private SensorManager sensorManager;     // Sensor manager
    private Sensor accelerometer;
    private Sensor magnetic;
    private Sensor gyro;

    private int counter=1;

    private boolean recording = false;
    private boolean counterOn = false;

    private float accValues[] = new float[3];
    private float gyroValues[] = new float[3];
    private float magValues[] = new float[3];

    private Context context;

    private static final int REQUESTCODE_STORAGE_PERMISSION = 1;

    Collection<String[]> accerelometerData = new ArrayList<>();
    Collection<String[]> magneticData = new ArrayList<>();
    Collection<String[]> gyroData = new ArrayList<>();

    private CsvWriter csvWriter=null;

    TextView stateText;
    //    EditText fileIDEdit;
    Switch counterSwitch;

    TextView accText;
    TextView gyroText;
    TextView magText;

    String Gender,Hand,Application,Age;

    private String EVENT_DATE_TIME = "2021-12-31 10:30:00";
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private LinearLayout linear_layout_1, linear_layout_2;
    private TextView tv_minute, tv_second;
    private Handler handler = new Handler();
    private Runnable runnable;

    private int counter_time;

    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.button).setOnClickListener(listenerStartButton);
        findViewById(R.id.button2).setOnClickListener(listenerStopButton);

//        fileIDEdit = (EditText) findViewById(R.id.editText);
        accText = (TextView) findViewById(R.id.textView5);
        gyroText = (TextView) findViewById(R.id.textView6);
        magText = (TextView) findViewById(R.id.textView7);

        stateText = (TextView) findViewById(R.id.textView);
        stateText.setText("Sensor Data Collection App");



        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner_gender);
        //create a list of items for the spinner.
        String[] items = new String[]{"Gender", "Male", "Female"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Gender = (String) parent.getItemAtPosition(position);
//                Toast.makeText(MainActivity.this,Gender , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });



        //get the spinner from the xml.
        Spinner dropdown_hand = findViewById(R.id.spinner_hand);
        //create a list of items for the spinner.
        String[] items_hand = new String[]{"Hand Holding Mobile", "Left Hand", "Right Hand", "Both Hands"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter_hand = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_hand);
        //set the spinners adapter to the previously created one.
        dropdown_hand.setAdapter(adapter_hand);

        dropdown_hand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Hand = (String) parent.getItemAtPosition(position);
//                Toast.makeText(MainActivity.this, Hand , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        //get the spinner from the xml.
        Spinner dropdown_age = findViewById(R.id.spinner_age);
        //create a list of items for the spinner.
        String[] items_age = new String[]{"Age", "<20", "20-25", "25-30", "30-35", ">35"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter_age = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_age);
        //set the spinners adapter to the previously created one.
        dropdown_age.setAdapter(adapter_age);

        dropdown_age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                Age = (String) parent.getItemAtPosition(position);
//                Toast.makeText(MainActivity.this, Age , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        //get the spinner from the xml.
        Spinner dropdown_app_name = findViewById(R.id.spinner_app_name);
        //create a list of items for the spinner.
        String[] items_app_name = new String[]{"Application Name", "Facebook", "Instagram", "Twitter", "Whatsapp"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter_app_name = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_app_name);
        //set the spinners adapter to the previously created one.
        dropdown_app_name.setAdapter(adapter_app_name);

        dropdown_app_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                Application = (String) parent.getItemAtPosition(position);
//                Toast.makeText(MainActivity.this, Application , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
//        counterSwitch = (Switch) findViewById(R.id.switch3);
//        counterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // The toggle is enabled
//                    if (!recording) {
//                        counterOn = true;
//                    } else {
//                        Toast.makeText(MainActivity.this, "Cannot change this option while recording.", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        });

        context=this;

        //Sensor related
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);      // Accelerometer
        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);    //Linear accelaration
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);     // Step detected


        initUI();
//        countDownStart();
//        startTimer();
    }


    private void startTimer() {


        countDownTimer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_minute.setText(String.valueOf(counter_time));
                counter_time++;
            }


            @Override
            public void onFinish() {
                counter_time=0;
                tv_minute.setText("Finished");
//                startTimer(1);

                if (recording == true) {
                    recording = false;

                    tv_minute.setText("Finished");
                    counter = 0;

//                    String value = fileIDEdit.getText().toString();

                    stateText.setText("Recording stopped");
                    stateText.setTextColor(Color.parseColor("#0000FF"));

                    if (storagePermitted((Activity) context)) {
                        csvWriter = new CsvWriter();

                        final int min = 0;
                        final int max = 10000;
                        final int random = new Random().nextInt((max - min) + 1) + min;

                        String Final_string = "User_" + random + "_Gender_" + Gender + "_Hand_" + Hand +
                                "_Application_" + Application + "_Age_" + Age;
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Final_string + "_(Accelerometer).csv");
                        File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Final_string + "_(Gyroscope).csv");
                        File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Final_string + "_(Magnetic).csv");


                        try {
                            csvWriter.write(file, StandardCharsets.UTF_8, accerelometerData);
                            csvWriter.write(file2, StandardCharsets.UTF_8, gyroData);
                            csvWriter.write(file1, StandardCharsets.UTF_8, magneticData);
                            Toast.makeText(MainActivity.this, "Stored Path = " + file.toString(), Toast.LENGTH_LONG).show();
                            //                        Toast.makeText(MainActivity.this, "File recorded in memory.", Toast.LENGTH_LONG).show();
                        } catch (IOException io) {
                            Log.d("Error", io.getLocalizedMessage());
                        }
                    }
                }
            }
        }.start();

//        if(status==0) {
//            countDownTimer.start();
//        }else {
//            countDownTimer.cancel();
//        }
    }




    private void initUI() {
        linear_layout_1 = findViewById(R.id.linear_layout_1);
        linear_layout_2 = findViewById(R.id.linear_layout_2);
        tv_minute = findViewById(R.id.tv_minute);
//        tv_second = findViewById(R.id.tv_second);
    }


    private View.OnClickListener listenerStartButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Gender.equals("Gender") || Hand.equals("Hand Holding Mobile") ||
                    Application.equals("Application Name") || Age.equals("Age")){
                Toast.makeText(MainActivity.this, "Please select All Values from Dropdown!!", Toast.LENGTH_LONG).show();

            }
            else {
//                counter_time=0;
                startTimer();
//                countDownTimer.start();
                recording = true;
                stateText.setText("Recording started");
                stateText.setTextColor(Color.parseColor("#FF0000"));
            }
        }
    };

    //    private int REQUEST_CODE = 1;
    private View.OnClickListener listenerStopButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

//            write_fun();
            if (recording == true) {
                recording = false;

                counter_time=0;
//                    startTimer(1);
                countDownTimer.cancel();
//                    countDownTimer.onFinish();
                tv_minute.setText("Finished");
                counter = 0;

//                    String value = fileIDEdit.getText().toString();

                stateText.setText("Recording stopped");
                stateText.setTextColor(Color.parseColor("#0000FF"));

                if (storagePermitted((Activity) context)) {
                    csvWriter = new CsvWriter();

                    final int min = 0;
                    final int max = 10000;
                    final int random = new Random().nextInt((max - min) + 1) + min;

                    String Final_string = "User_" + random + "_Gender_" + Gender + "_Hand_" + Hand +
                            "_Application_" + Application + "_Age_" + Age;
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Final_string + "_(Accelerometer).csv");
                    File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Final_string + "_(Gyroscope).csv");
                    File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Final_string + "_(Magnetic).csv");


                    try {
                        csvWriter.write(file, StandardCharsets.UTF_8, accerelometerData);
                        csvWriter.write(file2, StandardCharsets.UTF_8, gyroData);
                        csvWriter.write(file1, StandardCharsets.UTF_8, magneticData);
                        Toast.makeText(MainActivity.this, "Stored Path = " + file.toString(), Toast.LENGTH_LONG).show();
                        //                        Toast.makeText(MainActivity.this, "File recorded in memory.", Toast.LENGTH_LONG).show();
                    } catch (IOException io) {
                        Log.d("Error", io.getLocalizedMessage());
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "Nothing to save. Recording was not started.", Toast.LENGTH_LONG).show();
            }

        }
    };



    @Override
    protected void onResume() {
        super.onResume();

        // Sensor listeners registration
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_FASTEST);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {

        // Converts timestamp to milliseconds
        long timeInMillis = (new Date()).getTime()
                + (event.timestamp - System.nanoTime()) / 1000000L;

        if (recording) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accText.setText("Accelerometer:   X= " + roundThis(event.values[0]) + "   Y= " + roundThis(event.values[1]) + "   Z= " + roundThis(event.values[2]));
                Log.d("Record", "Accelerometer" + String.valueOf(counter));
                accValues = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                gyroText.setText("Gyroscope:   X= " + roundThis(event.values[0]) + "   Y= " + roundThis(event.values[1]) + "   Z= " + roundThis(event.values[2]));
                Log.d("Record", "Gyroscope" + String.valueOf(counter));
                gyroValues = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magText.setText("Magnetometer:   X= " + roundThis(event.values[0]) + "   Y= " + roundThis(event.values[1]) + "   Z= " + roundThis(event.values[2]));
                Log.d("Record", "Magnetometer" + String.valueOf(counter));
                magValues = event.values;
            }

            if (counterOn){
                accerelometerData.add(new String[]{String.valueOf(counter), String.valueOf(accValues[0]), String.valueOf(accValues[1]), String.valueOf(accValues[2])});
                gyroData.add(new String[]{String.valueOf(counter), String.valueOf(gyroValues[0]), String.valueOf(gyroValues[1]), String.valueOf(gyroValues[2])});
                magneticData.add(new String[]{String.valueOf(counter), String.valueOf(magValues[0]), String.valueOf(magValues[1]), String.valueOf(magValues[2])});
            } else {
                accerelometerData.add(new String[]{String.valueOf(timeInMillis), String.valueOf(accValues[0]), String.valueOf(accValues[1]), String.valueOf(accValues[2])});
                gyroData.add(new String[]{String.valueOf(timeInMillis), String.valueOf(gyroValues[0]), String.valueOf(gyroValues[1]), String.valueOf(gyroValues[2])});
                magneticData.add(new String[]{String.valueOf(timeInMillis), String.valueOf(magValues[0]), String.valueOf(magValues[1]), String.valueOf(magValues[2])});
            }

            counter++;
        }

    }

    // Checks if there is permission to write and read in memory
    // Requests permission to the user if not
    private static boolean storagePermitted(Activity activity) {

        // Check read and write permissions
        Boolean readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Boolean writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (readPermission && writePermission) {
            return true;
        }

        // Request permission to the user
        ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUESTCODE_STORAGE_PERMISSION);

        return false;
    }

    // Rounds the value
    public static float roundThis(float value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(4, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Mandatory
    }
}

/* VERSION 1.0 OF ON SENSOR CHANGED
        if (recording) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accerelometerData.add(new String[]{String.valueOf(timeInMillis), String.valueOf(event.values[0]), String.valueOf(event.values[1]), String.valueOf(event.values[2])});
            }
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                gyroData.add(new String[]{String.valueOf(timeInMillis), String.valueOf(event.values[0]), String.valueOf(event.values[1]), String.valueOf(event.values[2])});
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticData.add(new String[]{String.valueOf(timeInMillis), String.valueOf(event.values[0]), String.valueOf(event.values[1]), String.valueOf(event.values[2])});
            }
        }
 */