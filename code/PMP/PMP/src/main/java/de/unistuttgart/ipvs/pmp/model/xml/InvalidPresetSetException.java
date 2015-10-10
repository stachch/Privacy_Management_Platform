/*
 * Copyright 2012 pmp-android development team
 * Project: PMP
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
package de.unistuttgart.ipvs.pmp.model.xml;

import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetSet;

/**
 * Exception if the {@link PresetSet} contained any element that cannot be resolved because it is not actually
 * installed.
 * 
 * @author Tobias Kuhn
 * 
 */
public class InvalidPresetSetException extends Exception {
    
    private static final long serialVersionUID = 5000448075555827715L;
    
    
    public InvalidPresetSetException() {
        super();
    }
    
    
    public InvalidPresetSetException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
    
    
    public InvalidPresetSetException(String detailMessage) {
        super(detailMessage);
    }
    
    
    public InvalidPresetSetException(Throwable throwable) {
        super(throwable);
    }
    
}
