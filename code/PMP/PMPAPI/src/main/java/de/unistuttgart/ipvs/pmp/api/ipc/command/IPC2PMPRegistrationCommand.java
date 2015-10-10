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
import de.unistuttgart.ipvs.pmp.api.handler.PMPRegistrationHandler;
import de.unistuttgart.ipvs.pmp.service.pmp.IPMPService;
import de.unistuttgart.ipvs.pmp.service.pmp.RegistrationResult;

/**
 * Command to register an App with PMP.
 * 
 * @author Tobias Kuhn
 * 
 */
public class IPC2PMPRegistrationCommand extends IPC2PMPCommand<PMPRegistrationHandler> {
    
    public IPC2PMPRegistrationCommand(PMPRegistrationHandler handler, String sourceService, long timeout) {
        super(handler, sourceService, timeout);
    }
    
    
    @Override
    protected void executeOnPMP(IPMPService pmp) throws RemoteException {
        if (pmp.isRegistered(getSourceService())) {
            getPMPHandler().onAlreadyRegistered();
            
        } else {
            
            getPMPHandler().onRegistration();
            
            RegistrationResult rr = pmp.registerApp(getSourceService());
            if (rr.getSuccess()) {
                getPMPHandler().onSuccess();
            } else {
                getPMPHandler().onFailure(rr.getMessage());
            }
            
        }
    }
    
    
    @Override
    protected PMPHandler getNullHandler() {
        return new PMPRegistrationHandler();
    }
}
