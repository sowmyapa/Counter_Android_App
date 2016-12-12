package com.cs442.sparameshwara.counterapp;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * CounterService class which keeps counting number every sec and display notification every 10 second in a different thread.
 * Created by sowmyaparameshwara on 10/21/16.
 */
public class CounterService extends Service {

    /**The unit digit value of counter start*/
    int counterUnitDigitValue;
    /**Current value of the counter.*/
    int currentCounterIndex;
    /**Boolean which is set to true when service is active, set to false when service is stopped by the application.*/
    boolean isActive;

    /**Notification builder object*/
    NotificationCompat.Builder mBuilder;


    /**
     * Service callback on starting this service.
     * It will start the logic for computing the counter every sec and sending notification every 10 sec.
     * This returns 'START_NOT_STICKY' : So the service will not be restarted when it is forcefully destroyed
     * by system.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent newIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Counter Notification")
                .setContentText("Current Counter Value : "+ currentCounterIndex)
                .setSound(
                        RingtoneManager.getDefaultUri(
                                RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
        .setContentIntent(pendingIntent);
        isActive = true;
        currentCounterIndex = intent.getIntExtra(ActivityConstants.START_INDEX,ActivityConstants.DEFAULT_START_INDEX);
        counterUnitDigitValue = currentCounterIndex % 10;
        startCounterComputation();
        Log.d("sparameshwara:","onStartCommand(), input value is : "+currentCounterIndex);
        return START_NOT_STICKY;
    }

    /**
     * Creates a new thread for computing counter in background.
     */
    private void startCounterComputation() {
        Thread thread = new Thread(null, doBackgroundThreadProcessing,
                "Background");
        thread.start();
        Log.d("sparameshwara:","startCounterComputation(), started a new thread.");
    }

    /**
     * The method is destroyed and isActive is set to false;
     */
    @Override
    public void onDestroy() {
        isActive = false;
        super.onDestroy();
        Log.d("sparameshwara:","onDestroy().");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Runnable that executes the background processing method.
    private Runnable doBackgroundThreadProcessing = new Runnable() {
        public void run() {
            backgroundThreadProcessing();
        }
    };


    /**
     * Increments the counter index every 1 second when the service is active.
     * After 10 seconds it creates a notification event.
     */
    private void backgroundThreadProcessing() {
        while(isActive) {
            try {
                Thread.sleep(1000);
                currentCounterIndex++;
                Log.d("sparameshwara:","backgroundThreadProcessing(). New counter value : "+currentCounterIndex);
                if(currentCounterIndex %10== counterUnitDigitValue){
                  notifyUser();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Notifies the user with current counting index.
     */
    private void notifyUser() {
        mBuilder.setContentText("Current Counter Value : "+ currentCounterIndex);
        startForeground(ActivityConstants.COUNTER_NOTIFICATION_ID, mBuilder.build());
        Log.d("sparameshwara:","backgroundThreadProcessing(). Notification Sent, current counter value : "+currentCounterIndex);
    }


}


