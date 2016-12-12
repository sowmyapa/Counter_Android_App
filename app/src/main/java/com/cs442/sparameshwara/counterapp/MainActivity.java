package com.cs442.sparameshwara.counterapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The main activity class for the counter app.
 * Created by sowmyaparameshwara on 10/21/16.
 */
public class MainActivity extends AppCompatActivity {

    /**The input edittext component.*/
    private EditText inputText;
    /**Button used for starting service*/
    private Button startServiceButton;
    /**Button used for stopping service*/
    private Button stopServiceButton;
    /**Value at which counting is started in application*/
    private int counterStartIndex;
    /**Boolean which is true if service is running, false otherwise*/
    private static boolean serviceRunning;

    /**
     * The activity class oncreate method.
     * In this method we initialise all the android components required.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputText = (EditText) findViewById(R.id.inputText);
        startServiceButton = (Button) findViewById(R.id.startButton);
        stopServiceButton = (Button) findViewById(R.id.stopButton);
        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStartButton();
            }
        });
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStopButton();
            }
        });
        Log.d("sparameshwara:","onCreate()");
    }

    /**
     * The method to process stop service action.
     * The counter service will be stopped.
     */
    private void onClickStopButton() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ActivityConstants.COUNTER_NOTIFICATION_ID);
        Intent counterIntent = new Intent(this,CounterService.class);
        stopService(counterIntent);
        serviceRunning = false;
        Toast.makeText(this,"Service Stopped",Toast.LENGTH_LONG).show();
        Log.d("sparameshwara:","onClickStopButton()");
    }

    /**
     * The method to process start service action
     * If the service is already running, it displays a toast message.
     * Else it will read the input from EditText, check if it is a valid number,
     *     if number is valid, it will use this value for starting counter service.
     *     else it will set the counter to DEFAULT_VALUE for starting counter service,
     *     if input is empty it prompts a message.
     */
    private void onClickStartButton() {
        if (!serviceRunning) {
            processInput();
        }else{
            Toast.makeText(this,"Service is already running, hence your request is ignored",Toast.LENGTH_LONG).show();
        }
        Log.d("sparameshwara:","onClickStartButton(), serviceRunning : "+serviceRunning);
    }

    /**
     * Process input.
     * it will read the input from EditText, check if it is a valid number,
     * if number is valid, it will use this value for starting counter service.
     * else it will set the counter to DEFAULT_VALUE for starting counter service,
     * if input is empty it prompts a message.
     */
    private void processInput() {
        String input = inputText.getText().toString();
        inputText.setText("");
        if(input == null || input.length() == 0){
            Toast.makeText(this,"Please enter input and try again.",Toast.LENGTH_LONG).show();
        }else if (input != null && input.length() > 0 && input.matches("[0-9]+")) {
            counterStartIndex = Integer.parseInt(input);
            startCounterService();
            Toast.makeText(this,"Service started with counter index : "+counterStartIndex,Toast.LENGTH_LONG).show();
        } else {
            counterStartIndex = ActivityConstants.DEFAULT_START_INDEX;
            startCounterService();
            Toast.makeText(this,"Input invalid, Service started with default counter index : "+counterStartIndex,Toast.LENGTH_LONG).show();
        }
        Log.d("sparameshwara:","processInput(), input value is : "+input);

    }

    /**
     * Starts the counter service.
     */
    private void startCounterService() {
        Intent counterIntent = new Intent(this, CounterService.class);
        counterIntent.putExtra(ActivityConstants.START_INDEX, counterStartIndex);
        startService(counterIntent);
        serviceRunning = true;
        Log.d("sparameshwara:","startCounterService()");
    }
}
