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
package de.unistuttgart.ipvs.pmp.api;

import android.app.Application;
import android.os.Bundle;
import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.service.app.IAppService;

/**
 * Implementation of the {@link IAppService.Stub} stub.
 * 
 * @author Thorsten Berberich
 */
public class AppServiceImplementation extends IAppService.Stub {
    
    /**
     * The {@link Application} referenced.
     */
    private final Application application;
    
    
    public AppServiceImplementation(Application application) {
        this.application = application;
    }
    
    
    @Override
    public void updateServiceFeatures(Bundle features) throws RemoteException {
        PMP.getForService(this.application).onServiceFeatureUpdate(features);
    }
    
}
