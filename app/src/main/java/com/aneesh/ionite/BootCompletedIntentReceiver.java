package com.aneesh.ionite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, DayCycle.class);
            pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(pushIntent);
            Intent pushIntent2 = new Intent(context, AlarmCreator.class);
            pushIntent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(pushIntent2);
        }
    }
}