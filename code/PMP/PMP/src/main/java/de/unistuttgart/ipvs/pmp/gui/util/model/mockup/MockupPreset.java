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
package de.unistuttgart.ipvs.pmp.gui.util.model.mockup;

import java.util.ArrayList;
import java.util.HashMap;

import de.unistuttgart.ipvs.pmp.model.element.ElementPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.missing.MissingApp;
import de.unistuttgart.ipvs.pmp.model.element.missing.MissingPrivacySettingValue;
import de.unistuttgart.ipvs.pmp.model.element.preset.Preset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;

/**
 * 
 * @author Tobias Kuhn
 * 
 */
public class MockupPreset extends Preset {
    
    public MockupPreset(IModelElement creator, String identifier, String name, String description) {
        super(creator, identifier);
        this.name = name;
        this.description = description;
        this.deleted = false;
        this.assignedApps = new ArrayList<IApp>();
        this.missingPrivacySettings = new ArrayList<MissingPrivacySettingValue>();
        this.missingApps = new ArrayList<MissingApp>();
        this.privacySettingValues = new HashMap<IPrivacySetting, String>();
    }
    
    
    @Override
    public void setPersistenceProvider(ElementPersistenceProvider<? extends ModelElement> persistenceProvider) {
        super.setPersistenceProvider(null);
    }
    
    
    public void setAvailable(boolean available) {
        if (available) {
            this.missingApps.clear();
        } else {
            this.missingApps.add(new MissingApp(""));
        }
    }
    
    
    @Override
    public void assignApp(IApp app) {
        try {
            super.assignApp(app);
        } catch (Throwable t) {
        }
    }
    
    
    @Override
    public void removeApp(IApp app) {
        try {
            super.removeApp(app);
        } catch (Throwable t) {
        }
    }
}
