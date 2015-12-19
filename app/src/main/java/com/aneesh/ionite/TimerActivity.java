/**
 *
 */
package com.aneesh.ionite;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @author Aneesh
 */
public class TimerActivity extends Activity {

	private static final String FORMAT = "%02d:%02d:%02d";
	public static ArrayList<String[]> schedule;
	public static String currentClass = "";
	public static long timeLeft;
	TextView timeUntil;
	TextView otherOne;
	int seconds, minutes;
	String realday = "1";
	String schedday = "0";
	public static boolean display = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.timer);

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		schedule = (ArrayList<String[]>) MainActivity.output[2];
		String lastEndTime = "";
		for (int i = 0; i < schedule.size(); i++) {
			String startTime = schedule.get(i)[1].split(" - ")[0];
			if (!lastEndTime.equals("") && !lastEndTime.equals(startTime)) {
				String[] kash = { "Passing Time - " + schedule.get(i)[0], lastEndTime + " - " + startTime };
				schedule.add(i, kash);
			}
			lastEndTime = schedule.get(i)[1].split(" - ")[1];
		}
		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(TimerActivity.this);
		display = SP.getBoolean("DispOp", true);
		timeUntil = (TextView) findViewById(R.id.textView1);
		otherOne = (TextView) findViewById(R.id.textView);
		timeUntil.setGravity(Gravity.CENTER);
		otherOne.setGravity(Gravity.CENTER);
		currentClass = MainActivity.getCurrentClass(schedule, (String) MainActivity.output[1]);
		if (!currentClass.equals("NONE")) {
			for (String[] thingy : schedule) {
				if (thingy[0].equals(currentClass)) {
					String timing = thingy[1];
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
					myCal.set(Calendar.HOUR_OF_DAY, endTimeHour);
					myCal.set(Calendar.MINUTE, endTimeMin);
					myCal.set(Calendar.SECOND, 0);
					myCal.set(Calendar.MILLISECOND, 0);
					timeLeft = myCal.getTimeInMillis() - System.currentTimeMillis();
					break;
				}
			}
			Calendar myCal = Calendar.getInstance();
			realday = "" + myCal.get(Calendar.DAY_OF_MONTH) + myCal.get(Calendar.MONTH);
			int[] arr = setScheduleDate((String) MainActivity.output[0]);
			schedday = "" + arr[1] + arr[0];
		}
		java.util.Date date = new java.util.Date();

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

		if ((!currentClass.equals("NONE") && !currentClass.equals(""))
				&& (realday.equals(schedday) && (now.after(startSchool) && now.before(endSchool)))) {
			new CountDownTimer(timeLeft, 1000) { // adjust the milli seconds
				// here

				public void onTick(long millisUntilFinished) {
					if (currentClass.contains("Passing Time")) {
						String classAboutToEnd = currentClass.split(" - ")[1];
						otherOne.setText(classAboutToEnd + " will start in:");
					} else {
						otherOne.setText(currentClass + " will end in:");
					}
					String unformatted = ("" + String.format(FORMAT, TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
							TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
									- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
							TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES
									.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
					if (display == false) {
						String[] parts = unformatted.split(":");
						String thingy = "";
						for (int i = 0; i < parts.length; i++) {
							if (parts[i].equals("00"))
								continue;
							else {
								if (i == 0)
									thingy += parts[0] + " hours" + "\n";
								else if (i == 1)
									thingy += parts[1] + " minutes" + "\n";
								else
									thingy += parts[2] + " seconds" + "\n";
							}
						}
						timeUntil.setText(thingy);
					} else {
						timeUntil.setText(unformatted);
					}
				}

				public void onFinish() {
					otherOne.setText(currentClass);
					timeUntil.setText("is over!");
				}
			}.start();
		} else {
			Calendar myCal = Calendar.getInstance();
			realday = "" + myCal.get(Calendar.DAY_OF_MONTH) + myCal.get(Calendar.MONTH);
			int[] arr = setScheduleDate((String) MainActivity.output[0]);
			schedday = "" + arr[1] + arr[0];
			java.util.Date date1 = new java.util.Date();

			Time now1 = new Time(date1.getTime());

			// Time now = new Time(cal1.getTime().getTime());

			Calendar cal1 = Calendar.getInstance();
			cal1.set(Calendar.HOUR_OF_DAY, 8);
			cal1.set(Calendar.MINUTE, 40);
			cal1.set(Calendar.SECOND, 00);

			Time startSchool1 = new Time(cal1.getTime().getTime());

			cal1.set(Calendar.HOUR_OF_DAY, 16);
			cal1.set(Calendar.MINUTE, 00);
			cal1.set(Calendar.SECOND, 00);

			Time endSchool1 = new Time(cal1.getTime().getTime());
			if (realday.equals(schedday) && now.after(endSchool1)) {
				otherOne.setText("School has");
				timeUntil.setText("ended!");
			} else {
				otherOne.setText("School hasn't");
				timeUntil.setText("started yet!");
			}
		}
	}

	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.pull_in_down, R.anim.push_out_up);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
		int[] arr = { month, day };
		return arr;
	}

}
