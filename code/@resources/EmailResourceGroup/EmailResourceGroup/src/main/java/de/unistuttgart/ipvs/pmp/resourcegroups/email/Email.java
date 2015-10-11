/*
 * Copyright 2012 pmp-android development team
 * Project: EmailResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.email;

import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.resource.IPMPConnectionInterface;
import de.unistuttgart.ipvs.pmp.shared.resource.ResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.BooleanPrivacySetting;

public class Email extends ResourceGroup {
    
    public static final String PRIVACY_SETTING_SEND_EMAIL = "canSendEmail";
    public static final String RESOURCE_EMAIL_OPERATIONS = "emailOperations";
    
    
    public Email(IPMPConnectionInterface pmpci) {
        super("de.unistuttgart.ipvs.pmp.resourcegroups.email", pmpci);
        
        registerResource(RESOURCE_EMAIL_OPERATIONS, new EmailResource());
        registerPrivacySetting(PRIVACY_SETTING_SEND_EMAIL, new BooleanPrivacySetting());
    }
    
    
    public void onRegistrationSuccess() {
        Log.d(this, "Registration success.");
    }
    
    
    public void onRegistrationFailed(String message) {
        Log.e(this, "Registration failed with \"" + message + "\"");
    }
}
