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
package de.unistuttgart.ipvs.pmp.shared;

import de.unistuttgart.ipvs.pmp.shared.api.ipc.command.IPC2PMPCommand;

/**
 * Constants used by PMP, PMP-API and their implementing Apps and ResourceGroups.
 * 
 * @author Jakob Jarosch
 */
public class Constants {
    
    /**
     * The Log-Name used in DDMS for debugging.
     */
    public static final String LOG_NAME = "PMP";
    
    /**
     * The Android-wide identifier for the PMP (also used to connect to the Service inside the {@link IPC2PMPCommand}.
     */
    public static final String PMP_IDENTIFIER = "de.unistuttgart.ipvs.pmp.service.PMPService";
    
    /**
     * 
     */
    public static final String PMP_LOG_SUFIX = "PMP";
    
    /**
     * The prefix that will be put in front of the key of the service feature
     */
    public static final String SERVICE_FEATURE_PREFIX = "[app-sf]-";
}
