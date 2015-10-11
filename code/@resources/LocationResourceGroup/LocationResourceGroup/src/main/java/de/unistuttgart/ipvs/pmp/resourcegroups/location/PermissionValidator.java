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

import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.resource.ResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.AbstractPrivacySetting;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.EnumPrivacySetting;

/**
 * Simple PrivacySetting verification.
 * 
 * @author Jakob Jarosch
 */
public class PermissionValidator {
    
    private ResourceGroup rg;
    private String appIdentifier;
    
    
    public PermissionValidator(ResourceGroup rg, String appIdentifier) {
        this.rg = rg;
        this.appIdentifier = appIdentifier;
    }
    
    
    public void validate(String psIdentifier, String requiredValue) {
        AbstractPrivacySetting<?> ps = this.rg.getPrivacySetting(psIdentifier);
        String grantedValue = this.rg.getPMPPrivacySettingValue(psIdentifier, this.appIdentifier);
        
        boolean failed = true;
        try {
            if (ps.permits(grantedValue, requiredValue)) {
                failed = false;
            }
        } catch (PrivacySettingValueException e) {
            Log.e(this, "Something went wrong while validating the permissions for Privacy Settings.", e);
        }
        
        if (failed) {
            throw new SecurityException("The requested action requires at the PrivacySetting " + psIdentifier
                    + " with at least " + requiredValue + " as required value");
        }
    }
    
    
    public void validate(UseLocationDescriptionEnum reference) {
        @SuppressWarnings("unchecked")
        EnumPrivacySetting<UseLocationDescriptionEnum> ps = (EnumPrivacySetting<UseLocationDescriptionEnum>) this.rg
                .getPrivacySetting(LocationResourceGroup.PS_USE_LOCATION_DESCRIPTION);
        boolean failed = true;
        try {
            if (ps.permits(this.appIdentifier, reference)) {
                failed = false;
            }
        } catch (PrivacySettingValueException e) {
            Log.e(this, "Something went wrong while validating the permissions for Privacy Settings.", e);
        }
        
        if (failed) {
            throw new SecurityException("The requested action requires at the PrivacySetting "
                    + LocationResourceGroup.PS_USE_LOCATION_DESCRIPTION + " with at least " + reference.name()
                    + " as required value");
        }
    }
    
    
    public int getIntValue(String psIdentifier) {
        String value = this.rg.getPMPPrivacySettingValue(psIdentifier, this.appIdentifier);
        if (value == null) {
            return 100000;
        } else {
            return Integer.parseInt(value);
        }
    }
}
