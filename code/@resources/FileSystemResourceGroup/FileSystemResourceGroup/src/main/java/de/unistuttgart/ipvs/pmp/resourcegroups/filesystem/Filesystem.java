/*
 * Copyright 2012 pmp-android development team
 * Project: FileSystemResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.filesystem;

import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.resource.IPMPConnectionInterface;
import de.unistuttgart.ipvs.pmp.shared.resource.ResourceGroup;

/**
 * This resource gives access to files saved on the user's Andorid device. To do so, it defines several privacy
 * settings,
 * such as privacy-settings for reading or writing files on the device's file system. It also registers all
 * corresponding
 * resources.
 * 
 * @author Patrick Strobel
 * @version 0.2.0
 */
public class Filesystem extends ResourceGroup {
    
    public static final String PACKAGE_NAME = "de.unistuttgart.ipvs.pmp.resourcegroups.filesystem";
    
    
    /**
     * Creates the resource-group including its privacy settings and resources
     * 
     * @param context
     *            Context of the service giving access to our resource-group
     * @param service
     *            Class of our service.
     * @throws Exception
     *             Throws if at least one privacy setting could not be instantiated.
     */
    public Filesystem(IPMPConnectionInterface pmpci) {
        super(PACKAGE_NAME, pmpci);
        
        new PrivacySettings(this).registerPrivacySettings();
        
        Resources resources = new Resources();
        resources.registerResources(this);
    }
    
    
    public void onRegistrationSuccess() {
        Log.d(this, "Registration was successfull");
    }
    
    
    public void onRegistrationFailed(String message) {
        Log.d(this, "Registration failed: " + message);
    }
}
