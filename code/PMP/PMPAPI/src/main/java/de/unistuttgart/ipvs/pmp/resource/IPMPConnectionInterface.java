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
package de.unistuttgart.ipvs.pmp.resource;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.test.mock.MockContext;

/**
 * Interface for communication from the {@link ResourceGroup} plugin to the PMP model.
 * 
 * @author Tobias Kuhn
 * 
 */
public interface IPMPConnectionInterface {
    
    /**
     * Must first ask the PMP model for the privacy setting identified by rgPackage and psIdentifier, then find out what
     * this privacy setting's value is for appPackage.
     * 
     * @param rgPackage
     * @param psIdentifier
     * @param appPackage
     * @return the value of the privacy setting identified for appPackage, or null, if it is not set or was not found
     */
    public String getPrivacySettingValue(String rgPackage, String psIdentifier, String appPackage);
    
    
    /**
     * Ability to get a context for a resource group.
     * 
     * @param rgPackage
     * @return an Android context
     * @deprecated Results are undefined. Use {@link #getContext(String, String)} instead.
     */
    @Deprecated
    public Context getContext(String rgPackage);
    
    
    /**
     * Ability to get a context for a resource group for a specific app.
     * 
     * @param rgPackage
     * @param appPackage
     * @return an Android {@link Context}, if the operation was allowed, a {@link MockContext} otherwise
     */
    public Context getContext(String rgPackage, String appPackage);
    
    
    /**
     * @see Context#registerReceiver(BroadcastReceiver, IntentFilter)
     */
    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter);
    
    
    /**
     * @see Context#unregisterReceiver(BroadcastReceiver)
     */
    public void unregisterReceiver(BroadcastReceiver receiver);
    
}
