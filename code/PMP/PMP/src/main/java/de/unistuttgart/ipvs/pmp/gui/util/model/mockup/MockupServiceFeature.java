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
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.missing.MissingPrivacySettingValue;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.ServiceFeature;

/**
 * 
 * @author Tobias Kuhn
 * 
 */
public class MockupServiceFeature extends ServiceFeature {
    
    public MockupServiceFeature(MockupApp app, String identifier, boolean available) {
        super(app, identifier);
        this.missingPrivacySettings = new ArrayList<MissingPrivacySettingValue>();
        if (!available) {
            this.missingPrivacySettings.add(new MissingPrivacySettingValue("", "", ""));
        }
        this.privacySettingValues = new HashMap<PrivacySetting, String>();
    }
    
    
    public void addPS(MockupPrivacySetting ps, String reqValue) {
        this.privacySettingValues.put(ps, reqValue);
    }
    
    
    @Override
    public void setPersistenceProvider(ElementPersistenceProvider<? extends ModelElement> persistenceProvider) {
        super.setPersistenceProvider(null);
    }
    
}
