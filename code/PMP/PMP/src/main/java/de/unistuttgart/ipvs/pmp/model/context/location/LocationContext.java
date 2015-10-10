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
package de.unistuttgart.ipvs.pmp.model.context.location;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.context.IContextView;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;

public class LocationContext implements IContext, LocationListener {
    
    /**
     * Stop if you have a position as close as this
     */
    private static final float BEST_ACCURACY = 25.0f;
    
    /**
     * And as new as this
     */
    private static final long BEST_TIME_DELTA = 60000L;
    
    /**
     * But don't waste more battery than this period
     */
    private static final long MAXIMUM_LOCATION_ESTIMATING_TIME = 25000L;
    
    /**
     * The maximum accuracy loss, if a newer update is to be added
     */
    private static final float MAXIMUM_ACCURACY_LOSS = 2.5f;
    
    /**
     * The maximum accuracy above which results are rejected
     */
    private static final float ACCURACY_REJECT_LIMIT = 1000.0f;
    
    /**
     * The maximal time difference above which results are rejected
     */
    // FIXME
    private static final long TIME_DELTA_REJECT_LIMIT = 300000L;
    /**
     * The possibly waiting {@link Thread}.
     */
    private volatile Thread waiter;
    
    private IContextView view = null;
    
    private LocationContextState lastState;
    
    
    public LocationContext() {
        this.lastState = new LocationContextState();
    }
    
    
    @Override
    public String getIdentifier() {
        return "LocationContext";
    }
    
    
    @Override
    public String getName() {
        return PMPApplication.getContext().getString(R.string.contexts_location_name);
    }
    
    
    @Override
    public String getDescription() {
        return PMPApplication.getContext().getString(R.string.contexts_location_desc);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public Drawable getIcon() {
        return PMPApplication.getContext().getResources().getDrawable(R.drawable.contexts_location_icon);
    }
    
    
    @Override
    public IContextView getView(Context context) {
        if (this.view == null) {
            this.view = new LocationContextView(context);
        }
        return this.view;
    }
    
    
    @Override
    public long update(Context context) {
        this.lastState.unset();
        this.waiter = null;
        
        final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        
        onLocationChanged(lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        onLocationChanged(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        
        // if these were already fine, no need to do anything
        if ((this.lastState.getAccuracy() <= BEST_ACCURACY)
                && (this.lastState.getTime() >= System.currentTimeMillis() - BEST_TIME_DELTA)) {
            return 0L;
        }
        
        this.waiter = Thread.currentThread();
        
        Thread looping = new Thread() {
            
            @Override
            public void run() {
                Looper.prepare();
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, LocationContext.this);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, LocationContext.this);
                Looper.loop();
            }
        };
        
        looping.start();
        
        try {
            Thread.sleep(MAXIMUM_LOCATION_ESTIMATING_TIME);
        } catch (InterruptedException e) {
            // do nothing, desired behavior
        } finally {
            looping.interrupt();
            lm.removeUpdates(this);
        }
        
        // reject, if not sufficing (and not an emulator)
        if (this.lastState.isSet() && (!PMPApplication.isEmulator())
                && ((this.lastState.getAccuracy() > ACCURACY_REJECT_LIMIT)
                        || (System.currentTimeMillis() - this.lastState.getTime() > TIME_DELTA_REJECT_LIMIT))) {
            this.lastState.unset();
        }
        
        return 0L;
    }
    
    
    @Override
    public boolean getLastState(String condition) {
        try {
            if (this.lastState.isSet()) {
                LocationContextCondition lcc = LocationContextCondition.parse(condition);
                return lcc.satisfiedIn(this.lastState);
            } else {
                return false;
            }
        } catch (InvalidConditionException ice) {
            return false;
        }
    }
    
    
    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            return;
        }
        
        // only check if this might not be an emulator
        boolean update = PMPApplication.isEmulator();
        long now = System.currentTimeMillis();
        
        if (!update) {
            
            // reject when too old
            if (location.getTime() < now - TIME_DELTA_REJECT_LIMIT) {
                return;
            }
            // or too bad
            if ((!location.hasAccuracy()) || (location.getAccuracy() > ACCURACY_REJECT_LIMIT)) {
                return;
            }
            
            // if it's better, take it
            update |= (location.getAccuracy() < this.lastState.getAccuracy());
            // if it's not too worse, but newer, take it
            update |= (location.getAccuracy() < this.lastState.getAccuracy() * MAXIMUM_ACCURACY_LOSS)
                    && (location.getTime() > this.lastState.getTime());
        }
        
        if (update) {
            this.lastState.update(location);
        }
        
        // if that's enough, interrupt the waiting
        if ((this.waiter != null) && (this.lastState.getAccuracy() <= BEST_ACCURACY)
                && (this.lastState.getTime() >= now - BEST_TIME_DELTA)) {
            this.waiter.interrupt();
        }
    }
    
    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Don't need this
    }
    
    
    @Override
    public void onProviderEnabled(String provider) {
        // Don't need this
    }
    
    
    @Override
    public void onProviderDisabled(String provider) {
        // Don't need this
    }
    
    
    @Override
    public String makeHumanReadable(String condition) throws InvalidConditionException {
        return LocationContextCondition.parse(condition).toHumanReadable();
    }
    
    
    @Override
    public void conditionValidOrThrow(String condition) throws InvalidConditionException {
        LocationContextCondition.parse(condition);
    }
}
