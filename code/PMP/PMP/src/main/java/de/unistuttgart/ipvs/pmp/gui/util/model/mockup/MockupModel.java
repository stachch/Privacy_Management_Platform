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
import java.util.List;
import java.util.Map;

import de.unistuttgart.ipvs.pmp.model.IModel;
import de.unistuttgart.ipvs.pmp.model.ModelCache;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelIntegrityError;
import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.App;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.preset.Preset;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.service.pmp.RegistrationResult;

/**
 * Mockup model guaranteed to not persist anything or communicate with anyone while trying to maintain the main
 * infrastructure.
 * 
 * @author Tobias Kuhn
 * 
 */
public class MockupModel implements IModel {
    
    public static final MockupModel instance = new MockupModel();
    
    private ModelCache cache;
    
    
    private MockupModel() {
        this.cache = new ModelCache();
    }
    
    
    @Override
    public List<IApp> getApps() {
        return new ArrayList<IApp>(this.cache.getApps().values());
    }
    
    
    @Override
    public IApp getApp(String identifier) {
        return this.cache.getApps().get(identifier);
    }
    
    
    @Override
    @Deprecated
    public RegistrationResult registerApp(String identifier) {
        return null;
    }
    
    
    public void registerApp(String identifier, MockupApp app) {
        this.cache.getApps().put(identifier, app);
    }
    
    
    @Override
    public boolean unregisterApp(String identifier) {
        
        App app = this.cache.getApps().get(identifier);
        
        for (IPreset preset : app.getAssignedPresets()) {
            // this time, there's no way but to cast (or run manually through all apps)                     
            Assert.instanceOf(preset, Preset.class, ModelIntegrityError.class, Assert.ILLEGAL_CLASS, "preset", preset);
            Preset castPreset = (Preset) preset;
            
            // since these presets were assigned to the app they now are guaranteed not to be available.
            if (!castPreset.isDeleted()) {
                castPreset.forceRecache();
                castPreset.rollout();
            }
        }
        
        return this.cache.getApps().remove(identifier) != null;
    }
    
    
    @Override
    public List<IResourceGroup> getResourceGroups() {
        return new ArrayList<IResourceGroup>(this.cache.getResourceGroups().values());
    }
    
    
    @Override
    public IResourceGroup getResourceGroup(String identifier) {
        return this.cache.getResourceGroups().get(identifier);
    }
    
    
    @Override
    @Deprecated
    public boolean installResourceGroup(String identifier, boolean dontDownload) {
        return false;
    }
    
    
    public boolean installResourceGroup(String identifier, MockupRG rg) {
        this.cache.getResourceGroups().put(identifier, rg);
        return true;
    }
    
    
    @Override
    public boolean uninstallResourceGroup(String identifier) {
        return this.cache.getResourceGroups().remove(identifier) != null;
    }
    
    
    @Override
    public List<IPreset> getPresets() {
        return new ArrayList<IPreset>(this.cache.getAllPresets());
    }
    
    
    @Override
    public List<IPreset> getPresets(ModelElement creator) {
        Map<String, Preset> creatorPresets = this.cache.getPresets().get(creator);
        if (creatorPresets == null) {
            return new ArrayList<IPreset>();
        } else {
            return new ArrayList<IPreset>(creatorPresets.values());
        }
    }
    
    
    @Override
    public IPreset getPreset(IModelElement creator, String identifier) {
        Map<String, Preset> creatorPresets = this.cache.getPresets().get(creator);
        if (creatorPresets == null) {
            return null;
        } else {
            return creatorPresets.get(identifier);
        }
    }
    
    
    @Override
    public IPreset addPreset(IModelElement creator, String identifier, String name, String description) {
        Preset newPreset = new MockupPreset(creator, identifier, name, description);
        Map<String, Preset> creatorMap = this.cache.getPresets().get(creator);
        if (creatorMap == null) {
            creatorMap = new HashMap<String, Preset>();
            this.cache.getPresets().put(creator, creatorMap);
        }
        creatorMap.put(identifier, newPreset);
        return newPreset;
    }
    
    
    @Override
    public IPreset addUserPreset(String name, String description) {
        // prepare standard
        Map<String, Preset> creatorMap = this.cache.getPresets().get(null);
        int suffix = 1;
        String identifier = name;
        
        // find free identifier
        while (creatorMap.get(identifier) != null) {
            suffix++;
            identifier = name + suffix;
        }
        
        // create
        return addPreset(null, identifier, name, description);
    }
    
    
    @Override
    public boolean removePreset(IModelElement creator, String identifier) {
        // does the creator map exist?
        Map<String, Preset> creatorMap = this.cache.getPresets().get(creator);
        
        if (creatorMap == null) {
            return false;
        } else {
            Preset p = creatorMap.get(identifier);
            
            for (IApp app : p.getAssignedApps()) {
                // this time, there's no way but to cast (or run manually through all apps)
                Assert.instanceOf(app, App.class, ModelIntegrityError.class, Assert.ILLEGAL_CLASS, "app", app);
                App castApp = (App) app;
                castApp.removePreset(p);
            }
            
            return creatorMap.remove(identifier) != null;
        }
    }
    
    
    @Override
    public void clearAll() {
        this.cache.getApps().clear();
        this.cache.getServiceFeatures().clear();
        this.cache.getResourceGroups().clear();
        this.cache.getPrivacySettings().clear();
        this.cache.getPresets().clear();
    }
    
    
    @Override
    public List<IContext> getContexts() {
        throw new UnsupportedOperationException();
    }
    
    
    @Override
    public List<IContextAnnotation> getContextAnnotations() {
        throw new UnsupportedOperationException();
    }
    
    
    @Override
    public List<IContextAnnotation> getContextAnnotations(IContext context) {
        throw new UnsupportedOperationException();
    }
    
}
