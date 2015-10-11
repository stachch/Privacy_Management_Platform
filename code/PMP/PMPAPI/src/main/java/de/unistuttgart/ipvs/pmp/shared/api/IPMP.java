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
package de.unistuttgart.ipvs.pmp.shared.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.shared.api.gui.registration.RegistrationActivity;
import de.unistuttgart.ipvs.pmp.shared.api.handler.PMPHandler;
import de.unistuttgart.ipvs.pmp.shared.api.handler.PMPRegistrationHandler;
import de.unistuttgart.ipvs.pmp.shared.api.handler.PMPRequestResourceHandler;
import de.unistuttgart.ipvs.pmp.shared.api.handler.PMPRequestServiceFeaturesHandler;
import de.unistuttgart.ipvs.pmp.shared.api.handler.PMPServiceFeatureUpdateHandler;

/**
 * The interface contract for all the API functions that can possibly be called.
 * 
 * @author Tobias Kuhn
 *         
 */
public interface IPMP {
    
    /*
     * IPC call methods
     */
    
    /**
     * <p>
     * Sends a registration request to PMP and obtains the currently assigned service features. Handles the displaying
     * of GUI elements to indicate to the user that the registration process is running.
     * </p>
     * 
     * <p>
     * Calling this method is an easy way to perform the registration with getting all the GUI elements handled for you.
     * If you want an even simpler way, consider using the {@link RegistrationActivity}.
     * </p>
     * 
     * @param activity
     *            the {@link Activity} in which contexts the dialogs shall be shown
     */
    public void register(Activity activity);
    
    
    /**
     * Sends a registration request to PMP and obtains the currently assigned service features. The result can be
     * obtained in handler, which will be called in a separate {@link Thread}.
     * 
     * @param handler
     *            the {@link PMPRegistrationHandler} to call in a separate {@link Thread} when certain actions happen
     */
    public void register(PMPRegistrationHandler handler);
    
    
    /**
     * Sends a registration request to PMP and obtains the currently assigned service features. The result can be
     * obtained in handler, which will be called in a separate {@link Thread}. Gives opportunity to specify a maximum
     * milliseconds timeout.
     * 
     * @param handler
     *            the {@link PMPRegistrationHandler} to call in a separate {@link Thread} when certain actions happen
     * @param timeout
     *            The amount of milliseconds in the future which designates the latest desirable point in time to
     *            execute the registration. If it can only be executed afterwards, the {@link PMPHandler#onTimeout()}
     *            callback is called instead of performing the registration. Use 0 to designate infinite timeout.
     */
    public void register(PMPRegistrationHandler handler, int timeout);
    
    
    /**
     * Sends a service feature update request to PMP to be stored in the API cache that can be accessed after an
     * arbitrary interval (<i>not immediately</i>) by various methods like for example
     * {@link IPMP#isServiceFeatureEnabled(String)}, {@link IPMP#areServiceFeaturesEnabled(String...)} or
     * {@link IPMP#getServiceFeatures()}.
     */
    public void updateServiceFeatures();
    
    
    /**
     * Sends a service feature update request to PMP. The result can be obtained in handler, which will be called in a
     * separate {@link Thread}. Stores the response in the API cache.
     * 
     * @param handler
     *            the {@link PMPServiceFeatureUpdateHandler} to call in a separate {@link Thread} when certain actions
     *            happen
     */
    public void updateServiceFeatures(PMPServiceFeatureUpdateHandler handler);
    
    
    /**
     * Sends a service feature update request to PMP. The result can be obtained in handler, which will be called in a
     * separate {@link Thread}. Stores the response in the API cache. Gives opportunity to specify a maximum
     * milliseconds timeout.
     * 
     * @param handler
     *            the {@link PMPServiceFeatureUpdateHandler} to call in a separate {@link Thread} when certain actions
     *            happen
     * @param timeout
     *            The amount of milliseconds in the future which designates the latest desirable point in time to
     *            execute the update request. If it can only be executed afterwards, the {@link PMPHandler#onTimeout()}
     *            callback is called instead of performing the registration. Use 0 to designate infinite timeout.
     */
    public void updateServiceFeatures(PMPServiceFeatureUpdateHandler handler, int timeout);
    
    
    /**
     * Sends a service feature request to PMP, i.e. a request to activate specific service features because
     * functionality was requested that requires these service features. Shows a user dialog to first confirm this is
     * intended.
     * 
     * @param serviceFeatures
     *            the service features that shall be requested
     * @param activity
     *            the {@link Activity} in which contexts the dialogs shall be shown
     *            
     */
    public void requestServiceFeatures(Activity activity, List<String> serviceFeatures);
    
    
    /**
     * Sends a service feature request to PMP, i.e. a request to activate specific service features because
     * functionality was requested that requires these service features. Shows a user dialog to first confirm this is
     * intended.
     * 
     * @param serviceFeatures
     *            the service features that shall be requested
     * @param activity
     *            the {@link Activity} in which contexts the dialogs shall be shown
     */
    public void requestServiceFeatures(Activity activity, String... serviceFeatures);
    
    
    /**
     * Sends a service feature request to PMP, i.e. a request to activate specific service features because
     * functionality was requested that requires these service features. Gives opportunity not to show a user dialog to
     * first confirm this is intended.
     * 
     * @param serviceFeatures
     *            the service features that shall be requested
     * @param handler
     *            the {@link PMPRequestServiceFeaturesHandler} to call in a separate {@link Thread} when certain actions
     *            happen
     */
    public void requestServiceFeatures(List<String> serviceFeatures, PMPRequestServiceFeaturesHandler handler);
    
    
    /**
     * Sends a service feature request to PMP, i.e. a request to activate specific service features because
     * functionality was requested that requires these service features. Gives opportunity not to show a user dialog to
     * first confirm this is intended.
     * 
     * @param serviceFeatures
     *            the service features that shall be requested
     * @param handler
     *            the {@link PMPRequestServiceFeaturesHandler} to call in a separate {@link Thread} when certain actions
     *            happen
     * @param showDialog
     *            whether to display a dialog asking the user whether this action was intentional and whether he or she
     *            wants to change the service features
     */
    public void requestServiceFeatures(List<String> serviceFeatures, PMPRequestServiceFeaturesHandler handler,
            boolean showDialog);
            
            
    /**
     * Sends a service feature request to PMP, i.e. a request to activate specific service features because
     * functionality was requested that requires these service features. Gives opportunity not to show a user dialog to
     * first confirm this is intended. Gives opportunity to specify a maximum milliseconds timeout.
     * 
     * @param serviceFeatures
     *            the service features that shall be requested
     * @param handler
     *            the {@link PMPRequestServiceFeaturesHandler} to call in a separate {@link Thread} when certain actions
     *            happen
     * @param showDialog
     *            whether to display a dialog asking the user whether this action was intentional and whether he or she
     *            wants to change the service features
     * @param timeout
     *            The amount of milliseconds in the future which designates the latest desirable point in time to
     *            execute the service feature request. If it can only be executed afterwards, the
     *            {@link PMPHandler#onTimeout()} callback is called instead of performing the registration. Use 0 to
     *            designate infinite timeout.
     */
    public void requestServiceFeatures(List<String> serviceFeatures, PMPRequestServiceFeaturesHandler handler,
            boolean showDialog, int timeout);
            
            
    /**
     * Sends a resource request to PMP, i.e. a request to get the {@link IBinder} for a specific resource to be stored
     * in the API cache that can be accessed after an arbitrary interval (<i>not immediately</i>) by
     * {@link IPMP#isResourceCached(PMPResourceIdentifier)} and {@link IPMP#getResourceFromCache(PMPResourceIdentifier)}
     * .
     * 
     * @param resource
     *            the resource for which the IBinder shall be requested
     */
    public void getResource(PMPResourceIdentifier resource);
    
    
    /**
     * Sends a resource request to PMP, i.e. a request to get the {@link IBinder} for a specific resource. The result
     * can be obtained in handler, which will be called in a separate {@link Thread}. Stores the response in the API
     * cache.
     * 
     * @param resource
     *            the resource for which the IBinder shall be requested
     * @param handler
     *            the {@link PMPRequestResourceHandler} to call in a separate {@link Thread} when certain actions
     *            happen
     */
    public void getResource(PMPResourceIdentifier resource, PMPRequestResourceHandler handler);
    
    
    /**
     * Sends a resource request to PMP, i.e. a request to get the {@link IBinder} for a specific resource. The result
     * can be obtained in handler, which will be called in a separate {@link Thread}. Stores the response in the API
     * cache. Gives opportunity to specify a maximum milliseconds timeout.
     * 
     * @param resource
     *            the resource for which the IBinder shall be requested
     * @param handler
     *            the {@link PMPRequestResourceHandler} to call in a separate {@link Thread} when certain actions
     *            happen
     * @param timeout
     *            The amount of milliseconds in the future which designates the latest desirable point in time to
     *            execute the resource request. If it can only be executed afterwards, the
     *            {@link PMPHandler#onTimeout()} callback is called instead of performing the registration. Use 0 to
     *            designate infinite timeout.
     */
    public void getResource(PMPResourceIdentifier resource, PMPRequestResourceHandler handler, int timeout);
    
    
    /*
     * Cached Service Feature methods
     */
    
    /**
     * Accesses the API service feature cache.
     * 
     * @return a {@link Map} that maps the service feature identifier to its last known state in PMP
     */
    public Map<String, Boolean> getServiceFeatures();
    
    
    /**
     * Accesses the API service feature cache.
     * 
     * @param serviceFeature
     *            the service feature to look for
     * @return true, if the service feature was enabled in the last known state in PMP, false otherwise
     */
    public boolean isServiceFeatureEnabled(String serviceFeature);
    
    
    /**
     * Accesses the API service feature cache.
     * 
     * @param serviceFeatures
     *            the list of service features to check to be enabled
     * @return true, if all the service features were enabled in the last known state in PMP, false otherwise
     */
    public boolean areServiceFeaturesEnabled(List<String> serviceFeatures);
    
    
    /**
     * Accesses the API service feature cache.
     * 
     * @param serviceFeatures
     *            the service features to check to be enabled
     * @return true, if all the service features were enabled in the last known state in PMP, false otherwise
     */
    public boolean areServiceFeaturesEnabled(String... serviceFeatures);
    
    
    /**
     * Accesses the API service feature cache.
     * 
     * @param serviceFeatures
     *            the list of service features to check to be disabled
     * @return true, if all the service features were disabled in the last known state in PMP, false otherwise
     */
    public boolean areServiceFeaturesDisabled(List<String> serviceFeatures);
    
    
    /**
     * Accesses the API service feature cache.
     * 
     * @param serviceFeatures
     *            the service features to check to be disabled
     * @return true, if all the service features were disabled in the last known state in PMP, false otherwise
     */
    public boolean areServiceFeaturesDisabled(String... serviceFeatures);
    
    
    /**
     * Accesses the API service feature cache.
     * 
     * @param serviceFeatures
     *            the list of service features to list, if they are enabled
     * @return a list of all the service features passed that were actually enabled in the last known state in PMP
     */
    public List<String> listEnabledServiceFeatures(List<String> serviceFeatures);
    
    
    /**
     * Accesses the API service feature cache.
     * 
     * @param serviceFeatures
     *            the service features to list, if they are enabled
     * @return a list of all the service features passed that were actually enabled in the last known state in PMP
     */
    public List<String> listEnabledServiceFeatures(String... serviceFeatures);
    
    
    /**
     * Accesses the API service feature cache.
     * 
     * @param serviceFeatures
     *            the list of service features to list, if they are disabled
     * @return a list of all the service features passed that were actually disabled in the last known state in PMP
     */
    public List<String> listDisabledServiceFeatures(List<String> serviceFeatures);
    
    
    /**
     * Accesses the API service feature cache.
     * 
     * @param serviceFeatures
     *            the service features to list, if they are disabled
     * @return a list of all the service features passed that were actually disabled in the last known state in PMP
     */
    public List<String> listDisabledServiceFeatures(String... serviceFeatures);
    
    
    /**
     * Accesses the API service feature cache.
     * 
     * @return a set of all the service features known in the API cache.
     */
    public Set<String> listAllServiceFeatures();
    
    
    /*
     * Cached Resource binder methods
     */
    
    /**
     * Accesses the API resource cache.
     * 
     * @param resource
     *            the resource that shall be checked
     * @return true, if the resource is cached and still connected, false otherwise
     */
    public boolean isResourceCached(PMPResourceIdentifier resource);
    
    
    /**
     * Accesses the API resource cache.
     * 
     * @param resource
     *            the resource that shall be accessed
     * @return the still connected {@link IBinder} for the resource that was cached, null if it is not available
     */
    public IBinder getResourceFromCache(PMPResourceIdentifier resource);
}
