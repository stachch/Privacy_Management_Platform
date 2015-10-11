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
package de.unistuttgart.ipvs.pmp.shared.api.ipc.command;

import java.util.List;

import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.shared.api.handler.PMPHandler;
import de.unistuttgart.ipvs.pmp.shared.api.handler.PMPRequestServiceFeaturesHandler;
import de.unistuttgart.ipvs.pmp.shared.service.pmp.IPMPService;

/**
 * Command to request the enabling of service features at PMP.
 * 
 * @author Tobias Kuhn
 *         
 */
public class IPC2PMPRequestServiceFeaturesCommand extends IPC2PMPCommand<PMPRequestServiceFeaturesHandler> {
    
    private final List<String> serviceFeatures;
    
    
    public IPC2PMPRequestServiceFeaturesCommand(List<String> serviceFeatures, PMPRequestServiceFeaturesHandler handler,
            String sourceService, long timeout) {
        super(handler, sourceService, timeout);
        this.serviceFeatures = serviceFeatures;
    }
    
    
    @Override
    protected void executeOnPMP(IPMPService pmp) throws RemoteException {
        if (!pmp.requestServiceFeature(getSourceService(),
                this.serviceFeatures.toArray(new String[this.serviceFeatures.size()]))) {
            getPMPHandler().onRequestFailed();
        }
    }
    
    
    @Override
    protected PMPHandler getNullHandler() {
        return new PMPRequestServiceFeaturesHandler();
    }
    
}
