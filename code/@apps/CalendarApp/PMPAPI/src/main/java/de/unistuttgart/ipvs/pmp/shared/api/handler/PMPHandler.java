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

/**
 * General handler that reacts on any IPC command. Handlers are guaranteed to be called in a separate {@link Thread}.
 * 
 * @author Tobias Kuhn
 * 
 */
public abstract class PMPHandler {
    
    /**
     * Called before the IPC connection is established.
     * 
     * @throws AbortIPCException
     *             if and only if you wish to abort the IPC before it starts
     */
    public void onPrepare() throws AbortIPCException {
    }
    
    
    /**
     * Called after the command was executed.
     */
    public void onFinalize() {
    }
    
    
    /**
     * Called whenever a binding failure occurs. Typical reasons are for example the destination service (e.g. PMP) is
     * not installed on the device or an error occured during the message transmit.
     */
    public void onBindingFailed() {
    }
    
    
    /**
     * Called when a specified timeout was exceeded.
     */
    public void onTimeout() {
    }
}
