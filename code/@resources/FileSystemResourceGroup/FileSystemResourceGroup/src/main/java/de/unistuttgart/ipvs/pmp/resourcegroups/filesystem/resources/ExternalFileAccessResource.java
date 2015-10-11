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
package de.unistuttgart.ipvs.pmp.resourcegroups.filesystem.resources;

import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.shared.resource.Resource;

/**
 * This resource gives access to external files stored on the device. The used external directory (e.g. "/Music" or
 * "/Ringtones") is selected in the constructor.
 * 
 * @author Patrick Strobel
 * @version 0.1.0
 */
public class ExternalFileAccessResource extends Resource {
    
    private ExternalFileAccess.Directories directory;
    
    
    /**
     * Creates a new resource.
     * 
     * @param directory
     *            Directory to which this resource shoud grant access
     */
    public ExternalFileAccessResource(ExternalFileAccess.Directories directory) {
        this.directory = directory;
    }
    
    
    @Override
    public IBinder getAndroidInterface(String appIdentifier) {
        return new ExternalFileAccess(appIdentifier, this, this.directory);
    }
    
    
    @Override
    public IBinder getMockedAndroidInterface(String appIdentifier) {
        return new ExternalFileAccessMock();
    }
    
    
    @Override
    public IBinder getCloakedAndroidInterface(String appIdentifier) {
        return new ExternalFileAccessCloak();
    }
    
}
