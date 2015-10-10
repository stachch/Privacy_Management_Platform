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
package de.unistuttgart.ipvs.pmp.resource;

import de.unistuttgart.ipvs.pmp.resource.privacysetting.library.EnumPrivacySetting;

/**
 * What type of data the Resource Group is supposed to deliver. The definition order carries semantics for
 * {@link EnumPrivacySetting}.
 * 
 * @author Tobias Kuhn
 * 
 */
public enum RGMode {
    /**
     * Correct data
     */
    NORMAL,
    /**
     * Obviously faked data
     */
    MOCK,
    /**
     * Data that is fake but indistinguishable from "NORMAL"
     */
    CLOAK;
    
}
