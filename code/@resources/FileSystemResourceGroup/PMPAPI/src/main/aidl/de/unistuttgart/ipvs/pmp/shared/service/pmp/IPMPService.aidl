package de.unistuttgart.ipvs.pmp.shared.service.pmp;

import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.shared.service.pmp.RegistrationResult;

/**
 * The Service of PMP provided for an app.
 * 
 * @author Jakob Jarosch
 */
interface IPMPService {
    
    /**
     * A registered app can call this method to gain an arbitrary (but most likely the first initial) update on all
     * the service features. The {@link PMPService} will then call concurrently the app service's
     * {@link IAppServicePMP#setServiceFeature(Integer)}.
     * 
     * @param appPackage
     *            the identifier for the app to receive the service feature update
     * @return true, if the service feature will be performed, false, if the app wasn't found
     */
    boolean getServiceFeatureUpdate(String appPackage);
    
    
    /**
     * Method for registering a new app at PMP.
     * 
     * @param appPackage
     *            the identifier for the app to register
     *            @return the result of the registration
     */
    RegistrationResult registerApp(String appPackage);
    
    
    /**
     * Method for checking whether an app is registered with PMP or not.
     * 
     * @param appPackage
     *            the identifier for the app to check registration for
     * @return true, if and only if the app with identifier is registered with PMP
     */
    boolean isRegistered(String appPackage);
    
    
    /**
     * Gets an AIDL resource.
     * 
     * @param appPackage
     *            the identifier for the app that requests the resource
     * @param rgPackage
     *            the identifier for the RG that is requested
     * @param resource
     *            the name of the resource
     * @return the IBinder interface for the resource of the resourceGroup, or null, if an error happened
     *         (e.g. resource not found)
     */
    IBinder getResource(String appPackage, String rgPackage, String resource);
    
    
    /**
     * A registered app can call this method to request several service features to be enabled for functionality to
     * work.
     * 
     * @param appPackage
     *            the identifier for the app that requests the service features
     * @param requiredServiceFeatures
     *            the names of the service features to request
     */
    boolean requestServiceFeature(String appPackage, in String[] requiredServiceFeatures);
    
    /**
     * Whether the resource is mocked.
     * @param appPackage
     *            the identifier for the app that requests the status
     * @param rgPackage 
     *            the identifier for the RG whose status is requested
     */
     boolean isMocked(String appPackage, String rgPackage);
}
