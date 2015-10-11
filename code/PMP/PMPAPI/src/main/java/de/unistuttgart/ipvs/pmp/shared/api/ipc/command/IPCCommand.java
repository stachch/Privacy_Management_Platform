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
import de.unistuttgart.ipvs.pmp.shared.api.handler.PMPHandler;

/**
 * The basic IPC command to issue. All IPC commands must extend this functionality.
 * 
 * @author Tobias Kuhn
 * 
 */
public abstract class IPCCommand {
    
    /**
     * The timeout point in the {@link System#currentTimeMillis()} domain.
     */
    private final long timeout;
    
    /**
     * The identifier of the IPC source
     */
    private final String sourceService;
    
    /**
     * The identifier of the IPC destination
     */
    private final String destinationService;
    
    /**
     * The handler associated with this {@link IPCCommand}.
     */
    private final PMPHandler handler;
    
    
    /**
     * Creates a new basic {@link IPCCommand}.
     * 
     * @param handler
     *            the handler associated with this command
     * @param sourceService
     *            the application or service creating this command
     * @param destinationService
     *            the service this command is directed to
     */
    public IPCCommand(PMPHandler handler, String sourceService, String destinationService) {
        this(handler, sourceService, destinationService, Long.MAX_VALUE);
    }
    
    
    /**
     * Creates a new basic {@link IPCCommand}.
     * 
     * @param handler
     *            the handler associated with this command
     * @param sourceService
     *            the application or service creating this command
     * @param destinationService
     *            the service this command is directed to
     * @param timeout
     *            timeout point in the {@link System#currentTimeMillis()} domain
     */
    public IPCCommand(PMPHandler handler, String sourceService, String destinationService, long timeout) {
        if (handler != null) {
            this.handler = handler;
        } else {
            this.handler = getNullHandler();
        }
        this.sourceService = sourceService;
        this.destinationService = destinationService;
        this.timeout = timeout;
    }
    
    
    /**
     * 
     * @return a null handler, i.e. a handler that is not literally "null" but performs no operation whatsoever.
     */
    protected abstract PMPHandler getNullHandler();
    
    
    public long getTimeout() {
        return this.timeout;
    }
    
    
    public String getSourceService() {
        return this.sourceService;
    }
    
    
    public String getDestinationService() {
        return this.destinationService;
    }
    
    
    public PMPHandler getHandler() {
        return this.handler;
    }
    
    
    /**
     * Executes the command on the binder binder. Supposed to be overridden.
     * 
     * @param binder
     *            the {@link IBinder} that was got by requesting the binder from the destinationIdentifier service
     */
    public abstract void execute(IBinder binder);
    
}
