/*
 * Copyright 2012 pmp-android development team
 * Project: LocationResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.location;

import de.unistuttgart.ipvs.pmp.resourcegroups.location.resource.AbsoluteLocationResource;
import de.unistuttgart.ipvs.pmp.shared.resource.IPMPConnectionInterface;
import de.unistuttgart.ipvs.pmp.shared.resource.ResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.BooleanPrivacySetting;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.EnumPrivacySetting;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.IntegerPrivacySetting;

public class LocationResourceGroup extends ResourceGroup {
    
    public static final String PACKAGE_NAME = "de.unistuttgart.ipvs.pmp.resourcegroups.location";
    
    public static final String R_ABSOLUTE_LOCATION = "absoluteLocationResource";
    
    public static final String PS_USE_ABSOLUTE_LOCATION = "useAbsoluteLocation";
    public static final String PS_USE_COORDINATES = "useCoordinates";
    public static final String PS_USE_LOCATION_DESCRIPTION = "useLocationDescription";
    
    public static final String PS_USE_ACCURACY = "useAccuracy";
    public static final String PS_USE_SPEED = "useSpeed";
    
    public static final String PS_LOCATION_PRECISION = "locationPrecision";
    
    
    /**
     * Creates a new {@link LocationResourceGroup}.
     * 
     * @param rgPackage
     *            Packagename of the ResourceGroup
     * @param pmpci
     *            Connectioninterface to PMP.
     */
    public LocationResourceGroup(IPMPConnectionInterface pmpci) {
        super(PACKAGE_NAME, pmpci);
        
        registerResource(R_ABSOLUTE_LOCATION, new AbsoluteLocationResource(this));
        
        registerPrivacySetting(PS_USE_ABSOLUTE_LOCATION, new BooleanPrivacySetting());
        registerPrivacySetting(PS_USE_COORDINATES, new BooleanPrivacySetting());
        registerPrivacySetting(PS_USE_LOCATION_DESCRIPTION, new EnumPrivacySetting<UseLocationDescriptionEnum>(
                UseLocationDescriptionEnum.class));
        registerPrivacySetting(PS_USE_ACCURACY, new BooleanPrivacySetting());
        registerPrivacySetting(PS_USE_SPEED, new BooleanPrivacySetting());
        registerPrivacySetting(PS_LOCATION_PRECISION, new IntegerPrivacySetting(Integer.MAX_VALUE, 0));
    }
}
