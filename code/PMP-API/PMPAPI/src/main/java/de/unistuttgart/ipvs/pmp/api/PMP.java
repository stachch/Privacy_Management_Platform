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
package de.unistuttgart.ipvs.pmp.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.api.handler.AbortIPCException;
import de.unistuttgart.ipvs.pmp.api.handler.PMPRegistrationHandler;
import de.unistuttgart.ipvs.pmp.api.handler.PMPRequestResourceHandler;
import de.unistuttgart.ipvs.pmp.api.handler.PMPRequestServiceFeaturesHandler;
import de.unistuttgart.ipvs.pmp.api.handler.PMPServiceFeatureUpdateHandler;
import de.unistuttgart.ipvs.pmp.api.handler._default.PMPDefaultRegistrationHandler;
import de.unistuttgart.ipvs.pmp.api.handler._default.PMPDefaultRequestSFHandler;
import de.unistuttgart.ipvs.pmp.api.ipc.IPCScheduler;
import de.unistuttgart.ipvs.pmp.api.ipc.command.IPC2PMPRegistrationCommand;
import de.unistuttgart.ipvs.pmp.api.ipc.command.IPC2PMPRequestResourceCommand;
import de.unistuttgart.ipvs.pmp.api.ipc.command.IPC2PMPRequestServiceFeaturesCommand;
import de.unistuttgart.ipvs.pmp.api.ipc.command.IPC2PMPUpdateServiceFeaturesCommand;
import de.unistuttgart.ipvs.pmp.api.ipc.command.IPCCommand;

/**
 * The main PMP API implementing all the calls.
 * 
 * @author Tobias Kuhn
 * 
 */
public class PMP implements IPMP {
    
    /**
     * The static instance of the API
     */
    private static volatile PMP instance = null;
    
    /**
     * The {@link Application} for which this API shall perform operations.
     */
    private Application application;
    
    /**
     * The {@link IPCScheduler} used to communicate with PMP.
     */
    private IPCScheduler scheduler;
    
    /**
     * The cache of Service Feature states.
     */
    private final ConcurrentMap<String, Boolean> sfsCache;
    
    /**
     * The cache of Resource {@link IBinder}s.
     */
    private final ConcurrentMap<PMPResourceIdentifier, IBinder> resCache;
    
    /**
     * The list of {@link PMPServiceFeatureUpdateHandler} to call on the next SF update.
     */
    private final BlockingQueue<PMPServiceFeatureUpdateHandler> callOnUpdate;
    
    
    /**
     * Gets the API for an application.
     * 
     * @param application
     *            the {@link Application} for which the API shall perform operations
     * @return the {@link IPMP} API for the application
     */
    public static IPMP get(Application application) {
        return getForService(application);
    }
    
    
    /**
     * Gets the API for the service implementation
     * 
     * @param application
     * @return
     */
    protected static PMP getForService(Application application) {
        if (instance == null) {
            instance = new PMP();
        }
        instance.setApplication(application);
        return instance;
    }
    
    
    /**
     * Gets the API, if an API for an application was previously requested. If not, consider calling
     * {@link PMP#get(Application))} before.
     * 
     * @return the {@link IPMP} API for the last application supplied
     * @throws IllegalAccessError
     *             if this method is called without ever specifying an {@link Application} via
     *             {@link PMP#get(Application)}.
     */
    public static IPMP get() {
        if (instance == null) {
            throw new IllegalAccessError("Tried to fetch an API without ever specifying an Application.");
        } else {
            return instance;
        }
    }
    
    
    private PMP() {
        this.sfsCache = new ConcurrentHashMap<String, Boolean>();
        this.resCache = new ConcurrentHashMap<PMPResourceIdentifier, IBinder>();
        this.callOnUpdate = new LinkedBlockingQueue<PMPServiceFeatureUpdateHandler>();
    }
    
    
    private void setApplication(Application application) {
        this.application = application;
        this.scheduler = new IPCScheduler(application);
    }
    
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + Integer.toHexString(hashCode());
    }
    
    
    /**
     * Method to be called when service features change, so they can be cached. Can be called from an arbitrary thread.
     * 
     * @param update
     */
    protected void onServiceFeatureUpdate(final Bundle update) {
        Log.d(this, " caching service features...");
        for (String sfId : update.keySet()) {
            Log.v(this, " received " + sfId + " = " + update.getBoolean(sfId));
            this.sfsCache.put(sfId, update.getBoolean(sfId));
        }
        
        while (!this.callOnUpdate.isEmpty()) {
            final PMPServiceFeatureUpdateHandler pmpsfuh = this.callOnUpdate.poll();
            new Thread() {
                
                @Override
                public void run() {
                    pmpsfuh.onUpdate(update);
                };
            }.start();
        }
    }
    
    
    /**
     * Method to be called when binders are received, so they can be cached. Can be called from an arbitrary thread.
     * 
     * @param resource
     * @param binder
     */
    protected void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
        Log.d(this, " caching resource...");
        if (binder != null) {
            this.resCache.put(resource, binder);
        }
    }
    
    
    /**
     * 
     * @param timeout
     *            the amount of ms in the future
     * @return the time point in {@link System#currentTimeMillis()} domain
     */
    private long makeTimeout(int timeout) {
        return (timeout == 0) ? Long.MAX_VALUE : System.currentTimeMillis() + timeout;
    }
    
    
    /*
     * interface methods
     */
    
    @Override
    public void register(Activity activity) {
        register(new PMPDefaultRegistrationHandler(activity), 0);
    }
    
    
    @Override
    public void register(PMPRegistrationHandler handler) {
        register(handler, 0);
    }
    
    
    @Override
    public void register(PMPRegistrationHandler handler, int timeout) {
        IPCCommand ipc = new IPC2PMPRegistrationCommand(handler, this.application.getPackageName(),
                makeTimeout(timeout));
        this.scheduler.queue(ipc);
    }
    
    
    @Override
    public void updateServiceFeatures() {
        updateServiceFeatures(null, 0);
    }
    
    
    @Override
    public void updateServiceFeatures(PMPServiceFeatureUpdateHandler handler) {
        updateServiceFeatures(handler, 0);
    }
    
    
    @Override
    public void updateServiceFeatures(PMPServiceFeatureUpdateHandler handler, int timeout) {
        IPCCommand ipc = new IPC2PMPUpdateServiceFeaturesCommand(handler, this.application.getPackageName(),
                makeTimeout(timeout));
        if (handler != null) {
            if (!this.callOnUpdate.offer(handler)) {
                Log.e(this, "Could not register handler to call on update.");
            }
        }
        this.scheduler.queue(ipc);
    }
    
    
    @Override
    public void requestServiceFeatures(Activity activity, List<String> serviceFeatures) {
        requestServiceFeatures(serviceFeatures, new PMPDefaultRequestSFHandler(activity), true, 0);
    }
    
    
    @Override
    public void requestServiceFeatures(Activity activity, String... serviceFeatures) {
        requestServiceFeatures(Arrays.asList(serviceFeatures), new PMPDefaultRequestSFHandler(activity), true, 0);
    }
    
    
    @Override
    public void requestServiceFeatures(List<String> serviceFeatures, PMPRequestServiceFeaturesHandler handler) {
        requestServiceFeatures(serviceFeatures, handler, true, 0);
    }
    
    
    @Override
    public void requestServiceFeatures(List<String> serviceFeatures, PMPRequestServiceFeaturesHandler handler,
            boolean showDialog) {
        requestServiceFeatures(serviceFeatures, handler, showDialog, 0);
    }
    
    
    @Override
    public void requestServiceFeatures(List<String> serviceFeatures, PMPRequestServiceFeaturesHandler handler,
            boolean showDialog, int timeout) {
        IPCCommand ipc = new IPC2PMPRequestServiceFeaturesCommand(serviceFeatures, handler,
                this.application.getPackageName(), makeTimeout(timeout));
        this.scheduler.queue(ipc);
    }
    
    
    @Override
    public void getResource(PMPResourceIdentifier resource) {
        getResource(resource, null, 0);
    }
    
    
    @Override
    public void getResource(PMPResourceIdentifier resource, PMPRequestResourceHandler handler) {
        getResource(resource, handler, 0);
    }
    
    
    @Override
    public void getResource(PMPResourceIdentifier resource, final PMPRequestResourceHandler handler, int timeout) {
        
        PMPRequestResourceHandler nativeHandler = new PMPRequestResourceHandler() {
            
            @Override
            public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
                PMP.this.onReceiveResource(resource, binder, isMocked);
                handler.onReceiveResource(resource, binder, isMocked);
            }
            
            
            @Override
            public void onPrepare() throws AbortIPCException {
                handler.onPrepare();
            }
            
            
            @Override
            public void onFinalize() {
                handler.onFinalize();
            }
            
            
            @Override
            public void onBindingFailed() {
                handler.onBindingFailed();
            }
            
            
            @Override
            public void onTimeout() {
                handler.onTimeout();
            }
        };
        
        IPCCommand ipc = new IPC2PMPRequestResourceCommand(resource, nativeHandler, this.application.getPackageName(),
                makeTimeout(timeout));
        this.scheduler.queue(ipc);
    }
    
    
    @Override
    public Map<String, Boolean> getServiceFeatures() {
        return new HashMap<String, Boolean>(this.sfsCache);
    }
    
    
    @Override
    public boolean isServiceFeatureEnabled(String serviceFeature) {
        Boolean result = this.sfsCache.get(serviceFeature);
        return (result != null) ? result : false;
    }
    
    
    @Override
    public boolean areServiceFeaturesEnabled(List<String> serviceFeatures) {
        for (String serviceFeature : serviceFeatures) {
            if (!isServiceFeatureEnabled(serviceFeature)) {
                return false;
            }
        }
        return true;
    }
    
    
    @Override
    public boolean areServiceFeaturesEnabled(String... serviceFeatures) {
        return areServiceFeaturesEnabled(Arrays.asList(serviceFeatures));
    }
    
    
    @Override
    public boolean areServiceFeaturesDisabled(List<String> serviceFeatures) {
        for (String serviceFeature : serviceFeatures) {
            if (isServiceFeatureEnabled(serviceFeature)) {
                return false;
            }
        }
        return true;
    }
    
    
    @Override
    public boolean areServiceFeaturesDisabled(String... serviceFeatures) {
        return areServiceFeaturesDisabled(Arrays.asList(serviceFeatures));
    }
    
    
    @Override
    public List<String> listEnabledServiceFeatures(List<String> serviceFeatures) {
        List<String> result = new ArrayList<String>(serviceFeatures.size());
        for (String serviceFeature : serviceFeatures) {
            if (isServiceFeatureEnabled(serviceFeature)) {
                result.add(serviceFeature);
            }
        }
        return result;
    }
    
    
    @Override
    public List<String> listEnabledServiceFeatures(String... serviceFeatures) {
        return listEnabledServiceFeatures(Arrays.asList(serviceFeatures));
    }
    
    
    @Override
    public List<String> listDisabledServiceFeatures(List<String> serviceFeatures) {
        List<String> result = new ArrayList<String>(serviceFeatures.size());
        for (String serviceFeature : serviceFeatures) {
            if (!isServiceFeatureEnabled(serviceFeature)) {
                result.add(serviceFeature);
            }
        }
        return result;
    }
    
    
    @Override
    public List<String> listDisabledServiceFeatures(String... serviceFeatures) {
        return listDisabledServiceFeatures(Arrays.asList(serviceFeatures));
    }
    
    
    @Override
    public Set<String> listAllServiceFeatures() {
        return new HashSet<String>(this.sfsCache.keySet());
    }
    
    
    @Override
    public boolean isResourceCached(PMPResourceIdentifier resource) {
        IBinder cached = this.resCache.get(resource);
        return (cached != null) && (cached.pingBinder());
    }
    
    
    @Override
    public IBinder getResourceFromCache(PMPResourceIdentifier resource) {
        return this.resCache.get(resource);
    }
    
}
