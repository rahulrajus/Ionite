package com.aneesh.ionite;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;

public class UnCaughtException implements UncaughtExceptionHandler {

	private Context context;
	private static Context context1;

	public UnCaughtException(Context ctx) {
		context = ctx;
		context1 = ctx;
	}
	
	public void uncaughtException(Thread t, Throwable e) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				builder.setTitle("No Connection!");
				builder.create();
				builder.setCancelable(false);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.exit(0);
					}
				});
				builder.setMessage("You are not connected to the internet. Connect to a cellular network or WiFi.");
				builder.show();
				Looper.loop();
			}
		}.start();
	}
}