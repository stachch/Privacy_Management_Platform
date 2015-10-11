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
 * This resource gives access to every file or directory on the device. Accessing protected files, such as system files,
 * might require root-rights. Otherwise this resource-group will crash. For safety and security reasons it's recommended
 * to use this resource only if special file access is needed. Please use the other resources instead.
 * 
 * @author Patrick Strobel
 * @version 0.1.0
 */
public class GenericFileAccessResource extends Resource {
    
    @Override
    public IBinder getAndroidInterface(String appIdentifier) {
        return new GenericFileAccess(appIdentifier, this);
    }
    
    
    @Override
    public IBinder getMockedAndroidInterface(String appIdentifier) {
        return new GenericFileAccessMock();
    }
    
    
    @Override
    public IBinder getCloakedAndroidInterface(String appIdentifier) {
        return new GenericFileAccessCloak();
    }
    
}
