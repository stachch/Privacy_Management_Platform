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
package de.unistuttgart.ipvs.pmp.model.plugin;

import java.util.logging.Level;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.model.Model;
import de.unistuttgart.ipvs.pmp.model.PersistenceConstants;
import de.unistuttgart.ipvs.pmp.model.PresetController;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.resource.IPMPConnectionInterface;
import de.unistuttgart.ipvs.pmp.resource.RGMode;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.util.FileLog;

/**
 * A singleton for the PMP model to connect to the RG plugins.
 * 
 * @author Tobias Kuhn
 * 
 */
public class PMPConnectionInterface implements IPMPConnectionInterface {
    
    private static PMPConnectionInterface instance = new PMPConnectionInterface();
    
    
    public static PMPConnectionInterface getInstance() {
        return instance;
    }
    
    
    private PMPConnectionInterface() {
    }
    
    
    @Override
    public String getPrivacySettingValue(String rgPackage, String psIdentifier, String appPackage) {
        IResourceGroup rg = Model.getInstance().getResourceGroup(rgPackage);
        if (rg == null) {
            return null;
        } else {
            IPrivacySetting ps = rg.getPrivacySetting(psIdentifier);
            if (ps == null) {
                return null;
            } else {
                IApp a = Model.getInstance().getApp(appPackage);
                if (a == null) {
                    return null;
                } else {
                    return getPrivacySettingValue(rg, ps, a);
                }
            }
        }
    }
    
    
    /**
     * @see PMPConnectionInterface#getPrivacySettingValue(String, String, String)
     */
    private String getPrivacySettingValue(IResourceGroup rg, IPrivacySetting ps, IApp a) {
        try {
            String result = PresetController.findBestValue(a, ps);
            
            FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_SETTING_REQUESTS, Level.FINE,
                    "%s requested privacy setting value for '%s' for app '%s', returned '%s'.", rg.getName(),
                    ps.getName(), a.getName(), result);
            
            return result;
            
        } catch (PrivacySettingValueException plve) {
            Log.e(this, "Error while calculating privacy setting value.", plve);
            return null;
        }
    }
    
    
    @Override
    @Deprecated
    public Context getContext(String rgPackage) {
        return PMPApplication.getContext();
    }
    
    
    @Override
    public Context getContext(String rgPackage, String appPackage) {
        IResourceGroup rg = Model.getInstance().getResourceGroup(rgPackage);
        IApp app = Model.getInstance().getApp(appPackage);
        if (rg == null || app == null) {
            return new MockContext2();
        } else {
            RGMode mode = null;
            
            try {
                String bestValue = PresetController.findBestValue(app,
                        rg.getPrivacySetting(PersistenceConstants.MODE_PRIVACY_SETTING));
                mode = (bestValue == null) ? RGMode.NORMAL : RGMode.valueOf(bestValue);
            } catch (PrivacySettingValueException psve) {
                psve.printStackTrace();
            }
            
            if (RGMode.NORMAL.equals(mode)) {
                return new SecurityContextAdapter(PMPApplication.getContext(), rg, app);
            } else {
                return new MockContext2();
            }
        }
    }
    
    
    @Override
    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        // TODO do we need to connect this to the service?
        PMPApplication.getContext().registerReceiver(receiver, filter);
    }
    
    
    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        // TODO do we need to connect this to the service?
        PMPApplication.getContext().unregisterReceiver(receiver);
    }
    
}
