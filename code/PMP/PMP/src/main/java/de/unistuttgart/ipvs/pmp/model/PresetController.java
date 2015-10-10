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
package de.unistuttgart.ipvs.pmp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import de.unistuttgart.ipvs.pmp.model.element.app.App;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.ServiceFeature;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.util.FileLog;

/**
 * Calculates all the preset or service feature (de)active, etc. stuff optimized in one place.
 * 
 * @author Tobias Kuhn
 * 
 */
public class PresetController {
    
    private static final String TAG = "PresetController";
    
    
    /**
     * Finds the actually granted privacy setting value for privacy setting <code>ps</code> in preset <code>p</code>.
     * Incorporates looking through all context annotations and selecting the appropriate ones.
     * 
     * @param p
     *            preset that contains the privacy setting value
     * @param ps
     *            the privacy setting whose value shall be found
     * @return the actually set privacy setting value for <code>ps</code> in <code>p</code>
     * @throws PrivacySettingValueException
     *             if a value was rejected by the privacy setting
     */
    private static String findGrantedPSValue(IPreset p, IPrivacySetting ps) throws PrivacySettingValueException {
        String lastValue = p.getGrantedPrivacySettingValue(ps);
        boolean usingContexts = false;
        
        for (IContextAnnotation ca : p.getContextAnnotations(ps)) {
            if (ca.isActive()) {
                if (!usingContexts) {
                    // override value, first context
                    lastValue = ca.getOverridePrivacySettingValue();
                    usingContexts = true;
                    
                } else {
                    // additive logic inside contexts
                    if (ps.permits(lastValue, ca.getOverridePrivacySettingValue())) {
                        lastValue = ca.getOverridePrivacySettingValue();
                    }
                    
                }
            }
        }
        
        if (usingContexts) {
            FileLog.get().logWithForward(TAG, null, FileLog.GRANULARITY_CONTEXT_CHANGES, Level.FINER,
                    "Contexts overrode the set value for %s, it is now '%s'.", ps.getName(), lastValue);
        }
        
        return lastValue;
    }
    
    
    /**
     * Finds all the granted {@link IPrivacySetting} with their values for a specific {@link App} limiting the search to
     * the {@link IPrivacySetting}s in relevant.
     * 
     * @param app
     *            the app to be searched for
     * @param relevant
     *            the privacy settings which are actually necessary to be searched for. It is recommended to pass a
     *            {@link Set}.
     * @return a map mapping the privacy setting to its value
     * @throws PrivacySettingValueException
     *             if a value was rejected by the privacy setting
     */
    private static Map<IPrivacySetting, String> findGranted(IApp app, Collection<IPrivacySetting> relevant)
            throws PrivacySettingValueException {
        Map<IPrivacySetting, String> granted = new HashMap<IPrivacySetting, String>();
        // for all presets
        for (IPreset p : app.getAssignedPresets()) {
            if (!p.isAvailable() || p.isDeleted()) {
                continue;
            }
            
            // check relevant
            for (IPrivacySetting ps : relevant) {
                String grantNow = findGrantedPSValue(p, ps);
                
                if (grantNow != null) {
                    String existing = granted.get(ps);
                    
                    if (existing == null) {
                        granted.put(ps, grantNow);
                    } else {
                        if (ps.permits(existing, grantNow)) {
                            // grantNow allows more
                            granted.put(ps, grantNow);
                            
                        }
                        /* else existing allows more, do nothing */
                    }
                }
                
            } /* for relevant PS */
        }
        
        return granted;
    }
    
    
    /**
     * Finds the best value for a {@link PrivacySetting} for an {@link App}.
     * 
     * @param app
     *            the app to be searched for
     * @param privacySetting
     *            the privacy setting to be searched for
     * @return the best value for that privacy setting
     * @throws PrivacySettingValueException
     *             if a value was rejected by the privacy setting
     */
    public static String findBestValue(IApp app, IPrivacySetting privacySetting) throws PrivacySettingValueException {
        List<IPrivacySetting> relevant = new ArrayList<IPrivacySetting>(1);
        relevant.add(privacySetting);
        return findGranted(app, relevant).get(privacySetting);
    }
    
    
    /**
     * Verifies all the service features of an app against its granted privacy settings.
     * 
     * @param app
     *            the app to be checked
     * @param serviceFeatures
     *            the service features of the app to be checked
     * @return a map mapping {@link ServiceFeature}s to booleans, where they are true, if and only if they are both
     *         available and enabled
     * @throws PrivacySettingValueException
     *             if a value was rejected by the privacy setting
     */
    public static Map<ServiceFeature, Boolean> verifyServiceFeatures(App app, Collection<ServiceFeature> serviceFeatures)
            throws PrivacySettingValueException {
        // find the relevant PS
        Set<IPrivacySetting> relevant = new HashSet<IPrivacySetting>();
        for (ServiceFeature sf : serviceFeatures) {
            for (IPrivacySetting ips : sf.getRequiredPrivacySettings()) {
                relevant.add(ips);
            }
        }
        
        Map<IPrivacySetting, String> granted = findGranted(app, relevant);
        
        // actual check against granted
        Map<ServiceFeature, Boolean> verification = new HashMap<ServiceFeature, Boolean>();
        for (ServiceFeature sf : serviceFeatures) {
            verification.put(sf, verifyServiceFeature(granted, sf));
        }
        
        return verification;
    }
    
    
    /**
     * Verifies that one service feature is enabled or not using the app's granted privacy settings.
     * 
     * @param serviceFeature
     *            the service feature to verify
     * @return true, if and only if the {@link ServiceFeature} is both available and enabled
     * @throws PrivacySettingValueException
     *             if a value was rejected by the privacy setting
     */
    public static boolean verifyServiceFeature(ServiceFeature serviceFeature) throws PrivacySettingValueException {
        Map<IPrivacySetting, String> granted = findGranted(serviceFeature.getApp(),
                serviceFeature.getRequiredPrivacySettings());
        return verifyServiceFeature(granted, serviceFeature);
    }
    
    
    /**
     * Verifies that one service feature is enabled or not using the supplied granted privacy settings
     * 
     * @param granted
     *            the privacy settings that are granted in this scenario
     * @param serviceFeature
     *            the service feature to verify
     * @return true, if and only if the {@link ServiceFeature} is both available and enabled given the granted privacy
     *         settings
     * @throws PrivacySettingValueException
     *             if a value was rejected by the privacy setting
     */
    public static boolean verifyServiceFeature(Map<IPrivacySetting, String> granted, ServiceFeature serviceFeature)
            throws PrivacySettingValueException {
        if (!serviceFeature.isAvailable()) {
            return false;
        }
        
        // actual check against granted
        for (Entry<PrivacySetting, String> reqPSValue : serviceFeature.getRequiredPrivacySettingValues().entrySet()) {
            if (!reqPSValue.getKey().permits(reqPSValue.getValue(), granted.get(reqPSValue.getKey()))) {
                return false;
            }
        }
        
        return true;
    }
    
}
