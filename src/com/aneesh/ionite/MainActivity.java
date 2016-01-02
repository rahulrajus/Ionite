package com.aneesh.ionite;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

	static final int DATE_DIALOG_ID = 999;
	public static Object[] output = null;
	public static boolean retrieved = false;
	public static boolean git = false;
	public static LinearLayout which1 = null;
	public static Date startYear = null;
	public static Date endYear = null;
	// static
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	static int buttonsPressed = 0;
	static int schedYear;
	static int schedMonth;
	static int schedDay;
	public static AnimatorSet setRightOut = null;
	public static AnimatorSet setLeftIn = null;
	public static boolean APP_EXIT_FLAG = false;
	public static int realYear = 0;

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, selectedYear);
			cal.set(Calendar.MONTH, selectedMonth);
			cal.set(Calendar.DAY_OF_MONTH, selectedDay);
			Date chosen = new GregorianCalendar(selectedYear, selectedMonth, selectedDay).getTime();
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				Calendar cal1 = Calendar.getInstance();
				cal1.set(Calendar.YEAR, selectedYear);
				cal1.set(Calendar.MONTH, selectedMonth);
				cal1.set(Calendar.DAY_OF_MONTH, selectedDay);
				while (cal1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
						|| cal1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					cal1.add(Calendar.DAY_OF_MONTH, 1);
				}
				selectedYear = cal1.get(Calendar.YEAR);
				selectedMonth = cal1.get(Calendar.MONTH);
				selectedMonth++;
				selectedDay = cal1.get(Calendar.DAY_OF_MONTH);
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
				if (name.equalsIgnoreCase("No Internet Connection")) {
					Intent i = new Intent(getBaseContext(), InternetActivity.class);
					startActivity(i);
				} else {
					schedule = (ArrayList<String[]>) output[2];
					if (date.split("[\\s,\\.]+").length > 2)
						setScheduleDate(date);
					else {
						schedYear = selectedYear;
						schedMonth = selectedMonth;
						schedDay = selectedDay;
					}

					LinearLayout lay = ((LinearLayout) findViewById(R.id.root));
					LinearLayout lay1 = ((LinearLayout) findViewById(R.id.root));
					lay1.removeAllViews();
					LinearLayout lay2 = ((LinearLayout) findViewById(R.id.root1));
					lay2.removeAllViews();
					lay.setVisibility(View.INVISIBLE);
					display((LinearLayout) findViewById(R.id.root), date, name, schedule, 1);
				}
			} else if (chosen.before(startYear) || chosen.after(endYear)) {
				if (chosen.before(startYear)) {
					Calendar cal1 = Calendar.getInstance();
					cal1.setTime(startYear);
					selectedYear = cal1.get(Calendar.YEAR);
					selectedMonth = cal1.get(Calendar.MONTH);
					selectedMonth++;
					selectedDay = cal1.get(Calendar.DAY_OF_MONTH);
				} else 
				{
					Calendar cal1 = Calendar.getInstance();
					cal1.setTime(endYear);
					selectedYear = cal1.get(Calendar.YEAR);
					selectedMonth = cal1.get(Calendar.MONTH);
					selectedMonth++;
					selectedDay = cal1.get(Calendar.DAY_OF_MONTH);
				}
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
				if (name.equalsIgnoreCase("No Internet Connection")) {
					Intent i = new Intent(getBaseContext(), InternetActivity.class);
					startActivity(i);
				} else {
					schedule = (ArrayList<String[]>) output[2];
					if (date.split("[\\s,\\.]+").length > 2)
						setScheduleDate(date);
					else {
						schedYear = selectedYear;
						schedMonth = selectedMonth;
						schedDay = selectedDay;
					}

					LinearLayout lay = ((LinearLayout) findViewById(R.id.root));
					LinearLayout lay1 = ((LinearLayout) findViewById(R.id.root));
					lay1.removeAllViews();
					LinearLayout lay2 = ((LinearLayout) findViewById(R.id.root1));
					lay2.removeAllViews();
					lay.setVisibility(View.INVISIBLE);
					display((LinearLayout) findViewById(R.id.root), date, name, schedule, 1);
				}
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
				if (name.equalsIgnoreCase("No Internet Connection")) {
					Intent i = new Intent(getBaseContext(), InternetActivity.class);
					startActivity(i);
				} else {
					schedule = (ArrayList<String[]>) output[2];
					if (date.split("[\\s,\\.]+").length > 2)
						setScheduleDate(date);
					else {
						schedYear = selectedYear;
						schedMonth = selectedMonth;
						schedDay = selectedDay;
					}

					LinearLayout lay = ((LinearLayout) findViewById(R.id.root));
					LinearLayout lay1 = ((LinearLayout) findViewById(R.id.root));
					lay1.removeAllViews();
					LinearLayout lay2 = ((LinearLayout) findViewById(R.id.root1));
					lay2.removeAllViews();
					lay.setVisibility(View.INVISIBLE);
					display((LinearLayout) findViewById(R.id.root), date, name, schedule, 1);
				}
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

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static boolean isAirplaneModeOn(Context context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
		} else {
			return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
		}
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
				|| schedDay != cal.get(Calendar.DAY_OF_MONTH) || dayname.contains("No schedule available")
				|| dayname.contains("No school") || dayname.contains("Summer")) {
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
		Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(MainActivity.this));
		startYear = new GregorianCalendar(2015, Calendar.SEPTEMBER, 8).getTime();
		endYear = new GregorianCalendar(2016, Calendar.JUNE, 23).getTime();
		Intent pushIntent = new Intent(this, com.aneesh.ionite.DayCycle.class);
		if (isMyServiceRunning(DayCycle.class))
			stopService(pushIntent);
		startService(pushIntent);
		Intent pushIntent2 = new Intent(this, com.aneesh.ionite.AlarmCreator.class);
		startService(pushIntent2);
		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		git = SP.getBoolean("animationSelector", true);
		// Setup stuff
		final LinearLayout root = (LinearLayout) findViewById(R.id.root);
		final ScrollView scroll = (ScrollView) findViewById(R.id.scroller);
		final LinearLayout root1 = (LinearLayout) findViewById(R.id.root1);
		final ScrollView scroll1 = (ScrollView) findViewById(R.id.scroller1);
		RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.mainLayout);

		rlayout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {

			public void onSwipeLeft() {
				if (git == true) {
					if (((LinearLayout) findViewById(R.id.root1)).getChildCount() < 1) {
						int i = switchDays((LinearLayout) findViewById(R.id.root1), 1);
						Log.e("Result", "" + i);
						if (i != -1) {
							scroll1.startAnimation(inFromRightAnimation());
							scroll1.postDelayed(new Runnable() {
								@Override
								public void run() {
									((LinearLayout) findViewById(R.id.root)).removeAllViews();
								}
							}, 300);
							scroll.startAnimation(outToLeftAnimation());
							scroll.postDelayed(new Runnable() {
								@Override
								public void run() {
									((LinearLayout) findViewById(R.id.root)).removeAllViews();
								}
							}, 300);
							//
						}
					} else {
						int i = switchDays((LinearLayout) findViewById(R.id.root), 1);
						Log.e("Result", "" + i);
						if (i != -1) {
							scroll.startAnimation(inFromRightAnimation());
							scroll.postDelayed(new Runnable() {

								@Override
								public void run() {
									((LinearLayout) findViewById(R.id.root1)).removeAllViews();
								}
							}, 300);
							scroll1.startAnimation(outToLeftAnimation());
							scroll1.postDelayed(new Runnable() {
								@Override
								public void run() {
									((LinearLayout) findViewById(R.id.root1)).removeAllViews();
								}
							}, 300);
						}
						// ((LinearLayout)
						// findViewById(R.id.root1)).removeAllViews();
					}

				} else {
					switchDays((LinearLayout) findViewById(R.id.root), 1);
				}
			}

			public void onSwipeRight() {
				if (git == true) {
					if (((LinearLayout) findViewById(R.id.root1)).getChildCount() < 1) {
						int i = switchDays((LinearLayout) findViewById(R.id.root1), -1);
						Log.e("Result", "" + i);
						if (i != -1) {
							scroll.startAnimation(outToRightAnimation());
							scroll.postDelayed(new Runnable() {
								@Override
								public void run() {
									((LinearLayout) findViewById(R.id.root)).removeAllViews();
								}
							}, 300);
							scroll1.startAnimation(inFromLeftAnimation());
							scroll1.postDelayed(new Runnable() {
								@Override
								public void run() {
									((LinearLayout) findViewById(R.id.root)).removeAllViews();
								}
							}, 300);
						}
						// ((LinearLayout)
						// findViewById(R.id.root)).removeAllViews();
					} else {
						int i = switchDays((LinearLayout) findViewById(R.id.root), -1);
						Log.e("Result", "" + i);
						if (i != -1) {
							scroll1.startAnimation(outToRightAnimation());
							scroll1.postDelayed(new Runnable() {

								@Override
								public void run() {
									((LinearLayout) findViewById(R.id.root1)).removeAllViews();
								}
							}, 300);
							scroll.startAnimation(inFromLeftAnimation());
							scroll.postDelayed(new Runnable() {
								@Override
								public void run() {
									((LinearLayout) findViewById(R.id.root1)).removeAllViews();
								}
							}, 300);
						}
						// ((LinearLayout)
						// findViewById(R.id.root1)).removeAllViews();
					}

				} else {
					switchDays((LinearLayout) findViewById(R.id.root), -1);
				}
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

		scroll.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
			public void onSwipeLeft() {
				if (git) {
					int i = switchDays((LinearLayout) findViewById(R.id.root1), 1);
					Log.e("Result", "" + i);
					if (i != -1) {
						scroll1.startAnimation(inFromRightAnimation());
						scroll1.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root)).removeAllViews();
							}
						}, 300);
						scroll.startAnimation(outToLeftAnimation());
						scroll.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root)).removeAllViews();
							}
						}, 300);
					}
					// ((LinearLayout)
					// findViewById(R.id.root)).removeAllViews();
				} else {
					switchDays((LinearLayout) findViewById(R.id.root), 1);
				}
			}

			public void onSwipeRight() {
				if (git) {
					int i = switchDays((LinearLayout) findViewById(R.id.root1), -1);
					Log.e("Result", "" + i);
					if (i != -1) {
						scroll.startAnimation(outToRightAnimation());
						scroll.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root)).removeAllViews();
							}
						}, 300);
						scroll1.startAnimation(inFromLeftAnimation());
						scroll1.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root)).removeAllViews();
							}
						}, 300);
					}
					// ((LinearLayout)
					// findViewById(R.id.root)).removeAllViews();
				} else {
					switchDays((LinearLayout) findViewById(R.id.root), -1);
				}
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
		root.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
			public void onSwipeLeft() {
				if (git) {
					int i = switchDays((LinearLayout) findViewById(R.id.root1), 1);
					Log.e("Result", "" + i);
					if (i != -1) {
						scroll1.startAnimation(inFromRightAnimation());
						scroll1.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root)).removeAllViews();
							}
						}, 300);
						scroll.startAnimation(outToLeftAnimation());
						scroll.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root)).removeAllViews();
							}
						}, 300);
					}
					// ((LinearLayout)
					// findViewById(R.id.root)).removeAllViews();
				} else {
					switchDays((LinearLayout) findViewById(R.id.root), 1);
				}
			}

			public void onSwipeRight() {
				if (git) {
					int i = switchDays((LinearLayout) findViewById(R.id.root1), -1);
					Log.e("Result", "" + i);
					if (i != -1) {
						scroll.startAnimation(outToRightAnimation());
						scroll.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root)).removeAllViews();
							}
						}, 300);
						scroll1.startAnimation(inFromLeftAnimation());
						scroll1.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root)).removeAllViews();
							}
						}, 300);
					}
					// ((LinearLayout)
					// findViewById(R.id.root)).removeAllViews();
				} else {
					switchDays((LinearLayout) findViewById(R.id.root), -1);
				}
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
		scroll1.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
			public void onSwipeLeft() {
				if (git) {
					int i = switchDays((LinearLayout) findViewById(R.id.root), 1);
					Log.e("Result", "" + i);
					if (i != -1) {
						scroll.startAnimation(inFromRightAnimation());
						scroll.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root1)).removeAllViews();
							}
						}, 300);
						scroll1.startAnimation(outToLeftAnimation());
						scroll1.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root1)).removeAllViews();
							}
						}, 300);
					}
					// ((LinearLayout)
					// findViewById(R.id.root1)).removeAllViews();
				} else {
					switchDays((LinearLayout) findViewById(R.id.root), 1);
				}
			}

			public void onSwipeRight() {
				if (git) {
					int i = switchDays((LinearLayout) findViewById(R.id.root), -1);
					Log.e("Result", "" + i);
					if (i != -1) {
						scroll1.startAnimation(outToRightAnimation());
						scroll1.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root1)).removeAllViews();
							}
						}, 300);
						scroll.startAnimation(inFromLeftAnimation());
						scroll.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root1)).removeAllViews();
							}
						}, 300);
					}
					// ((LinearLayout)
					// findViewById(R.id.root1)).removeAllViews();
				} else {
					switchDays((LinearLayout) findViewById(R.id.root), -1);
				}
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
		root1.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
			public void onSwipeLeft() {
				if (git) {
					int i = switchDays((LinearLayout) findViewById(R.id.root), 1);
					Log.e("Result", "" + i);
					if (i != -1) {
						scroll.startAnimation(inFromRightAnimation());
						scroll.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root1)).removeAllViews();
							}
						}, 300);
						scroll1.startAnimation(outToLeftAnimation());
						scroll1.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root1)).removeAllViews();
							}
						}, 300);
					}
					// ((LinearLayout)
					// findViewById(R.id.root1)).removeAllViews();
				} else {
					switchDays((LinearLayout) findViewById(R.id.root), 1);
				}
			}

			public void onSwipeRight() {
				if (git) {
					int i = switchDays((LinearLayout) findViewById(R.id.root), -1);
					Log.e("Result", "" + i);
					if (i != -1) {
						scroll1.startAnimation(outToRightAnimation());
						scroll1.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root1)).removeAllViews();
							}
						}, 300);
						scroll.startAnimation(inFromLeftAnimation());
						scroll.postDelayed(new Runnable() {
							@Override
							public void run() {
								((LinearLayout) findViewById(R.id.root1)).removeAllViews();
							}
						}, 300);
					}
					// ((LinearLayout)
					// findViewById(R.id.root1)).removeAllViews();
				} else {
					switchDays((LinearLayout) findViewById(R.id.root), -1);
				}
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

		File file = new File("data.txt");
		// Check for first run or upgrade
		String date = "";
		String name = "";
		ArrayList<String[]> schedule = null;
		try {
			output = new Retriever().execute("https://ion.tjhsst.edu").get();
		} catch (InterruptedException | ExecutionException e2) {
			e2.printStackTrace();
		}
		date = (String) output[0];
		name = (String) output[1];
		schedule = (ArrayList<String[]>) (output[2]);
		if (name.equalsIgnoreCase("No Internet Connection")) {
			Intent i = new Intent(MainActivity.this, InternetActivity.class);
			startActivity(i);
			Log.e("Info", "No Internet");
		} else {
			Log.e("Info", "Have Internet");
			setScheduleDate(date);
			LinearLayout lay = ((LinearLayout) findViewById(R.id.root));
			lay.setVisibility(View.INVISIBLE);
			display(root, date, name, schedule, 1);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		String date = "";
		String name = "";
		ArrayList<String[]> schedule = null;
		output = null;
		try {
			output = new Retriever().execute("https://ion.tjhsst.edu/").get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		date = (String) output[0];
		name = (String) output[1];
		if (name.equalsIgnoreCase("No Internet Connection")) {
			Intent i = new Intent(getBaseContext(), InternetActivity.class);
			startActivity(i);
		} else {
			schedule = (ArrayList<String[]>) output[2];
			setScheduleDate(date);
			display(((LinearLayout) findViewById(R.id.root)), date, name, schedule, -1);
		}
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

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
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
		String[] stuff = date.split("[\\s,\\.]+");
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
		schedYear = realYear;
		schedMonth = month;
		schedDay = day;
		return;
	}

	public int switchDays(LinearLayout which, int id) {
		if (id == -1) {

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, schedYear);
			cal.set(Calendar.MONTH, schedMonth);
			cal.set(Calendar.DAY_OF_MONTH, schedDay);
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
				cal.add(Calendar.DAY_OF_YEAR, -3);
			else
				cal.add(Calendar.DAY_OF_YEAR, -1);

			Date chosen = cal.getTime();

			if (chosen.before(startYear))
				return -1;

			String parameter = dateFormat.format(cal.getTime());
			String date = "";
			String name = "";
			ArrayList<String[]> schedule = null;
			output = null;
			try {
				output = new Retriever().execute("https://ion.tjhsst.edu/?date=" + parameter).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			date = (String) output[0];
			name = (String) output[1];
			if (name.equalsIgnoreCase("No Internet Connection")) {
				Intent i = new Intent(getBaseContext(), InternetActivity.class);
				startActivity(i);
			} else {
				schedule = (ArrayList<String[]>) output[2];
				setScheduleDate(date);
				display(which, date, name, schedule, -1);
			}
		} else {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, schedYear);
			cal.set(Calendar.MONTH, schedMonth);
			cal.set(Calendar.DAY_OF_MONTH, schedDay);
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
				cal.add(Calendar.DAY_OF_YEAR, 3);
			else
				cal.add(Calendar.DAY_OF_YEAR, 1);

			Date chosen = cal.getTime();

			if (chosen.after(endYear))
				return -1;

			String parameter = dateFormat.format(cal.getTime());
			String date = "";
			String name = "";
			ArrayList<String[]> schedule = null;
			output = null;
			try {
				output = new Retriever().execute("https://ion.tjhsst.edu/?date=" + parameter).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			date = (String) output[0];
			name = (String) output[1];
			if (name.equalsIgnoreCase("No Internet Connection")) {
				Intent i = new Intent(getBaseContext(), InternetActivity.class);
				startActivity(i);
			} else {
				schedule = (ArrayList<String[]>) output[2];
				setScheduleDate(date);
				display(which, date, name, schedule, -1);
			}
		}
		return 1;
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

	private Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
				0.0f);
		inFromRight.setDuration(300);
		inFromRight.setInterpolator(new OvershootInterpolator());
		return inFromRight;
	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
				-1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(300);
		outtoLeft.setInterpolator(new OvershootInterpolator());
		return outtoLeft;
	}

	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT,
				0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(300);
		inFromLeft.setInterpolator(new OvershootInterpolator());
		return inFromLeft;
	}

	private Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
				+1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(300);
		outtoRight.setInterpolator(new OvershootInterpolator());
		return outtoRight;
	}

	public void display(LinearLayout which, String day, String name, ArrayList<String[]> schedule, int id) {
		which.removeAllViews();
		which1 = which;
		String currentClass = getCurrentClass(schedule, name);
		Calendar c = Calendar.getInstance();
		TextView dater = new TextView(this);
		dater.setText(day);
		dater.setTextSize(30);
		dater.setGravity(Gravity.CENTER);
		dater.setTextColor(Color.BLACK);
		which.addView(dater);

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
		which.addView(type);
		String o = "";
		for (String[] pair : schedule) {
			o += pair[0] + "|" + pair[1] + ", ";
		}
		Log.e("Sched", o);
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
					which.addView(label);
				} else {
					TextView label1 = new TextView(this);
					String text = "<b>" + (pair[0] + ":") + "   " + "</b>" + pair[1];
					label1.setText(Html.fromHtml(text));
					label1.setTextSize(24);
					label1.setTextColor(Color.BLACK);
					label1.setGravity(Gravity.CENTER);
					label1.setPadding(10, 15, 10, 15);
					which.addView(label1);
				}
			}
		}
		if (id == 1) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Animation fadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in_view);
					which1.startAnimation(fadeInAnimation);
					fadeInAnimation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							which1.setVisibility(View.VISIBLE);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub

						}
					});
				}

			}, 300);
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
			output = null;
			try {
				output = new Retriever().execute("https://ion.tjhsst.edu").get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			date = (String) output[0];
			name = (String) output[1];
			if (name.equalsIgnoreCase("No Internet Connection")) {
				Intent i = new Intent(getBaseContext(), InternetActivity.class);
				startActivity(i);
			} else {
				schedule = (ArrayList<String[]>) output[2];
				setScheduleDate(date);
				// display((LinearLayout) findViewById(R.id.root), date, name,
				// schedule, 1);

				String stored = date + "," + name + ",";
				for (String[] classes : schedule) {
					stored += classes[0] + "," + classes[1] + ",";
				}
				stored = stored.substring(0, stored.length() - 1);
				writeToFile(stored);
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
			}
			return true;
		case R.id.timer:
			String date2 = "";
			String name2 = "";
			ArrayList<String[]> schedule2 = null;
			output = null;
			try {
				output = new Retriever().execute("https://ion.tjhsst.edu").get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			date2 = (String) output[0];
			name2 = (String) output[1];
			schedule2 = (ArrayList<String[]>) output[2];
			if (name2.equalsIgnoreCase("No Internet Connection")) {
				Intent i = new Intent(getBaseContext(), InternetActivity.class);
				startActivity(i);
			} else {
				setScheduleDate(date2);
				display((LinearLayout) findViewById(R.id.root), date2, name2, schedule2, -1);

				String stored2 = date2 + "," + name2 + ",";
				for (String[] classes : schedule2) {
					stored2 += classes[0] + "," + classes[1] + ",";
				}
				stored2 = stored2.substring(0, stored2.length() - 1);
				writeToFile(stored2);
				Intent intent2 = new Intent(MainActivity.this, TimerActivity.class);
				startActivity(intent2);
				overridePendingTransition(R.anim.pull_in_up, R.anim.push_out_down);
			}
		case R.id.refresh:
			String date1 = "";
			String name1 = "";
			ArrayList<String[]> schedule1 = null;
			output = null;
			try {
				output = new Retriever().execute("https://ion.tjhsst.edu").get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			date1 = (String) output[0];
			name1 = (String) output[1];
			schedule1 = (ArrayList<String[]>) output[2];
			if (name1.equalsIgnoreCase("No Internet Connection")) {
				Intent i = new Intent(getBaseContext(), InternetActivity.class);
				startActivity(i);
			} else {
				setScheduleDate(date1);
				((LinearLayout) findViewById(R.id.root)).removeAllViews();
				((LinearLayout) findViewById(R.id.root1)).removeAllViews();
				LinearLayout lay = ((LinearLayout) findViewById(R.id.root));
				lay.setVisibility(View.INVISIBLE);
				display((LinearLayout) findViewById(R.id.root), date1, name1, schedule1, 1);
				String stored1 = date1 + "," + name1 + ",";
				for (String[] classes : schedule1) {
					stored1 += classes[0] + "," + classes[1] + ",";
					stored1 = stored1.substring(0, stored1.length() - 1);
					writeToFile(stored1);
				}
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
				Object[] output = { date, name, ars };
				return output;
			} else {
				Elements els = doc.getElementsByClass("block");
				els.addAll(doc.getElementsByClass("times"));
				String date = doc.getElementsByClass("schedule-date").get(0).text();
				Elements div = doc.getElementsByClass("schedule");
				String attr = div.first().attr("data-date");
				int integer = Integer.parseInt(attr.split("-")[0]);
				Log.d("Year", "" + integer);
				MainActivity.realYear = integer;
				String name = doc.getElementsByClass("day-name").get(0).text();
				List<Element> els2 = els.subList(els.size() / 2, els.size());
				List<Element> els1 = els.subList(0, els.size() / 2);
				ArrayList<String[]> schedule = new ArrayList<String[]>();
				for (int i = 0; i < els1.size(); i++) {
					if (!els1.get(i).text().substring(0, els1.get(i).text().length() - 1).contains("Passing")) {
						String[] array = { els1.get(i).text().substring(0, els1.get(i).text().length() - 1),
								els2.get(i).text() };
						schedule.add(array);
					}
				}
				Object[] output = { date, name, schedule };
				return output;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<String[]> schedule = new ArrayList<String[]>();
		Object[] output = { "", "No Internet Connection", schedule };
		return output;
	}

	protected void onPostExecute(Boolean result) {

	}
}
