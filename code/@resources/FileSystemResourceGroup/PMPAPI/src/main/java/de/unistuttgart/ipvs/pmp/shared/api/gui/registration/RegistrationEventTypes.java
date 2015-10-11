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
package de.unistuttgart.ipvs.pmp.shared.api.gui.registration;

/**
 * The defined event types do have influences on the gui.
 * 
 * @author Jakob Jarosch
 * 
 */
public enum RegistrationEventTypes {
    /**
     * No Activity is defined error message.
     */
    NO_ACITIVTY_DEFINED,
    
    /**
     * PMP is not installed error message.
     */
    PMP_NOT_INSTALLED,
    
    /**
     * Registration process has been started.
     */
    START_REGISTRATION,
    
    /**
     * Registration has succeeded.
     */
    REGISTRATION_SUCCEED,
    
    /**
     * Registration has failed.
     */
    REGISTRATION_FAILED,
    
    /**
     * App is already registered at PMP.
     */
    ALREADY_REGISTERED,
    
    /**
     * The Service Features screen is going to be opened.
     */
    SF_SCREEN_OPENED,
    
    /**
     * The Service Features screen has been closed.
     */
    SF_SCREEN_CLOSED,
    
    /**
     * The Apps main Activity is going to be brought into the front.
     */
    OPEN_APP,
}
