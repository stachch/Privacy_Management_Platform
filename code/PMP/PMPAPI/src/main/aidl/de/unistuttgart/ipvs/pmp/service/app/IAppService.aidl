package de.unistuttgart.ipvs.pmp.service.app;

/**
 * The IAppService is provided by an application, over the interface
 * it's possible to get name, description and ServiceFeatures from the App.
 *
 * @author Jakob Jarosch
 */
interface IAppService {
	
	 /**
     * Informs the app about its currently enabled i.e. active service features. 
     * 
     * @param features
     *            the Bundle that contains the mappings of strings (the identifiers of the service features in the app
     *            description XML) to booleans (true for granted i.e. active, false for not granted)
     */
    void updateServiceFeatures(in Bundle features);		

}