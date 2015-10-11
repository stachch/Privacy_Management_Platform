/*
 * Copyright 2012 pmp-android development team
 * Project: PMP-API
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
package de.unistuttgart.ipvs.pmp.shared.resource;

import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.AbstractPrivacySetting;

/**
 * An individual Resource of a {@link ResourceGroup}.
 * 
 * @author Tobias Kuhn
 *         
 */
public abstract class Resource {
    
    /**
     * The resource group that this resource is assigned to.
     */
    private ResourceGroup resourceGroup;
    
    
    /**
     * Assigns the resource group during registration.
     * 
     * <b>Do not call this method.</b>
     * 
     * @param resourceGroup
     */
    protected final void assignResourceGroup(ResourceGroup resourceGroup) {
        this.resourceGroup = resourceGroup;
    }
    
    
    /**
     * 
     * @return the associated {@link ResourceGroup}.
     */
    protected final ResourceGroup getResourceGroup() {
        return this.resourceGroup;
    }
    
    
    /**
     * Retrieves an actual privacy setting class.
     * 
     * @param privacySettingIdentifier
     *            the identifier of the privacy setting
     * @return the privacy setting with the the identifier in the resource group.
     */
    public final AbstractPrivacySetting<?> getPrivacySetting(String privacySettingIdentifier) {
        return this.resourceGroup.getPrivacySetting(privacySettingIdentifier);
    }
    
    
    /**
     * Gets the {@link IBinder} defined in AIDL for communicating over a Service.
     * 
     * @see http://developer.android.com/guide/developing/tools/aidl.html
     *      
     * @param appPackage
     *            the package for the app accessing the interface.
     *            
     * @return The IBinder that shall be returned when an App binds against the {@link ResourceGroupService} requesting
     *         this resource.
     */
    public abstract IBinder getAndroidInterface(String appPackage);
    
    
    /**
     * Gets the {@link IBinder} defined in AIDL for communicating over a Service, but only containing obviously fake
     * data. <b>Note that you must not return the equal or subclass of {@link Resource#getAndroidInterface(String)}.</b>
     * 
     * @see http://developer.android.com/guide/developing/tools/aidl.html
     *      
     * @param appPackage
     *            the package for the app accessing the interface.
     *            
     * @return The IBinder that shall be returned when an App binds against the {@link ResourceGroupService} requesting
     *         this resource.
     */
    public abstract IBinder getMockedAndroidInterface(String appPackage);
    
    
    /**
     * Gets the {@link IBinder} defined in AIDL for communicating over a Service, but only containing data that is
     * indistinguishable from real one, but is actually fake. <b>Note that you must not return the equal or subclass of
     * {@link Resource#getAndroidInterface(String)}.</b>
     * 
     * @see http://developer.android.com/guide/developing/tools/aidl.html
     *      
     * @param appPackage
     *            the package for the app accessing the interface.
     *            
     * @return The IBinder that shall be returned when an App binds against the {@link ResourceGroupService} requesting
     *         this resource.
     */
    public abstract IBinder getCloakedAndroidInterface(String appPackage);
    
}
