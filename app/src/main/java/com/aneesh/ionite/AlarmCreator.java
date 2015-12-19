package com.aneesh.ionite;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * @author Aneesh
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class AlarmCreator extends Service {

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String date = "";
        String name = "";
        ArrayList<String[]> schedule = null;
        Object[] output = null;
        try {
            output = new Retriever().execute("https://ion.tjhsst.edu").get();
        } catch (InterruptedException | ExecutionException e) {

        }
        date = (String) output[0];
        name = (String) output[1];
        schedule = (ArrayList<String[]>) output[2];
        int[] mydates = setScheduleDate(date);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean startPeriodNotif = SP.getBoolean("startPeriod", false);
        boolean endPeriodNotif = SP.getBoolean("endPeriod", false);
        int delayTime = SP.getInt("delayTime", 0);
        if (startPeriodNotif) {
            for (int i = 0; i < schedule.size(); i++) {
                Date now = new Date();
                Date pdStart = new Date();

                String timing = (schedule.get(i))[1];
                String startTime = "";
                String[] parts = timing.split(" - ");
                startTime = parts[0];

                int startTimeHour = 0;
                int startTimeMin = 0;

                parts = startTime.split(":");
                startTimeHour = Integer.parseInt(parts[0]);
                startTimeMin = Integer.parseInt(parts[1]);

                if (startTimeHour < 8)
                    startTimeHour += 12;

                Calendar myCal = Calendar.getInstance();
                myCal.set(Calendar.YEAR, mydates[0]);
                myCal.set(Calendar.MONTH, mydates[1]);
                myCal.set(Calendar.DAY_OF_MONTH, mydates[2]);
                myCal.set(Calendar.HOUR_OF_DAY, startTimeHour);
                myCal.set(Calendar.MINUTE, startTimeMin);
                pdStart = myCal.getTime();

                if (now.before(pdStart)) {
                    int id = Integer.parseInt(mydates[0] + mydates[1] + mydates[2] + Integer.toString(i) + "0");
                    long delay = pdStart.getTime() - now.getTime() - (delayTime * 1000 * 60);
                    Calendar date1 = Calendar.getInstance();
                    long time = date1.getTimeInMillis();
                    pdStart = new Date(time - (delayTime * 60 * 1000));
                    if (delayTime != 0)
                        scheduleNotification(getNotification(schedule.get(i)[0] + " will start in " + delayTime + " minutes!", pdStart.getTime()), delay, id);
                    scheduleNotification(getNotification(schedule.get(i)[0] + " just started!", pdStart.getTime()), delay, id + 2);

                }
            }
        }
        if (endPeriodNotif) {
            for (int i = 0; i < schedule.size(); i++) {
                Date now = new Date();
                Date pdend = new Date();

                String timing = (schedule.get(i))[1];
                String endTime = "";
                String[] parts = timing.split(" - ");
                endTime = parts[1];

                int endTimeHour = 0;
                int endTimeMin = 0;

                parts = endTime.split(":");
                endTimeHour = Integer.parseInt(parts[0]);
                endTimeMin = Integer.parseInt(parts[1]);

                if (endTimeHour < 8)
                    endTimeHour += 12;

                Calendar myCal = Calendar.getInstance();
                myCal.set(Calendar.YEAR, mydates[0]);
                myCal.set(Calendar.MONTH, mydates[1]);
                myCal.set(Calendar.DAY_OF_MONTH, mydates[2]);
                myCal.set(Calendar.HOUR_OF_DAY, endTimeHour);
                myCal.set(Calendar.MINUTE, endTimeMin);
                pdend = myCal.getTime();

                if (now.before(pdend)) {
                    int id = Integer.parseInt(mydates[0] + mydates[1] + mydates[2] + Integer.toString(i) + "1");
                    long delay = pdend.getTime() - now.getTime() - delayTime * 1000 * 60;
                    Calendar date1 = Calendar.getInstance();
                    long time = date1.getTimeInMillis();
                    pdend = new Date(time - (delayTime * 60 * 1000));
                    if (delayTime != 0)
                        scheduleNotification(getNotification(schedule.get(i)[0] + " will end in " + delayTime + " minutes!", pdend.getTime()), delay, id);
                    scheduleNotification(getNotification(schedule.get(i)[0] + " just ended!",pdend.getTime()), delay, id + 2);

                }
            }
        }
        stopSelf();
        return START_STICKY;
    }

    public int[] setScheduleDate(String date) {
        String[] stuff = date.split(", |\\. ");
        int month = 0;
        int day = Integer.parseInt(stuff[2]);
        int year = 0;
        switch (stuff[1]) {
            case "Jan":
                month = 0;
                break;
            case "Feb":
                month = 1;
                break;
            case "March":
                month = 2;
                break;
            case "April":
                month = 3;
                break;
            case "May":
                month = 4;
                break;
            case "June":
                month = 5;
                break;
            case "July":
                month = 6;
                break;
            case "Aug":
                month = 7;
                break;
            case "Sept":
                month = 8;
                break;
            case "Oct":
                month = 9;
                break;
            case "Nov":
                month = 10;
                break;
            case "Dec":
                month = 11;
                break;
        }
        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.MONTH) == 11 && month == 0) {
            cal.add(Calendar.YEAR, 1);
            year = cal.get(Calendar.YEAR);
        } else {
            year = cal.get(Calendar.YEAR);
        }
        int[] output = {year, month, day};
        return output;
    }

    private void scheduleNotification(Notification notification, long delay, int id) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content, long timer) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Ionite!");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.launcher_pic);
        builder.setLargeIcon(bm);
        builder.setVibrate(new long[]{0, 1000, 1000});
        builder.setWhen(timer);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(this, MainActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent sender = PendingIntent.getActivity(this, 192839, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(sender);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        return builder.build();
    }

}
