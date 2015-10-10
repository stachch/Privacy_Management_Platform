/*
 * Copyright 2012 pmp-android development team
 * Project: PMP
 * Project-Site: https://github.com/stachch/Privacy_Management_Platform
 *
 * ---------------------------------------------------------------------
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.unistuttgart.ipvs.pmp.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import de.unistuttgart.ipvs.pmp.service.PMPService;

/**
 * Util class for restarting the pmp after a dex code remove to be prepared for injecting a new code version.
 * 
 * @author Jakob Jarosch
 * 
 */
public class Restarter {
    
    public static final String RESTARTER_IDENTIFIER = "scheduledByRestarter";
    
    
    /**
     * Kills the referenced {@link Application} and restarts the {@link Activity}.
     * 
     * @param activity
     *            Activity which should be restarted.
     */
    public static final void killAppAndRestartActivity(Activity activity) {
        Intent i = new Intent(activity.getIntent());
        PendingIntent pi = PendingIntent.getActivity(activity.getBaseContext(), 0, i, activity.getIntent().getFlags());
        AlarmManager am = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + 1000L, pi);
        System.exit(0);
    }
    
    
    /**
     * Restarts the given service in <code>in</code> time.
     */
    public static void scheduleServiceRestart(Context context, long in) {
        Intent intent = new Intent(context, PMPService.class);
        intent.putExtra(RESTARTER_IDENTIFIER, true);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + in, pi);
    }
}
