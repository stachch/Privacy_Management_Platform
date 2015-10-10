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
package de.unistuttgart.ipvs.pmp.api.handler;

import de.unistuttgart.ipvs.pmp.api.ipc.command.IPC2PMPRegistrationCommand;

/**
 * Handles reactions for the {@link IPC2PMPRegistrationCommand}. Handlers are guaranteed to be called in a separate
 * {@link Thread}.
 * 
 * 
 * @author Tobias Kuhn
 * 
 */
public class PMPRegistrationHandler extends PMPHandler {
    
    /**
     * Called when the registration cannot be completed because the app is already registered.
     */
    public void onAlreadyRegistered() {
    }
    
    
    /**
     * Called when the registration will start, i.e. before the actual registration takes place.
     */
    public void onRegistration() {
    }
    
    
    /**
     * Called when the registration was successful.
     */
    public void onSuccess() {
    }
    
    
    /**
     * Called when the registration failed.
     * 
     * @param message
     *            the failure message
     */
    public void onFailure(String message) {
    }
    
}
