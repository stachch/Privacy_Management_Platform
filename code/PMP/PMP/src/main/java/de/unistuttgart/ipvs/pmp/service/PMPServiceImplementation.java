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
package de.unistuttgart.ipvs.pmp.service;

import java.util.logging.Level;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.model.Model;
import de.unistuttgart.ipvs.pmp.model.PersistenceConstants;
import de.unistuttgart.ipvs.pmp.model.PresetController;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.resource.RGMode;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.shared.service.pmp.IPMPService;
import de.unistuttgart.ipvs.pmp.shared.service.pmp.RegistrationResult;
import de.unistuttgart.ipvs.pmp.util.FileLog;

/**
 * Implementation of the {@link IPMPServiceApp.Stub} stub.
 * 
 * @author Jakob Jarosch
 */
public class PMPServiceImplementation extends IPMPService.Stub {
    
    @Override
    public boolean getServiceFeatureUpdate(String appPackage) throws RemoteException {
        
        IApp app = Model.getInstance().getApp(appPackage);
        if (app == null) {
            return false;
        } else {
            FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_SETTING_REQUESTS, Level.FINE,
                    "%s requested service features verification, results will be directly published to the app.",
                    app.getName());
                    
            app.verifyServiceFeatures();
            return true;
        }
    }
    
    
    @Override
    public RegistrationResult registerApp(String appPackage) throws RemoteException {
        if (Model.getInstance().getApp(appPackage) == null) {
            return Model.getInstance().registerApp(appPackage);
        } else {
            return new RegistrationResult(false, "Real registration attempt made, but already registered. Using API?");
        }
        
    }
    
    
    @Override
    public boolean isRegistered(String appPackage) throws RemoteException {
        return Model.getInstance().getApp(appPackage) != null;
    }
    
    
    @Override
    public IBinder getResource(String appPackage, String rgPackage, String resource) throws RemoteException {
        IResourceGroup rg = Model.getInstance().getResourceGroup(rgPackage);
        if (rg == null) {
            return null;
        } else {
            return rg.getResource(appPackage, resource);
        }
    }
    
    
    @Override
    public boolean requestServiceFeature(String appPackage, String[] requiredServiceFeatures) throws RemoteException {
        IApp app = Model.getInstance().getApp(appPackage);
        if (app == null) {
            return false;
        } else {
            Intent intent = GUITools.createAppActivityIntent(app);
            intent.putExtra(GUIConstants.ACTIVITY_ACTION, GUIConstants.CHANGE_SERVICEFEATURE);
            intent.putExtra(GUIConstants.REQUIRED_SERVICE_FEATURE, requiredServiceFeatures);
            GUITools.startIntent(intent);
            
            return true;
        }
    }
    
    
    @Override
    public boolean isMocked(String appPackage, String rgPackage) {
        IResourceGroup rg = Model.getInstance().getResourceGroup(rgPackage);
        IApp app = Model.getInstance().getApp(appPackage);
        if (rg == null || app == null) {
            return false;
        } else {
            RGMode mode = null;
            
            try {
                String bestValue = PresetController.findBestValue(app,
                        rg.getPrivacySetting(PersistenceConstants.MODE_PRIVACY_SETTING));
                mode = (bestValue == null) ? RGMode.NORMAL : RGMode.valueOf(bestValue);
            } catch (PrivacySettingValueException psve) {
                psve.printStackTrace();
            }
            
            return RGMode.MOCK.equals(mode);
        }
    }
    
}
