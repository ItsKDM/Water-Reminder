package com.example.kdm.water.services;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.kdm.water.R;
import com.example.kdm.water.UiUtilities.NotifMaker;
import com.example.kdm.water.UiUtilities.TimeUtilities;
import com.example.kdm.water.broadcast_recievers.NotificationActionReceiver;
import com.example.kdm.water.db.pref.PrefUserDetails;

import java.util.Locale;


public class PeriodicNotificationService extends Worker {
    public PeriodicNotificationService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {


        Context ctx = getApplicationContext();

        SharedPreferences pref = ctx.getSharedPreferences(PrefUserDetails.PREF_NAME, Context.MODE_PRIVATE);
        boolean showDailyNotifs = pref.getBoolean(PrefUserDetails.KEYS.KEY_SHOW_NOTIFS, PrefUserDetails.Defaults.SHOW_NOTIF);

        String sleepTime = pref.getString(PrefUserDetails.KEYS.KEY_SLEEP_TIME, PrefUserDetails.Defaults.SLEEP_TIME);
        String wakeupTime = pref.getString(PrefUserDetails.KEYS.KEY_WAKEUP_TIME, PrefUserDetails.Defaults.WAKEUP_TIME);
        String currentTime = TimeUtilities.getCurrentTime();
        boolean isBetweenSleep = TimeUtilities.isTimeInBetween2Times(sleepTime, wakeupTime, currentTime);

        if (showDailyNotifs && !isBetweenSleep) {
            createNotification(pref, ctx);

        }

        return Result.success();

    }

    private void createNotification(SharedPreferences pref, Context ctx) {
        String date = pref.getString(PrefUserDetails.KEYS.KEY_DATE_TODAY, PrefUserDetails.Defaults.DATE_TODAY);
        int achieved = pref.getInt(PrefUserDetails.KEYS.KEY_TODAY_INTAKE_ACHIEVED, PrefUserDetails.Defaults.TODAY_INTAKE_ACHIEVED);
        int target = pref.getInt(PrefUserDetails.KEYS.KEY_DAILY_TARGET, PrefUserDetails.Defaults.DAILY_TARGET);


        String title = "Hey! Drink some Water!";

        String details = String.format(Locale.ROOT, "%s Today's Progress:%d/%d ml. ", date, achieved, target);
        details += "\n Tap To Add 150 ml water to your daily Progress.";
        int resId = R.drawable.ic_notif_icon;

        //creating actions
        Intent i = new Intent(getApplicationContext(), NotificationActionReceiver.class);
        i.putExtra(NotificationActionReceiver.KEY_ACTION_I_DRANK_WATER, true);
        PendingIntent piDrankWater = PendingIntent.getBroadcast(
                ctx, NotificationActionReceiver.RECEIVER_RQ_CODE1, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action1 = new NotificationCompat.Action(
                R.drawable.ic_logo, "Add 150 ml", piDrankWater);


        Intent i2 = new Intent(getApplicationContext(), NotificationActionReceiver.class);
        i2.putExtra(NotificationActionReceiver.KEY_ACTION_STOP_NOTIFICATIONS_FOR_TODAY, true);
        PendingIntent piStopTodayNotifs = PendingIntent.getBroadcast(
                ctx, NotificationActionReceiver.RECEIVER_RQ_CODE2, i2, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action2 = new NotificationCompat.Action(
                R.drawable.ic_notifications_off, "Stop Notifications", piStopTodayNotifs);


        NotifMaker notifMaker = new NotifMaker(resId, ctx, title, details);
        notifMaker.setActionBtn1(action1);
        notifMaker.setActionBtn2(action2);
        notifMaker.show();
    }


}