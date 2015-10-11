package de.unistuttgart.ipvs.pmp.resourcegroups.location.aidl;

/**
 * The IAbsoluteLocation interface is used to get the current location
 * based on current GPS information.
 * The IAbsoluteLocation can only be used, when the Privacy Setting
 * "useAbsoluteLocation" is set to "true".
 *
 * @author Jakob Jarosch
 */
interface IAbsoluteLocation {

	/**
	 * This method has to be called before a location or other informations
	 * can be fetch from the other methods.
	 * When this method is called GPS has to be enabled.
	 * If GPS is disabled a notification will be created, the User has to enable
	 * GPS manually. Until this change is not made, no location will be returned.
	 * The method {@link IAbsoluteLocation#isGpsEnabled()} can be used to check if
	 * GPS is enabled.
	 *
	 * Requires: useAbsoluteLocation=true
	 *
	 * @param minTime The minimal time between two updates.
	 * @param minDistance The minimal distance between two updates.
	 */
	void startLocationLookup(long minTime, float minDistance);
	
	/**
	 * Ends the update requests for GPS. Call this always before your App exits.
	 *
	 * Requires: useAbsoluteLocation=true
	 */
	void endLocationLookup();

	/**
	 * Requires: useAbsoluteLocation=true
	 *
	 * @return Returns whether GPS is currently enabled or not.
	 */
	boolean isGpsEnabled();
	
	/**
	 * Requires: useAbsoluteLocation=true
	 * 
	 * @return Returns true when the GPS location fetch is active.
	 *         Returns false, when not startLocationLookup has been called,
	 *         or the time between two method calls was longer than minTime.
	 */
	boolean isActive();
	
	/**
	 * Requires: useAbsoluteLocation=true
	 *
	 * @return Returns whether the GPS module can calculate a position or not.
	 */
	boolean isFixed();
	
	/**
	 * Requires: useAbsoluteLocation=true
	 *
	 * @return Returns true if the constraints used when calling
	 *  startLocationLookup triggers an update.
	 */
	boolean isUpdateAvailable();
	
	/**
	 * Requires: useAbsoluteLocation=true, useCoordinates=true
	 *
	 * @return Returns the current longitude.
	 */
	double getLongitude();
	
	/**
	 * Requires: useAbsoluteLocation=true, useCoordinates=true
	 *
	 * @return Returns the current latitude.
	 */
	double getLatitude();
	
	/**
	 * Returns the accuracy of the GPS signal. The accuracy will never be better than the
	 * Privacy Setting "locationPrecision". Better precision means the user is better traceable,
	 * but he may rejects a more detailed location.
	 *
	 * Requires: useAbsoluteLocation=true, useAccuracy=true
	 *
	 * @return Returns the current accuracy of the GPS signal.
	 */
	float getAccuracy();
	
	/**
	 * Returns the current speed of the device.
	 *
	 * Requires: useAbsoluteLocation=true, useSpeed=true
	 * 
	 * @return Returns the current speed of the device, in kilometers per hour.
	 */
	float getSpeed();
	
	/**
	 * Requires: useAbsoluteLocation=true, useLocationDescription >= COUNTRY
	 *
	 * @return Returns the current country code, or null of no country code
	 *         could be fetched, could be possible if the IO failed.
	 */
	String getCountryCode();
	
	/**
	 * Requires: useAbsoluteLocation=true, useLocationDescription >= COUNTRY
	 *
	 * @return Returns the current country, or null of no country
	 *         could be fetched, could be possible if the IO failed.
	 */
	String getCountryName();
	
	/**
	 * Requires: useAbsoluteLocation=true, useLocationDescription >= CITY
	 *
	 * @return Returns the current locality, or null of no locality
	 *         could be fetched, could be possible if the IO failed.
	 */
	String getLocality();
	
	/**
	 * Requires: useAbsoluteLocation=true, useLocationDescription >= CITY
	 *
	 * @return Returns the current postal code of the city, or null of no postal code
	 *         could be fetched, could be possible if the IO failed.
	 */
	String getPostalCode();
	
	/**
	 * Requires: useAbsoluteLocation=true, useLocationDescription = STREET
	 *
	 * @return Returns the current street, or null of no street
	 *         could be fetched, could be possible if the IO failed.
	 */
	String getAddress();
}