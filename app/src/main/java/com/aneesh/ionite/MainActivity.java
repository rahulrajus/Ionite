package com.aneesh.ionite;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

    static final int DATE_DIALOG_ID = 999;
    public static Object[] output = null;
    public static boolean retrieved = false;
    // static
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    static LinearLayout llV;
    static int buttonsPressed = 0;
    static ScrollView scroller;
    static int schedYear;
    static int schedMonth;
    static int schedDay;
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, selectedYear);
            cal.set(Calendar.MONTH, selectedMonth);
            cal.set(Calendar.DAY_OF_MONTH, selectedDay);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                    || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                dlgAlert.setMessage("Please choose a weekday!");
                dlgAlert.setTitle("Choose another date");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                return;
            } else {
                selectedMonth++;
                String date = "";
                String name = "";
                ArrayList<String[]> schedule = null;
                output = null;
                try {
                    output = new Retriever().execute("https://ion.tjhsst.edu/?date=" + Integer.toString(selectedYear)
                            + "-" + Integer.toString(selectedMonth) + "-" + Integer.toString(selectedDay)).get();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                date = (String) output[0];
                name = (String) output[1];
                schedule = (ArrayList<String[]>) output[2];
                setScheduleDate(date);
                display(date, name, schedule);
            }
        }
    };

    public static boolean shouldRetrieve() {
        java.util.Date date1 = new java.util.Date();

        Time now = new Time(date1.getTime());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);

        Time parameter = new Time(cal.getTime().getTime());

        if (now.after(parameter)) {
            return true;
        }
        retrieved = false;
        return false;
    }

    public static String getCurrentClass(ArrayList<String[]> schedule, String dayname) {
        String currentClass = "";

        java.util.Date date = new java.util.Date();

        /***/
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, 11);
        cal1.set(Calendar.MINUTE, 40);
        cal1.set(Calendar.SECOND, 00);
        /***/

        Time now = new Time(date.getTime());

        // Time now = new Time(cal1.getTime().getTime());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 40);
        cal.set(Calendar.SECOND, 00);

        Time startSchool = new Time(cal.getTime().getTime());

        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);

        Time endSchool = new Time(cal.getTime().getTime());

        if (now.before(startSchool) || now.after(endSchool) || dayname.equals("HOLIDAY")
                || schedDay != cal.get(Calendar.DAY_OF_MONTH)) {
            return "NONE";
        } else {
            for (String[] pair : schedule) {
                String timing = pair[1];
                String startTime = "";
                String endTime = "";

                String[] parts = timing.split(" - ");
                startTime = parts[0];
                endTime = parts[1];

                String sTime1 = startTime.split(":")[0];
                String sTime2 = startTime.split(":")[1];

                String eTime1 = endTime.split(":")[0];
                String eTime2 = endTime.split(":")[1];

                int sTimeInt1 = Integer.parseInt(sTime1);
                int sTimeInt2 = Integer.parseInt(sTime2);

                int eTimeInt1 = Integer.parseInt(eTime1);
                int eTimeInt2 = Integer.parseInt(eTime2);

                if (sTimeInt1 < 8) {
                    sTimeInt1 += 12;
                }

                if (eTimeInt1 < 8) {
                    eTimeInt1 += 12;
                }

                Calendar cal2 = Calendar.getInstance();
                cal2.set(Calendar.HOUR_OF_DAY, sTimeInt1);
                cal2.set(Calendar.MINUTE, sTimeInt2);
                cal2.set(Calendar.SECOND, 00);

                Time start = new Time(cal2.getTime().getTime());

                cal2.set(Calendar.HOUR_OF_DAY, eTimeInt1);
                cal2.set(Calendar.MINUTE, eTimeInt2);
                cal2.set(Calendar.SECOND, 00);

                Time end = new Time(cal2.getTime().getTime());

                if (now.after(start) && now.before(end)) {
                    currentClass = pair[0];
                    break;
                }
            }
        }
        return currentClass;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (haveNetworkConnection() == false) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage(
                    "You are not connected to the internet. Connect to a cellular network or WiFi and re-open the app.");
            dlgAlert.setTitle("No connection");
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            dlgAlert.create().show();
        }

        Intent pushIntent = new Intent(this, com.aneesh.ionite.DayCycle.class);
        if (isMyServiceRunning(DayCycle.class))
            stopService(pushIntent);
        startService(pushIntent);
        Intent pushIntent2 = new Intent(this, com.aneesh.ionite.AlarmCreator.class);
        startService(pushIntent2);

        // Setup stuff
        LinearLayout layoutV = (LinearLayout) findViewById(R.id.root);
        llV = layoutV;
        ScrollView scroll = (ScrollView) findViewById(R.id.scroller);
        scroller = scroll;
        RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.mainLayout);

        rlayout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeRight() {
                switchDays(-1);
            }

            public void onSwipeLeft() {
                switchDays(1);
            }

            public void openTheThing() {
                Intent intent1 = new Intent(MainActivity.this, TimerActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.pull_in_up, R.anim.push_out_down);
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        scroller.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeRight() {
                switchDays(-1);
            }

            public void onSwipeLeft() {
                switchDays(1);
            }

            public void openTheThing() {
                Intent intent1 = new Intent(MainActivity.this, TimerActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.pull_in_up, R.anim.push_out_down);
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        llV.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeRight() {
                switchDays(-1);
            }

            public void onSwipeLeft() {
                switchDays(1);
            }

            public void openTheThing() {
                Intent intent1 = new Intent(MainActivity.this, TimerActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.pull_in_up, R.anim.push_out_down);
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = 0;
        try {
            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // handle exception
            e.printStackTrace();
            return;
        }

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean animations = SP.getBoolean("animationSelector", false);
        LayoutTransition layoutTransition = new LayoutTransition();

        if (animations)
            MainActivity.llV.setLayoutTransition(layoutTransition);
        else
            MainActivity.llV.setLayoutTransition(null);

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
        File file = new File("data.txt");
        // Check for first run or upgrade
        if (savedVersionCode == DOESNT_EXIST || currentVersionCode > savedVersionCode || !file.exists()) {
            String date = "";
            String name = "";
            ArrayList<String[]> schedule = null;
            try {
                output = new Retriever().execute("https://ion.tjhsst.edu").get();
            } catch (InterruptedException | ExecutionException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
            if (output == null) {
                try {
                    output = new Retriever().execute("https://ion.tjhsst.edu").get();
                } catch (InterruptedException | ExecutionException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            date = (String) output[0];
            name = (String) output[1];
            schedule = (ArrayList<String[]>) output[2];
            setScheduleDate(date);
            display(date, name, schedule);

            String stored = date + "," + name + ",";
            for (String[] classes : schedule) {
                stored += classes[0] + "," + classes[1] + ",";
            }
            stored = stored.substring(0, stored.length() - 1);
            writeToFile(stored);
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).commit();
        } else {
            String date = "";
            String name = "";
            ArrayList<String[]> schedule = null;
            if (shouldRetrieve()) {
                try {
                    output = new Retriever().execute("https://ion.tjhsst.edu").get();
                } catch (InterruptedException | ExecutionException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                date = (String) output[0];
                name = (String) output[1];
                schedule = (ArrayList<String[]>) output[2];
                String stored = date + "," + name + ",";
                for (String[] classes : schedule) {
                    stored += classes[0] + "-" + classes[1] + ",";
                }
                stored = stored.substring(0, stored.length() - 1);
                writeToFile(stored);
            } else {
                String data = readFromFile();
                String[] dataArr = data.split(",");
                date = dataArr[0];
                name = dataArr[1];
                schedule = new ArrayList<String[]>();
                for (int i = 2; i < dataArr.length; i++) {
                    String[] forreal = dataArr[i].split("-");
                    String[] array = {forreal[0], forreal[1]};
                    schedule.add(array);
                }
            }
            setScheduleDate(date);
            display(date, name, schedule);

            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).commit();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        String date = (String) output[0];
        String name = (String) output[1];
        ArrayList<String[]> schedule = (ArrayList<String[]>) output[2];
        setScheduleDate(date);
        display(date, name, schedule);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    openFileOutput("data.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void setScheduleDate(String date) {
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
        schedYear = year;
        schedMonth = month;
        schedDay = day;
        return;
    }

    public void switchDays(int id) {
        if (id == -1) {

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, schedYear);
            cal.set(Calendar.MONTH, schedMonth);
            cal.set(Calendar.DAY_OF_MONTH, schedDay);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
                cal.add(Calendar.DAY_OF_YEAR, -3);
            else
                cal.add(Calendar.DAY_OF_YEAR, -1);

            String parameter = dateFormat.format(cal.getTime());
            String date = "";
            String name = "";
            ArrayList<String[]> schedule = null;
            output = null;
            try {
                output = new Retriever().execute("https://ion.tjhsst.edu/?date=" + parameter).get();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            date = (String) output[0];
            name = (String) output[1];
            schedule = (ArrayList<String[]>) output[2];
            setScheduleDate(date);
            display(date, name, schedule);

        } else {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, schedYear);
            cal.set(Calendar.MONTH, schedMonth);
            cal.set(Calendar.DAY_OF_MONTH, schedDay);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
                cal.add(Calendar.DAY_OF_YEAR, 3);
            else
                cal.add(Calendar.DAY_OF_YEAR, 1);

            String parameter = dateFormat.format(cal.getTime());
            String date = "";
            String name = "";
            ArrayList<String[]> schedule = null;
            output = null;
            try {
                output = new Retriever().execute("https://ion.tjhsst.edu/?date=" + parameter).get();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            date = (String) output[0];
            name = (String) output[1];
            schedule = (ArrayList<String[]>) output[2];
            setScheduleDate(date);
            display(date, name, schedule);

        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("data.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File 	not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void display(String day, String name, ArrayList<String[]> schedule) {
        if (((LinearLayout) llV).getChildCount() > 0)
            ((LinearLayout) llV).removeAllViews();
        String currentClass = getCurrentClass(schedule, name);
        Calendar c = Calendar.getInstance();
        TextView dater = new TextView(this);
        dater.setText(day);
        dater.setTextSize(30);
        dater.setGravity(Gravity.CENTER);
        dater.setTextColor(Color.BLACK);
        llV.addView(dater);

        TextView type = new TextView(this);
        type.setText(name);
        type.setTypeface(type.getTypeface(), Typeface.BOLD);
        type.setTextSize(30);
        type.setGravity(Gravity.CENTER);
        if (name.contains("Red"))
            type.setTextColor(Color.RED);
        else if (name.contains("Blue"))
            type.setTextColor(Color.BLUE);
        else if (name.contains("Anchor"))
            type.setTextColor(Color.rgb(0, 153, 0));
        else
            type.setTextColor(Color.BLACK);
        llV.addView(type);

        for (String[] pair : schedule) {
            if (!pair[0].contains("Passing")) {
                if (pair[0] == currentClass && buttonsPressed == 0
                        && day.contains(Integer.toString(c.get(Calendar.DAY_OF_MONTH)))) {
                    TextView label = new TextView(this);
                    String text = "<b>" + (pair[0] + ":") + "   " + "</b>" + pair[1];
                    label.setText(Html.fromHtml(text));
                    label.setTextSize(24);
                    label.setTextColor(Color.RED);
                    label.setGravity(Gravity.CENTER);
                    label.setPadding(10, 15, 10, 15);
                    llV.addView(label);
                } else {
                    TextView label1 = new TextView(this);
                    String text = "<b>" + (pair[0] + ":") + "   " + "</b>" + pair[1];
                    label1.setText(Html.fromHtml(text));
                    label1.setTextSize(24);
                    label1.setTextColor(Color.BLACK);
                    label1.setGravity(Gravity.CENTER);
                    label1.setPadding(10, 15, 10, 15);
                    llV.addView(label1);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_jump:
                showDialog(DATE_DIALOG_ID);
                return true;
            case R.id.settings:
                String date = "";
                String name = "";
                ArrayList<String[]> schedule = null;
                Object[] output = null;
                try {
                    output = new Retriever().execute("https://ion.tjhsst.edu").get();
                } catch (InterruptedException | ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                date = (String) output[0];
                name = (String) output[1];
                schedule = (ArrayList<String[]>) output[2];
                setScheduleDate(date);
                display(date, name, schedule);

                String stored = date + "," + name + ",";
                for (String[] classes : schedule) {
                    stored += classes[0] + "," + classes[1] + ",";
                }
                stored = stored.substring(0, stored.length() - 1);
                writeToFile(stored);
                Intent intent1 = new Intent(MainActivity.this, TimerActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.pull_in_up, R.anim.push_out_down);
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.timer:
                String date2 = "";
                String name2 = "";
                ArrayList<String[]> schedule2 = null;
                Object[] output2 = null;
                try {
                    output2 = new Retriever().execute("https://ion.tjhsst.edu").get();
                } catch (InterruptedException | ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                date2 = (String) output2[0];
                name2 = (String) output2[1];
                schedule2 = (ArrayList<String[]>) output2[2];
                setScheduleDate(date2);
                display(date2, name2, schedule2);

                String stored2 = date2 + "," + name2 + ",";
                for (String[] classes : schedule2) {
                    stored2 += classes[0] + "," + classes[1] + ",";
                }
                stored2 = stored2.substring(0, stored2.length() - 1);
                writeToFile(stored2);
                Intent intent2 = new Intent(MainActivity.this, TimerActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.pull_in_up, R.anim.push_out_down);
            case R.id.refresh:
                String date1 = "";
                String name1 = "";
                ArrayList<String[]> schedule1 = null;
                Object[] output1 = null;
                try {
                    output1 = new Retriever().execute("https://ion.tjhsst.edu").get();
                } catch (InterruptedException | ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                date1 = (String) output1[0];
                name1 = (String) output1[1];
                schedule1 = (ArrayList<String[]>) output1[2];
                setScheduleDate(date1);
                display(date1, name1, schedule1);
                String stored1 = date1 + "," + name1 + ",";
                for (String[] classes : schedule1) {
                    stored1 += classes[0] + "," + classes[1] + ",";
                    stored1 = stored1.substring(0, stored1.length() - 1);
                    writeToFile(stored1);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, schedYear, schedMonth, schedDay);
        }
        return null;
    }
}

class Retriever extends AsyncTask<String, Void, Object[]> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Object[] doInBackground(String... website) {
        try {

            Document doc = Jsoup.connect(website[0]).get();
            String str = doc.toString();
            if (str.contains("No schedule available")) {
                String name = "No schedule available";
                String date = doc.getElementsByClass("schedule-date").get(0).text();
                ArrayList<String[]> ars = new ArrayList<String[]>();
                Object[] output = {date, name, ars};
                return output;
            } else {
                Elements els = doc.getElementsByClass("block");
                els.addAll(doc.getElementsByClass("times"));
                String date = doc.getElementsByClass("schedule-date").get(0).text();
                String name = doc.getElementsByClass("day-name").get(0).text();
                List<Element> els2 = els.subList(els.size() / 2, els.size());
                List<Element> els1 = els.subList(0, els.size() / 2);
                ArrayList<String[]> schedule = new ArrayList<String[]>();
                for (int i = 0; i < els1.size(); i++) {
                    if (!els1.get(i).text().substring(0, els1.get(i).text().length() - 1).contains("Passing")) {
                        String[] array = {els1.get(i).text().substring(0, els1.get(i).text().length() - 1),
                                els2.get(i).text()};
                        schedule.add(array);
                    }
                }
                Object[] output = {date, name, schedule};
                return output;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Boolean result) {

    }
}
