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

import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.shared.api.PMPResourceIdentifier;
import de.unistuttgart.ipvs.pmp.shared.api.handler.PMPHandler;
import de.unistuttgart.ipvs.pmp.shared.api.handler.PMPRequestResourceHandler;
import de.unistuttgart.ipvs.pmp.shared.service.pmp.IPMPService;

/**
 * Command to request a resource from PMP.
 * 
 * @author Tobias Kuhn
 *         
 */
public class IPC2PMPRequestResourceCommand extends IPC2PMPCommand<PMPRequestResourceHandler> {
    
    private final PMPResourceIdentifier resource;
    
    
    public IPC2PMPRequestResourceCommand(PMPResourceIdentifier resource, PMPRequestResourceHandler handler,
            String sourceService, long timeout) {
        super(handler, sourceService, timeout);
        this.resource = resource;
    }
    
    
    @Override
    protected void executeOnPMP(IPMPService pmp) throws RemoteException {
        getPMPHandler().onReceiveResource(this.resource,
                pmp.getResource(getSourceService(), this.resource.getResourceGroup(), this.resource.getResource()),
                pmp.isMocked(getSourceService(), this.resource.getResourceGroup()));
                
    }
    
    
    @Override
    protected PMPHandler getNullHandler() {
        return new PMPRequestResourceHandler();
    }
}
