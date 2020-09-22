package com.example.kdm.water.UiUtilities;


import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.kdm.water.db.db_water.WaterRepo;
import com.example.kdm.water.db.db_water.model.DailyLog;
import com.example.kdm.water.db.pref.PrefUserDetails;

public class UtilMethods {

    public static void makeDateChanges(
            @NonNull SharedPreferences prefBasicInfo, @NonNull WaterRepo repo) {
        /* A function to check weather the current date(i.e the date at which this method is called)
         * is equal to 'savedDate' attribute inside prefUserDetails. if True, it won't do anything.
         * If false, it will:
         *     1. add old date's log to logs table
         *     2. reset date and achieved qty in preferences
         *     3. clear allitems in today entries table table
         *
         * */
        boolean showLogs = prefBasicInfo.getBoolean(PrefUserDetails.KEYS.KEY_SHOW_LOGS, PrefUserDetails.Defaults.SHOW_LOGS);
        if (showLogs) {
            String dateFromPref = prefBasicInfo.getString(PrefUserDetails.KEYS.KEY_DATE_TODAY, PrefUserDetails.Defaults.DATE_TODAY);
            String today = TimeUtilities.getCurrentDate();
            if (!dateFromPref.equals(today)) {
                int achievedQty = prefBasicInfo.getInt(PrefUserDetails.KEYS.KEY_TODAY_INTAKE_ACHIEVED, PrefUserDetails.Defaults.TODAY_INTAKE_ACHIEVED);
                int targetQty = prefBasicInfo.getInt(PrefUserDetails.KEYS.KEY_DAILY_TARGET, PrefUserDetails.Defaults.DAILY_TARGET);

                DailyLog log = new DailyLog();
                log.setDate(dateFromPref);
                log.setTarget(targetQty);
                log.setAchieved(achievedQty);

                //1
                if (!repo.checkIfLogExists(log)) {
                    repo.addNewDailyLog(log);
                }

                //2.resetted date and intake to today,0
                prefBasicInfo.edit()
                        .putString(
                                PrefUserDetails.KEYS.KEY_DATE_TODAY, PrefUserDetails.Defaults.DATE_TODAY)
                        .putInt(
                                PrefUserDetails.KEYS.KEY_TODAY_INTAKE_ACHIEVED,
                                PrefUserDetails.Defaults.TODAY_INTAKE_ACHIEVED
                        )
                        .apply();

                //3
                repo.removeAllTodayEntries();
            }
        }


    }


}