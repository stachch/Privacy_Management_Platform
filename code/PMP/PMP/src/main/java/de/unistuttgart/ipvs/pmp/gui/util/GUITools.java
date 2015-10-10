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
package de.unistuttgart.ipvs.pmp.gui.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.gui.app.ActivityApp;
import de.unistuttgart.ipvs.pmp.gui.preset.ActivityPreset;
import de.unistuttgart.ipvs.pmp.gui.resourcegroup.ActivityResourceGroups;
import de.unistuttgart.ipvs.pmp.gui.resourcegroup.TabAvailable;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;

public class GUITools {
    
    /**
     * @return the action as a String, or an empty String if no action is given.
     */
    public static String getIntentAction(Intent intent) {
        /* Intent should never be null */
        if (intent == null) {
            throw new IllegalArgumentException("Intent can't be null");
        }
        
        String action = null;
        
        if (intent.getExtras() != null) {
            action = intent.getExtras().getString(GUIConstants.ACTIVITY_ACTION);
        }
        
        if (action == null) {
            action = "";
        }
        
        return action;
    }
    
    
    /**
     * Opens the App details of the given App.
     * 
     * @param app
     *            the App which should be opened
     */
    public static Intent createAppActivityIntent(IApp app) {
        Intent intent = new Intent(PMPApplication.getContext(), ActivityApp.class);
        intent.putExtra(GUIConstants.APP_IDENTIFIER, app.getIdentifier());
        return intent;
    }
    
    
    /**
     * Handles an intent which is called when a specific App should be referenced in the {@link Activity}.
     * 
     * @param intent
     *            which invoked the {@link Activity}
     * @return the corresponding {@link IApp}
     */
    public static IApp getIAppFromIntent(Intent intent) {
        /* Intent should never be null */
        if (intent == null) {
            throw new IllegalArgumentException("Intent can't be null");
        }
        
        String appIdentifier = intent.getExtras().getString(GUIConstants.APP_IDENTIFIER);
        
        /* App Identifier should never be null */
        if (appIdentifier == null) {
            throw new IllegalArgumentException("Intent should have the GUIConstants.APP_IDENTIFIER packed with it");
        }
        
        IApp app = ModelProxy.get().getApp(appIdentifier);
        
        /* App does not exists in the model */
        if (app == null) {
            throw new IllegalArgumentException("The given App (" + appIdentifier
                    + ") in the Intent does not exist in the model");
        }
        
        return app;
    }
    
    
    /**
     * Opens the {@link TabAvailable} tab and views all the listed Resourcegroups.
     * 
     * @param filteredRGIdentifiers
     *            Only the given Resourcegroups will be displayed.
     */
    public static Intent createRgFilterIntent(String[] filteredRGIdentifiers) {
        Intent intent = new Intent(PMPApplication.getContext(), ActivityResourceGroups.class);
        intent.putExtra(GUIConstants.ACTIVITY_ACTION, GUIConstants.FILTER_AVAILABLE_RGS);
        
        StringBuffer sb = new StringBuffer();
        sb.append("package:");
        for (String s : filteredRGIdentifiers) {
            if (sb.length() > 0) {
                sb.append(",package:");
            }
            sb.append(s);
        }
        intent.putExtra(GUIConstants.RGS_FILTER, sb.toString());
        
        return intent;
    }
    
    
    /**
     * @return the RGs filter as a String, or an empty String if no filter is given.
     */
    public static String getRgFilterFromIntent(Intent intent) {
        /* Intent should never be null */
        if (intent == null) {
            throw new IllegalArgumentException("Intent can't be null");
        }
        
        String rgsFilter = null;
        
        if (intent.getExtras() != null) {
            rgsFilter = intent.getExtras().getString(GUIConstants.RGS_FILTER);
        }
        
        if (rgsFilter == null) {
            rgsFilter = "";
        }
        
        return rgsFilter;
    }
    
    
    public static Intent createPresetIntent(IPreset preset) {
        Intent intent = new Intent(PMPApplication.getContext(), ActivityPreset.class);
        intent.putExtra(GUIConstants.PRESET_IDENTIFIER, preset.getLocalIdentifier());
        return intent;
    }
    
    
    /**
     * Returns the id for a requested PresetSet.
     * 
     * @param intent
     *            {@link Intent} which should be used for determining the id.
     * @return The id of a PresetSet.
     */
    public static String getPresetSetId(Intent intent) {
        /* Intent should never be null */
        if (intent == null) {
            throw new IllegalArgumentException("Intent can't be null");
        }
        
        String presetSetId = intent.getExtras().getString(GUIConstants.PRESET_SET_ID);
        
        /* PresetSet identifier should never be null */
        if (presetSetId == null) {
            throw new IllegalArgumentException("Intent should have the GUIConstants.PRESET_SET_ID packed with it");
        }
        
        return presetSetId;
    }
    
    
    /**
     * Returns all the requested Service Features.
     * 
     * @param intent
     *            Intent which should be handled.
     * @return The requested Service Features.
     */
    public static String[] getRequestedServiceFeatures(Intent intent) {
        String action = getIntentAction(intent);
        String[] requestedSFs = null;
        if (action != null && action.equals(GUIConstants.CHANGE_SERVICEFEATURE)) {
            requestedSFs = intent.getStringArrayExtra(GUIConstants.REQUIRED_SERVICE_FEATURE);
        }
        
        if (requestedSFs == null) {
            requestedSFs = new String[0];
        }
        
        return requestedSFs;
    }
    
    
    /**
     * @return Validates to true if the Service Feature was requested by the Intent.
     */
    public static boolean isServiceFeatureRequested(Intent intent, IServiceFeature sf) {
        String[] requestedSFs = getRequestedServiceFeatures(intent);
        for (String reqSF : requestedSFs) {
            if (reqSF.equals(sf.getLocalIdentifier())) {
                return true;
            }
        }
        
        return false;
    }
    
    
    /**
     * Starts the given {@link Intent}.
     * 
     * @param intent
     *            {@link Intent} which should be started.
     */
    public static void startIntent(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PMPApplication.getContext().startActivity(intent);
    }
    
    
    /**
     * Creates a new {@link Toast} in the main looper (gui).
     * 
     * @param context
     *            {@link Context} which is required for {@link Toast} creation.
     * @param message
     *            Message which should be displayed.
     * @param duration
     */
    public static void showToast(final Context context, final String message, final int duration) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            
            @Override
            public void run() {
                Toast.makeText(context, message, duration).show();
            }
        });
    }
}
