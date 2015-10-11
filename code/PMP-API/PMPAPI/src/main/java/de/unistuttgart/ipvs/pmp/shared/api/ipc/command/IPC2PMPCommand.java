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

import android.os.IBinder;
import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.shared.Constants;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.api.handler.PMPHandler;
import de.unistuttgart.ipvs.pmp.shared.service.pmp.IPMPService;

/**
 * A helper class for IPC commands directed towards PMP
 * 
 * @author Tobias Kuhn
 *         
 * @param <T>
 *            the handler subclass to have available in execute.
 */
public abstract class IPC2PMPCommand<T extends PMPHandler> extends IPCCommand {
    
    public IPC2PMPCommand(T handler, String sourceService, long timeout) {
        super(handler, sourceService, Constants.PMP_IDENTIFIER, timeout);
    }
    
    
    public IPC2PMPCommand(T handler, String sourceService) {
        super(handler, sourceService, Constants.PMP_IDENTIFIER);
    }
    
    
    @Override
    public void execute(IBinder binder) {
        String id = "?";
        try {
            id = binder.getInterfaceDescriptor();
        } catch (RemoteException re) {
            Log.e(this, "Cannot get interface descriptor: ", re);
        }
        
        if (id.equals(IPMPService.class.getName())) {
            try {
                executeOnPMP(IPMPService.Stub.asInterface(binder));
            } catch (RemoteException re) {
                Log.e(this, "Unexpected remote exception: ", re);
            }
        } else {
            Log.e(this, "Got wrong IBinder for PMP: " + id);
        }
    }
    
    
    @SuppressWarnings("unchecked")
    // guaranteed by the constructor
    protected T getPMPHandler() {
        return (T) getHandler();
    }
    
    
    protected abstract void executeOnPMP(IPMPService pmp) throws RemoteException;
    
}
