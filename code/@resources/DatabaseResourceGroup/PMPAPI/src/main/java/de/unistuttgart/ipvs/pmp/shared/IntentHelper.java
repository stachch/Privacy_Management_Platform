package de.unistuttgart.ipvs.pmp.shared;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public final class IntentHelper {
    
    /***
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
     *
     * If you are using an implicit intent, and know only 1 target would answer this intent,
     * This method will help you turn the implicit intent into the explicit form.
     *
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
     * 
     * @param context
     * @param implicitIntent
     *            - The original implicit intent
     * @return Explicit Intent created from the implicit original intent
     */
    public static void upgradeIntent(Context context, Intent service) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(service, 0);
        
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return;
        }
        
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        
        // Set the component to be explicit
        service.setComponent(component);
        
    }
    
}
