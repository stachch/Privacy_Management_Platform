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
package de.unistuttgart.ipvs.pmp.api.ipc.command;

import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.api.handler.PMPHandler;
import de.unistuttgart.ipvs.pmp.api.handler.PMPServiceFeatureUpdateHandler;
import de.unistuttgart.ipvs.pmp.service.pmp.IPMPService;

/**
 * Command to get the current service features.
 * 
 * @author Tobias Kuhn
 * 
 */
public class IPC2PMPUpdateServiceFeaturesCommand extends IPC2PMPCommand<PMPServiceFeatureUpdateHandler> {
    
    public IPC2PMPUpdateServiceFeaturesCommand(PMPServiceFeatureUpdateHandler handler, String sourceService,
            long timeout) {
        super(handler, sourceService, timeout);
    }
    
    
    @Override
    protected void executeOnPMP(IPMPService pmp) throws RemoteException {
        if (!pmp.getServiceFeatureUpdate(getSourceService())) {
            getPMPHandler().onUpdateFailed();
        }
    }
    
    
    @Override
    protected PMPHandler getNullHandler() {
        return new PMPServiceFeatureUpdateHandler();
    }
    
}
