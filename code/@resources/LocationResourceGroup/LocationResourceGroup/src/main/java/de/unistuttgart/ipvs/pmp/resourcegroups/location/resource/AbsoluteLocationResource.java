/*
 * Copyright 2012 pmp-android development team
 * Project: LocationResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.location.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import de.unistuttgart.ipvs.pmp.resourcegroups.location.LocationResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.resource.Resource;

/**
 * The {@link AbsoluteLocationResource} provides access to the android GPS location module.
 * 
 * @author Jakob Jarosch
 */
public class AbsoluteLocationResource extends Resource {
    
    /**
     * Timer for checking the usage of the resource.
     */
    private Timer timeoutTimer = null;
    
    /**
     * Reference to the {@link LocationResourceGroup}.
     */
    private LocationResourceGroup locationRG;
    
    /**
     * Location manager which provides access to the GPS location.
     */
    protected LocationManager locationManager;
    
    /**
     * {@link LocationListener} to fetch informations from the GPS module.
     */
    protected LocationListener locationListener = null;
    
    /**
     * Map holds all current requests.
     */
    Map<String, UpdateRequest> requests = new HashMap<String, UpdateRequest>();
    
    /**
     * Boolean is set to true when GPS is enabled.
     */
    protected boolean gpsEnabled = false;
    
    /**
     * Current latitude.
     */
    protected double latitude = 0.0;
    
    /**
     * Current longitude.
     */
    protected double longitude = 0.0;
    
    /**
     * Current accuracy.
     */
    protected float accuracy = 0.0F;
    
    /**
     * Current speed.
     */
    protected float speed = 0.0F;
    
    /**
     * Is set to true when the GPS signal is fixed.
     */
    protected boolean fixed = false;
    
    /**
     * Broadcast intent action indicating that the GPS has either started or stopped receiving GPS
     * fixes. An intent extra provides this state as a boolean, where {@code true} means that the
     * GPS is actively receiving fixes.
     * 
     * @see #EXTRA_ENABLED
     */
    public static final String GPS_FIX_CHANGE_ACTION = "android.location.GPS_FIX_CHANGE";
    
    /**
     * The lookup key for a boolean that indicates whether GPS is enabled or disabled. {@code true} means GPS is
     * enabled. Retrieve it with {@link android.content.Intent#getBooleanExtra(String,boolean)}.
     */
    public static final String EXTRA_ENABLED = "enabled";
    
    /**
     * A {@link BroadcastReceiver} to detect if the GPS module has a fix on the current location.
     */
    private BroadcastReceiver receiver = new DefaultBroadcastReceiver();
    
    
    /**
     * Create a new instance of the {@link AbsoluteLocationImpl}.
     * 
     * @param locationRG
     *            Reference to the {@link LocationResourceGroup}.
     */
    public AbsoluteLocationResource(LocationResourceGroup locationRG) {
        this.locationRG = locationRG;
    }
    
    
    @Override
    public IBinder getAndroidInterface(String appIdentifier) {
        return new AbsoluteLocationImpl(this.locationRG, this, appIdentifier);
    }
    
    
    @Override
    public IBinder getMockedAndroidInterface(String appIdentifier) {
        return new AbsoluteLocationMockImpl();
    }
    
    
    @Override
    public IBinder getCloakedAndroidInterface(String appIdentifier) {
        return new AbsoluteLocationCloakImpl();
    }
    
    
    /**
     * Starts a new location lookup.
     * 
     * @param appIdentifier
     *            Identifier of the App.
     * @param request
     *            Used UpdateRequest with details about minTime and minDistance.
     */
    public void startLocationLookup(String appIdentifier, UpdateRequest request) {
        this.locationManager = (LocationManager) this.locationRG.getContext(appIdentifier).getSystemService(
                Context.LOCATION_SERVICE);
        
        this.requests.put(appIdentifier, request);
        
        /* Create new locationListener, and timer if not already done. */
        if (this.locationListener == null) {
            this.locationListener = new DefaultLocationListener();
            
            this.timeoutTimer = new Timer();
            this.timeoutTimer.schedule(new UpdateRequestVerificator(), UpdateRequest.MAX_TIME_BETWEEN_REQUEST,
                    UpdateRequest.MAX_TIME_BETWEEN_REQUEST);
        }
        
        /* If the GPS is not already enabled, create a notification. */
        if (!this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            createNotification();
            this.gpsEnabled = false;
        } else {
            this.gpsEnabled = true;
        }
        
        /* Register the BroadcastReceiver for detecting a GPS location fix. */
        this.locationRG.getContext(appIdentifier).registerReceiver(this.receiver,
                new IntentFilter(GPS_FIX_CHANGE_ACTION));
        
        /* Start the request for location updates in a handler-thread to prevent exceptions. */
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            
            @Override
            public void run() {
                AbsoluteLocationResource.this.locationManager
                        .removeUpdates(AbsoluteLocationResource.this.locationListener);
                AbsoluteLocationResource.this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        calcMinTime(), calcMinDistance(), AbsoluteLocationResource.this.locationListener);
            }
        });
        
    }
    
    
    /**
     * Ends the location lookup for a specific app.
     * 
     * @param appIdentifier
     *            App which do not require any more location lookups.
     */
    public void endLocationLookup(String appIdentifier) {
        this.requests.remove(appIdentifier);
        
        /* When no more Apps are listening for the location, stop updates. */
        if (this.requests.size() == 0 && this.locationListener != null) {
            this.locationManager.removeUpdates(this.locationListener);
            this.locationRG.getContext(appIdentifier).unregisterReceiver(this.receiver);
            this.locationListener = null;
            this.timeoutTimer.cancel();
            this.gpsEnabled = false;
            this.accuracy = 0.0F;
            this.speed = 0.0F;
            this.longitude = 0.0;
            this.latitude = 0.0;
            this.fixed = false;
        }
    }
    
    
    /**
     * @return Returns whether GPS is enabled or not.
     */
    public boolean isGpsEnabled() {
        return this.gpsEnabled;
    }
    
    
    /**
     * @return Returns whether GPS location lookup is active or not.
     */
    public boolean isActive() {
        return (this.locationListener != null);
    }
    
    
    /**
     * @return Returns whether the GPS has a fixed location or not.
     */
    public boolean isFixed() {
        return this.fixed;
    }
    
    
    /**
     * @return Returns the current longitude. Or 0.0 If there was no previous fix.
     */
    public double getLongitude() {
        return this.longitude;
    }
    
    
    /**
     * @return Returns the current latitude. Or 0.0 If there was no previous fix.
     */
    public double getLatitude() {
        return this.latitude;
    }
    
    
    /**
     * @return Returns the current accuracy.
     */
    public float getAccuracy() {
        return this.accuracy;
    }
    
    
    /**
     * @return Returns the current speed.
     */
    public float getSpeed() {
        return this.speed;
    }
    
    
    /**
     * @return Calculates the minimal distance of all update requests.
     */
    protected float calcMinDistance() {
        float min = Float.MAX_VALUE;
        
        for (Entry<String, UpdateRequest> request : this.requests.entrySet()) {
            if (request.getValue().getMinDistance() < min) {
                min = request.getValue().getMinDistance();
            }
        }
        
        return min;
    }
    
    
    /**
     * @return Calculates the minimal time of all update requests.
     */
    protected long calcMinTime() {
        long min = Long.MAX_VALUE;
        
        for (Entry<String, UpdateRequest> request : this.requests.entrySet()) {
            if (request.getValue().getMinTime() < min) {
                min = request.getValue().getMinTime();
            }
        }
        
        return min;
    }
    
    
    /**
     * Creates a new notification.
     */
    protected void createNotification() {
        // FIXME: cannot access a context without an app permitting it
        /*    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pIntent = PendingIntent.getActivity(this.locationRG.getContext(), 0, intent, 0);
            
            Notification notification = new Notification(R.drawable.pmp_rg_location_error, this.locationRG.getContext()
                    .getString(R.string.pmp_rg_location_notification_infotext), System.currentTimeMillis());
            notification.setLatestEventInfo(this.locationRG.getContext(),
                    this.locationRG.getContext().getString(R.string.pmp_rg_location_notification_title), this.locationRG
                            .getContext().getString(R.string.pmp_rg_location_notification_description), pIntent);
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notification.vibrate = new long[] { 250, 250, 250 };
            
            NotificationManager notificationManager = (NotificationManager) this.locationRG.getContext().getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.notify("gpsDisabledNotification", 0, notification);*/
    }
    
    
    /**
     * Hides the notification.
     */
    protected void removeNotification() {
        // FIXME: cannot access a context without an app permitting it
        /*
        NotificationManager notificationManager = (NotificationManager) this.locationRG.getContext().getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.cancel("gpsDisabledNotification", 0);
        */
    }
    
    /**
     * {@link DefaultLocationListener} used to receive updates from the {@link LocationManager}.
     * 
     * @author Jakob Jarosch
     */
    class DefaultLocationListener implements LocationListener {
        
        @Override
        public void onLocationChanged(android.location.Location location) {
            AbsoluteLocationResource.this.longitude = location.getLongitude();
            AbsoluteLocationResource.this.latitude = location.getLatitude();
            AbsoluteLocationResource.this.accuracy = location.getAccuracy();
            AbsoluteLocationResource.this.speed = location.getSpeed();
            AbsoluteLocationResource.this.fixed = true;
        }
        
        
        @Override
        public void onProviderDisabled(String provider) {
            AbsoluteLocationResource.this.gpsEnabled = false;
            AbsoluteLocationResource.this.fixed = false;
            createNotification();
        }
        
        
        @Override
        public void onProviderEnabled(String provider) {
            AbsoluteLocationResource.this.gpsEnabled = true;
            removeNotification();
        }
        
        
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            /* Not required. */
        }
    }
    
    /**
     * {@link DefaultBroadcastReceiver} to receive changes of the gps fix state.
     * 
     * @author Jakob Jarosch
     */
    class DefaultBroadcastReceiver extends BroadcastReceiver {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            AbsoluteLocationResource.this.fixed = intent.getBooleanExtra(EXTRA_ENABLED, false);
        }
    }
    
    /**
     * {@link UpdateRequestVerificator} task is used to check if App has not recently requested any
     * update and can be removed from the list of active requests.
     * 
     * @author Jakob Jarosch
     */
    class UpdateRequestVerificator extends TimerTask {
        
        @Override
        public void run() {
            for (Entry<String, UpdateRequest> request : AbsoluteLocationResource.this.requests.entrySet()) {
                if (request.getValue().isOutdated()) {
                    endLocationLookup(request.getKey());
                }
            }
        }
    }
}
