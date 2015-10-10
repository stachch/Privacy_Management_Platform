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
package de.unistuttgart.ipvs.pmp.resource.privacysetting;

/**
 * Exception that is thrown if a String value is supplied to a {@link AbstractPrivacySetting} which is not valid
 * according to the {@link AbstractPrivacySetting}'s format criteria.
 * 
 * @author Tobias Kuhn
 * 
 */
public class PrivacySettingValueException extends Exception {
    
    private static final long serialVersionUID = -6411892866985727591L;
    
    
    public PrivacySettingValueException() {
        super();
    }
    
    
    public PrivacySettingValueException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
    
    
    public PrivacySettingValueException(String detailMessage) {
        super(detailMessage);
    }
    
    
    public PrivacySettingValueException(Throwable throwable) {
        super(throwable);
    }
    
}
