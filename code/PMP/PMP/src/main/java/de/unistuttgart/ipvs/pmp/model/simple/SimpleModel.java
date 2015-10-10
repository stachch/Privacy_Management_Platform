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
package de.unistuttgart.ipvs.pmp.model.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.model.IModel;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelIntegrityError;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelMisuseError;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;
import de.unistuttgart.ipvs.pmp.model.ipc.IPCProvider;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;

/**
 * @see ISimpleModel
 * @author Tobias Kuhn
 * 
 */
public class SimpleModel implements ISimpleModel {
    
    /*
     * Singleton stuff
     */
    public static final SimpleModel instance = new SimpleModel();
    
    
    public static ISimpleModel getInstance() {
        return instance;
    }
    
    
    private SimpleModel() {
    }
    
    
    @Override
    public void convertExpertToSimple(IModel model) {
        Assert.nonNull(model, ModelMisuseError.class, Assert.ILLEGAL_NULL, "model", model);
        
        IPCProvider.getInstance().startUpdate();
        try {
            
            // keep this state, now save all active SF
            Map<IApp, List<IServiceFeature>> actives = new HashMap<IApp, List<IServiceFeature>>();
            for (IApp a : model.getApps()) {
                actives.put(a, a.getActiveServiceFeatures());
            }
            
            // delete all user presets
            for (IPreset p : model.getPresets(null)) {
                // and all CAs
                for (IPrivacySetting ps : p.getGrantedPrivacySettings()) {
                    for (IContextAnnotation ca : p.getContextAnnotations(ps)) {
                        p.removeContextAnnotation(ps, ca);
                    }
                }
                model.removePreset(null, p.getLocalIdentifier());
            }
            // deactivate all non-user presets
            for (IPreset p : model.getPresets()) {
                p.setDeleted(true);
            }
            
            // install the simple mode presets for the apps
            for (Entry<IApp, List<IServiceFeature>> a : actives.entrySet()) {
                IPreset p = createPresetForApp(model, a.getKey());
                
                // assign all previously active SF to this one preset
                for (IServiceFeature sf : a.getValue()) {
                    try {
                        p.assignServiceFeature(sf);
                    } catch (PrivacySettingValueException e) {
                        // Ignore, but log
                        Log.w(this, "Converting Model with suddenly invalid PS request in SF (" + sf.getName() + ")", e);
                    }
                }
            }
            
        } finally {
            IPCProvider.getInstance().endUpdate();
        }
    }
    
    
    @Override
    public boolean isSimpleMode(IModel model) {
        return isSimpleMode(model, false);
    }
    
    
    /**
     * @see ISimpleModel#isSimpleMode(IModel)
     * @param allergic
     *            if true, will throw {@link ModelMisuseError}s.
     */
    public boolean isSimpleMode(IModel model, boolean allergic) {
        Assert.nonNull(model, ModelMisuseError.class, Assert.ILLEGAL_NULL, "model", model);
        
        for (IPreset p : model.getPresets()) {
            // check that each non-user preset is deleted
            if (p.isBundled() && !p.isDeleted()) {
                if (allergic) {
                    throw new ModelMisuseError(Assert.format(Assert.ILLEGAL_SIMPLE_MODE, "p", p));
                }
                return false;
            }
            
            // check that all existing presets correspond to one app only
            if ((p.getAssignedApps().size() != 1) && (p.getMissingApps().size() != 1)) {
                if (allergic) {
                    throw new ModelMisuseError(Assert.format(Assert.ILLEGAL_SIMPLE_MODE, "p", p));
                }
                return false;
            }
            
            // check that no CAs are present
            for (IPrivacySetting ps : p.getGrantedPrivacySettings()) {
                if (p.getContextAnnotations(ps).size() > 0) {
                    if (allergic) {
                        throw new ModelMisuseError(Assert.format(Assert.ILLEGAL_SIMPLE_MODE, "p", p));
                    }
                    return false;
                }
            }
            
        }
        
        for (IApp a : model.getApps()) {
            // check that all apps correspond to maximum one preset only
            int activePresets = 0;
            for (IPreset p : a.getAssignedPresets()) {
                if (!p.isDeleted()) {
                    activePresets++;
                }
            }
            
            if (activePresets > 1) {
                if (allergic) {
                    throw new ModelMisuseError(Assert.format(Assert.ILLEGAL_SIMPLE_MODE, "a", a));
                }
                return false;
            }
        }
        
        return true;
    }
    
    
    @Override
    public boolean setServiceFeatureActive(IModel model, IServiceFeature serviceFeature, boolean active)
            throws PrivacySettingValueException {
        Assert.nonNull(model, ModelMisuseError.class, Assert.ILLEGAL_NULL, "model", model);
        Assert.nonNull(serviceFeature, ModelMisuseError.class, Assert.ILLEGAL_NULL, "serviceFeature", serviceFeature);
        
        if (!isSimpleMode(model, true)) {
            return false;
        }
        
        IApp a = serviceFeature.getApp();
        IPreset p;
        // if no preset yet, create one
        if (a.getAssignedPresets().size() == 0) {
            p = createPresetForApp(model, a);
        } else {
            p = a.getAssignedPresets().get(0);
        }
        
        List<IServiceFeature> actives = new ArrayList<IServiceFeature>(a.getActiveServiceFeatures());
        // check whether the active setting is already the case
        boolean contained = actives.contains(serviceFeature);
        if ((contained && active) || (!contained && !active)) {
            return false;
        }
        
        // set the new actives
        if (active) {
            actives.add(serviceFeature);
        } else {
            actives.remove(serviceFeature);
        }
        
        p.getTransaction().start();
        try {
            
            // remove all previously assigned PS
            for (IPrivacySetting ps : p.getGrantedPrivacySettings()) {
                p.removePrivacySetting(ps);
            }
            
            // assign SF
            for (IServiceFeature sf : actives) {
                p.assignServiceFeature(sf);
            }
            
        } finally {
            p.getTransaction().commit();
        }
        
        return true;
    }
    
    
    /**
     * Creates a simple mode preset in the model for the app.
     * 
     * @param model
     * @param app
     * @return said preset
     */
    private IPreset createPresetForApp(IModel model, IApp app) {
        Assert.nonNull(model, ModelIntegrityError.class, Assert.ILLEGAL_NULL, "model", model);
        Assert.nonNull(app, ModelIntegrityError.class, Assert.ILLEGAL_NULL, "app", app);
        
        IPreset p = model.addUserPreset(app.getName(), "");
        p.assignApp(app);
        return p;
    }
    
}
