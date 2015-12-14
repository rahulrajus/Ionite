package com.aneesh.ionite;

import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
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

    @Override
    public void onPause() {
        super.onPause();
        String date = "";
        String name = "";
        ArrayList<String[]> schedule = null;
        Object[] output = MainActivity.output;
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
                        cancelNotification(
                                getNotification(schedule.get(i)[0] + " will start in " + delayTime + " minutes!",
                                        pdStart.getTime()),
                                delay, id);
                    cancelNotification(getNotification(schedule.get(i)[0] + " just started!", pdStart.getTime()), delay,
                            id + 2);

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
                        cancelNotification(
                                getNotification(schedule.get(i)[0] + " will end in " + delayTime + " minutes!",
                                        pdend.getTime()),
                                delay, id);
                    cancelNotification(getNotification(schedule.get(i)[0] + " just ended!", pdend.getTime()), delay,
                            id + 2);

                }
            }
        }
        Intent pushIntent2 = new Intent(getApplicationContext(), AlarmCreator.class);
        pushIntent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startService(pushIntent2);

        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void cancelNotification(Notification notification, long delay, int id) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        try {
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {
        }
    }

    private Notification getNotification(String content, long timer) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Ionite!");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent pushIntent2 = new Intent(getApplicationContext(), AlarmCreator.class);
                pushIntent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startService(pushIntent2);

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            CheckBoxPreference startPeriod = (CheckBoxPreference) getPreferenceManager().findPreference("startPeriod");
            CheckBoxPreference endPeriod = (CheckBoxPreference) getPreferenceManager().findPreference("endPeriod");

            startPeriod.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getActivity().getApplicationContext(), "Restart phone for immediate changes.",
                            Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            endPeriod.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getActivity().getApplicationContext(), "Restart phone for immediate changes.",
                            Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        }
    }

}