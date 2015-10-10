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

import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.model.element.ElementPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.ResourceGroup;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGIS;

/**
 * 
 * @author Tobias Kuhn
 * 
 */
public class MockupRG extends ResourceGroup {
    
    private Drawable icon;
    
    
    public MockupRG(String rgPackage, Drawable icon, IRGIS rgis) {
        super(rgPackage);
        this.privacySettings = new HashMap<String, PrivacySetting>();
        this.icon = icon;
        this.rgis = rgis;
        this.link = null;
    }
    
    
    @Override
    public void setPersistenceProvider(ElementPersistenceProvider<? extends ModelElement> persistenceProvider) {
        super.setPersistenceProvider(null);
    }
    
    
    @Override
    public Drawable getIcon() {
        return this.icon;
    }
    
    
    public void addPS(String name, MockupPrivacySetting ps) {
        this.privacySettings.put(name, ps);
        
    }
    
    
    @Override
    public IBinder getResource(String appPackage, String resource) {
        return null;
    }
    
}
