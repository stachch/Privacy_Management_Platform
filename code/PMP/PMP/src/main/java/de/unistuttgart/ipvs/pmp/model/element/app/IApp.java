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
package de.unistuttgart.ipvs.pmp.model.element.app;

import java.util.List;

import android.graphics.drawable.Drawable;
import de.unistuttgart.ipvs.pmp.model.IModel;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.ServiceFeature;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;

/**
 * <p>
 * This represents an {@link IApp} registered at PMP.
 * </p>
 * 
 * <p>
 * You can identify an {@link IApp} by its identifier, use {@link IApp#getIdentifier()}. With only an identifier you can
 * get the {@link IApp} object from {@link IModel#getApp(String)}.
 * </p>
 * 
 * @author Jakob Jarosch
 */
public interface IApp extends IModelElement {
    
    /**
     * @return the <b>unique</b> identifier of the {@link IApp}.
     */
    @Override
    public String getIdentifier();
    
    
    /**
     * @return the localized name of the {@link IApp}.
     */
    public String getName();
    
    
    /**
     * @return the localized description of the {@link IApp}.
     */
    public String getDescription();
    
    
    /**
     * @return an icon associated with this application or null if none found
     */
    public Drawable getIcon();
    
    
    /**
     * @return the service features provided by the {@link IApp}.
     */
    public List<IServiceFeature> getServiceFeatures();
    
    
    /**
     * Returns a service feature with exactly this identifier.
     * 
     * @param serviceFeatureIdentifier
     *            identifier of the {@link IServiceFeature}
     * @return the {@link IServiceFeature} with the specified identifier for this app, or null, if none exists
     */
    public IServiceFeature getServiceFeature(String serviceFeatureIdentifier);
    
    
    /**
     * @return the currently active {@link IServiceFeature}s for this {@link IApp}.
     */
    public List<IServiceFeature> getActiveServiceFeatures();
    
    
    /**
     * <p>
     * Queues the verification of all service features of this app which will publish the results to the app when
     * finished. This method is independent of {@link ServiceFeature#isActive()} which actually returns information.
     * </p>
     * 
     * <p>
     * The publishing itself is concurrent, the method will return without any information of the success of the
     * verification or the publishing.
     * </p>
     */
    public void verifyServiceFeatures();
    
    
    /**
     * @return all {@link IPreset}s which were assigned to the {@link IApp}.
     */
    public List<IPreset> getAssignedPresets();
    
    
    /**
     * Searches through all assigned presets and finds the best {@link IPrivacySetting} value avilable for this app.
     * 
     * @param privacySetting
     * @return the best value for privacySetting in all the presets, determined by
     *         {@link IPrivacySetting#permits(String, String)} or null, if none set or none allowed
     * @throws PrivacySettingValueException
     *             if any stored value is not a valid string for this privacy setting.
     */
    public String getBestAssignedPrivacySettingValue(IPrivacySetting privacySetting)
            throws PrivacySettingValueException;
    
}
