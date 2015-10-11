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
package de.unistuttgart.ipvs.pmp.shared.api.ipc;

import java.util.concurrent.Semaphore;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.shared.IntentHelper;
import de.unistuttgart.ipvs.pmp.shared.Log;

/**
 * <p>
 * The very basic keep-alive {@link ServiceConnection} for PMP IPC communication channels.
 * </p>
 * 
 * <p>
 * This object may not be used in the main thread since its logic relies on Android calling the
 * {@link ServiceConnection} callbacks in the main thread which would produce a dead lock, if the {@link IPCConnection}
 * was created in the main thread.
 * </p>
 * 
 * <p>
 * This object is not thread-safe, i.e. all methods should only be called by one thread with the exception of the
 * Android main thread allowed to call the {@link ServiceConnection} callbacks.
 * </p>
 * 
 * @author Tobias Kuhn
 *         
 */
public class IPCConnection implements ServiceConnection {
    
    /**
     * The {@link Context} in which the connection is established.
     */
    private final Context context;
    
    /**
     * The destination of this {@link IPCConnection}.
     */
    private String destinationService;
    
    /**
     * whether the {@link IPCConnection} is currently connected.
     */
    private boolean connected;
    
    /**
     * The {@link Semaphore} used to block the context thread until the main thread has received the {@link IBinder}
     * associated with the service to connect to.
     */
    private final Semaphore waitForBinder;
    
    /**
     * The last {@link IBinder} that was received for the connected service.
     */
    private IBinder lastBinder;
    
    
    /**
     * Creates a new, unconnected {@link IPCConnection}.
     * 
     * @param context
     *            the context to use for the connection
     * @throws IllegalAccessError
     *             if the {@link IPCConnection} is created on the main thread which would produce a dead lock during
     *             usage
     */
    public IPCConnection(Context context) {
        if (Thread.currentThread().getName().equalsIgnoreCase("main")) {
            throw new IllegalAccessError("IPCConnection is not allowed on the main thread.");
        }
        
        this.context = context;
        this.waitForBinder = new Semaphore(0);
    }
    
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + Integer.toHexString(hashCode());
    }
    
    
    /**
     * Sets the destination service to which the connection shall be established. Disconnects the connection, if it is
     * already connected. Does not connect to the newly set destination service.
     * 
     * @param destinationService
     *            the identifier of the service to connect to
     */
    public void setDestinationService(String destinationService) {
        if (destinationService.equals(this.destinationService)) {
            return;
        }
        
        if (this.connected) {
            disconnect();
        }
        this.destinationService = destinationService;
    }
    
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.v(this, " onServiceConnected(" + name.flattenToString() + ")");
        this.connected = true;
        this.lastBinder = service;
        this.waitForBinder.release();
    }
    
    
    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.v(this, " onServiceDisconnected(" + name.flattenToString() + ")");
        disconnect();
    }
    
    
    /**
     * Tries to obtain an {@link IBinder} interface for the connection. Connects to the service, if not yet connected.
     * 
     * @return the {@link IBinder}, if it is either cached or could be obtained during the connection; null, if either
     *         the connection was not possible or the thread was interrupted while waiting for the newly connected
     *         {@link IBinder}.
     */
    public IBinder getBinder() {
        // check the cached one
        if (this.connected) {
            Log.d(this, " has binder still cached");
            return this.lastBinder;
        }
        
        // try connecting
        if (!connect()) {
            Log.e(this, " failed connection attempt");
            return null;
        }
        
        // try getting the binder
        try {
            this.waitForBinder.acquire();
            return this.lastBinder;
        } catch (InterruptedException ie) {
            Log.e(this, " interrupted while waiting for binder");
            return null;
        }
    }
    
    
    /**
     * Connects to the destination service.
     * 
     * @return true, if successfully bound to the service; false, if the connection is not made
     */
    private boolean connect() {
        Log.d(this, " connecting to " + this.destinationService);
        Intent service = new Intent(this.destinationService);
        IntentHelper.upgradeIntent(context, service);
        return this.context.bindService(service, this, Context.BIND_AUTO_CREATE);
    }
    
    
    /**
     * Disconnects this connection from any service.
     */
    public void disconnect() {
        Log.d(this, " disconnect");
        this.context.unbindService(this);
        this.connected = false;
    }
    
}
