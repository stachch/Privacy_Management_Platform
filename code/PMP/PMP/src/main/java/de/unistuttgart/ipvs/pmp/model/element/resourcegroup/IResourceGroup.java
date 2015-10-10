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
package de.unistuttgart.ipvs.pmp.model.element.resourcegroup;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;

/**
 * {@link IResourceGroup} represents a resource group known by PMP.
 * 
 * @author Jakob Jarosch
 */
public interface IResourceGroup extends IModelElement {
    
    /**
     * @return the <b>unique</b> identifier of the {@link IResourceGroup}.
     */
    @Override
    public String getIdentifier();
    
    
    /**
     * @return the localized name of the {@link IResourceGroup}.
     */
    public String getName();
    
    
    /**
     * @return the localized description of the {@link IResourceGroup}.
     */
    public String getDescription();
    
    
    /**
     * @return an icon associated with this resource group or null if none found
     */
    public Drawable getIcon();
    
    
    /**
     * @return the revision number as the modification date of classes or xml
     */
    public long getRevision();
    
    
    /**
     * @return all {@link IPrivacySetting}s contained in this {@link IResourceGroup}.
     */
    public List<IPrivacySetting> getPrivacySettings();
    
    
    /**
     * Returns a {@link IPrivacySetting} for the given identifier.
     * 
     * @param privacySettingIdentifier
     *            Identifier of the {@link IPrivacySetting} which should be returned.
     * @return the requested {@link IPrivacySetting} or null, if no {@link IPrivacySetting} is available
     *         with that identifier.
     */
    public IPrivacySetting getPrivacySetting(String privacySettingIdentifier);
    
    
    /**
     * Returns the {@link IBinder} AIDL interface for a resource of this {@link IResourceGroup}.
     * 
     * @param appPackage
     *            the package of the requesting app
     * @param resource
     *            the name of the resource
     * @return the AIDL binder for the specified resource, or null, if none such resource is present
     */
    public IBinder getResource(String appPackage, String resource);
    
    
    /**
     * 
     * @return true, if the plugin somehow failed. Any further calls on this object are not permitted.
     */
    public boolean isDeactivated();
    
    
    /**
     * 
     * @return the reason for the deactivation (i.e. the {@link Throwable} that the plugin threw), iff isDeactivated(),
     *         else null.
     */
    public Throwable getReasonForDeactivation();
    
}
