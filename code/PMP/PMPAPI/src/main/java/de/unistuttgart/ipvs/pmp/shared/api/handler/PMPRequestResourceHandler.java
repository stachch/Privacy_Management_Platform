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
package de.unistuttgart.ipvs.pmp.shared.api.handler;

import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.shared.api.PMPResourceIdentifier;
import de.unistuttgart.ipvs.pmp.shared.api.ipc.command.IPC2PMPRequestResourceCommand;

/**
 * Handles reactions for the {@link IPC2PMPRequestResourceCommand}. Handlers are guaranteed to be called in a separate
 * {@link Thread}.
 * 
 * 
 * @author Tobias Kuhn
 * 
 */
public class PMPRequestResourceHandler extends PMPHandler {
    
    /**
     * Called when the resource is received.
     * 
     * @param resource
     *            the identifier of the resource
     * @param binder
     *            the AIDL interface for the resource, or null, if no such resource exists
     * @param isMocked
     *            whether the resource returns obvious fake data
     */
    public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
    }
}
