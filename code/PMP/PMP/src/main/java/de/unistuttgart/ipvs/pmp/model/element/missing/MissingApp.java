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
package de.unistuttgart.ipvs.pmp.model.element.missing;

import de.unistuttgart.ipvs.pmp.model.element.app.App;

/**
 * Object to inform about missing {@link App}s.
 * 
 * @author Tobias Kuhn
 * 
 */
public class MissingApp {
    
    private final String app;
    
    
    public MissingApp(String app) {
        this.app = app;
    }
    
    
    public String getApp() {
        return this.app;
    }
    
    
    @Override
    public String toString() {
        return String.format("%s [%s]", super.toString(), this.app);
    }
}
