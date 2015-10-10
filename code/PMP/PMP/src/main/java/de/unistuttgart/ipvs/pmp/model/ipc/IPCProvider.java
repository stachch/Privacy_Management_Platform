/*
 * Copyright 2012 pmp-android development team
 * Project: PMP
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
package de.unistuttgart.ipvs.pmp.model.ipc;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.api.ipc.IPCConnection;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelMisuseError;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.ServiceFeature;
import de.unistuttgart.ipvs.pmp.service.app.IAppService;

/**
 * General IPC provider which provides all the inter-process communication necessary for the model.
 * 
 * @author Tobias Kuhn
 * 
 */
public class IPCProvider {
    
    /**
     * How many cumulative update sessions are in progress, for > 0 no rollout should be done.
     */
    private final AtomicInteger updateSession;
    
    /**
     * The map containing the IPC operations to be performed.
     */
    protected final ConcurrentMap<String, Bundle> queue;
    
    /**
     * Singleton stuff
     */
    private static final IPCProvider instance = new IPCProvider();
    
    
    public static IPCProvider getInstance() {
        return instance;
    }
    
    
    /**
     * Singleton constructor
     */
    private IPCProvider() {
        this.updateSession = new AtomicInteger(0);
        this.queue = new ConcurrentHashMap<String, Bundle>();
    }
    
    
    /**
     * Starts one cumulative update session. This means, the IPC provider will start buffering IPC messages instead of
     * directly delivering them directly. Be sure to always call {@link IPCProvider#endUpdate()} afterwards.
     */
    public synchronized void startUpdate() {
        this.updateSession.incrementAndGet();
        Log.d(this, "IPC delayed update layer " + String.valueOf(this.updateSession) + " started.");
    }
    
    
    /**
     * Ends one cumulative update session started by {@link IPCProvider#startUpdate()}.
     */
    public synchronized void endUpdate() {
        Log.d(this, "IPC delayed update layer " + String.valueOf(this.updateSession) + " ended.");
        if (this.updateSession.get() > 0) {
            this.updateSession.decrementAndGet();
        }
        if (this.updateSession.intValue() == 0) {
            rollout();
        }
    }
    
    
    /**
     * Rolls-out all queued up IPC operations.
     */
    private synchronized void rollout() {
        Log.d(this, "Performing IPC rollout...");
        
        // launch a new Thread, that's cool these days
        
        new Thread("IPC rollout") {
            
            @Override
            public void run() {
                IPCConnection con = new IPCConnection(PMPApplication.getContext());
                
                Set<String> latestKeySet = IPCProvider.this.queue.keySet();
                for (final String key : latestKeySet) {
                    // N.B. we must not use an entry set because due to concurrency the entry could get deleted
                    //      by another execution before we access it.
                    
                    con.setDestinationService(key);
                    IBinder appBinder = con.getBinder();
                    
                    String id = "?";
                    try {
                        id = appBinder.getInterfaceDescriptor();
                    } catch (RemoteException re) {
                        Log.e(this, "Remote exception while getting interface descriptor: ", re);
                    }
                    
                    if (!id.equals(IAppService.class.getName())) {
                        Log.e(this, "Binder to " + key + " was not IAppService but " + id);
                    }
                    
                    IAppService as = IAppService.Stub.asInterface(appBinder);
                    Bundle value = IPCProvider.this.queue.remove(key);
                    if (value != null) {
                        try {
                            as.updateServiceFeatures(value);
                        } catch (RemoteException re) {
                            Log.e(this, "Remote exception while updating service features for " + key + ": ", re);
                        }
                    } else {
                        Log.d(this, "Rollout value went missing while iterating through key list");
                    }
                }
                
            }
        }.start();
    }
    
    
    /**
     * Queues an IPC operation to be done. Might not be done immediately, if a cumulative update session is in progress.
     * 
     * @param appPackage
     *            the package of the app
     * @param verification
     *            a map from the app's service features to boolean whereas the mapping should be true if and only if the
     *            service feature is active i.e. granted
     */
    public synchronized void queue(String appPackage, Map<ServiceFeature, Boolean> verification) {
        Assert.nonNull(appPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "appPackage", appPackage);
        Assert.nonNull(verification, ModelMisuseError.class, Assert.ILLEGAL_NULL, "verification", verification);
        
        // create the new bundle
        Bundle b = new Bundle();
        for (Entry<ServiceFeature, Boolean> e : verification.entrySet()) {
            b.putBoolean(e.getKey().getLocalIdentifier(), e.getValue());
        }
        
        this.queue.put(appPackage, b);
        
        // run, if no session
        if (this.updateSession.intValue() == 0) {
            rollout();
        } else {
            Log.d(this, "IPC connection queued.");
        }
    }
    
}
