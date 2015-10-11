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

import java.util.List;

import de.unistuttgart.ipvs.pmp.model.IModel;
import de.unistuttgart.ipvs.pmp.model.Model;
import de.unistuttgart.ipvs.pmp.model.PersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;
import de.unistuttgart.ipvs.pmp.model.ipc.IPCProvider;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetAssignedApp;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetAssignedPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetPSContext;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetSet;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.Preset;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetAssignedApp;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetAssignedPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetPSContext;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetSet;

/**
 * @see IXMLInterface
 * @author Tobias Kuhn
 * 
 */
public class XMLInterface implements IXMLInterface {
    
    public static final IXMLInterface instance = new XMLInterface();
    
    
    private XMLInterface() {
    }
    
    
    @Override
    public IPresetSet exportPresets(List<IPreset> presets) {
        IPresetSet result = new PresetSet();
        
        // each preset
        for (IPreset preset : presets) {
            Preset xmlPreset = new Preset(preset.getLocalIdentifier(),
                    PersistenceProvider.getPresetCreatorString(preset), preset.getName(), preset.getDescription());
            
            // each assigned app
            for (IApp app : preset.getAssignedApps()) {
                xmlPreset.addAssignedApp(new PresetAssignedApp(app.getIdentifier()));
            }
            
            // each privacy setting
            for (IPrivacySetting ps : preset.getGrantedPrivacySettings()) {
                PresetAssignedPrivacySetting paps = new PresetAssignedPrivacySetting(ps.getResourceGroup()
                        .getIdentifier(), String.valueOf(ps.getResourceGroup().getRevision()), ps.getLocalIdentifier(),
                        preset.getGrantedPrivacySettingValue(ps));
                
                // each context annotation
                for (IContextAnnotation ca : preset.getContextAnnotations(ps)) {
                    paps.addContext(new PresetPSContext(ca.getContext().getIdentifier(), ca.getContextCondition(), ca
                            .getOverridePrivacySettingValue()));
                }
                
                xmlPreset.addAssignedPrivacySetting(paps);
            }
            
            result.addPreset(xmlPreset);
        }
        
        return result;
    }
    
    
    @Override
    public void importPresets(List<de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPreset> presets, boolean override)
            throws InvalidPresetSetException {
        
        IModel m = Model.getInstance();
        
        IPCProvider.getInstance().startUpdate();
        try {
            
            // each preset
            for (de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPreset preset : presets) {
                IModelElement creator = m.getApp(preset.getCreator());
                if (creator == null) {
                    creator = m.getResourceGroup(preset.getCreator());
                }
                IPreset existing = m.getPreset(creator, preset.getIdentifier());
                
                // add one to the model according to semantics
                IPreset toWrite = null;
                
                if (existing != null) {
                    // already exists
                    
                    if (override) {
                        m.removePreset(existing.getCreator(), existing.getLocalIdentifier());
                        toWrite = m.addPreset(creator, preset.getIdentifier(), preset.getName(),
                                preset.getDescription());
                    } else {
                        toWrite = m.addUserPreset(preset.getName(), preset.getDescription());
                    }
                    
                } else {
                    // not yet, simply add
                    
                    toWrite = m.addPreset(creator, preset.getIdentifier(), preset.getName(), preset.getDescription());
                }
                
                // each app
                for (IPresetAssignedApp paa : preset.getAssignedApps()) {
                    IApp app = m.getApp(paa.getIdentifier());
                    if (app == null) {
                        throw new InvalidPresetSetException("App not found: " + paa.getIdentifier());
                    }
                    toWrite.assignApp(app);
                }
                
                // each ps
                for (IPresetAssignedPrivacySetting pasp : preset.getAssignedPrivacySettings()) {
                    IResourceGroup rg = m.getResourceGroup(pasp.getRgIdentifier());
                    if (rg == null) {
                        throw new InvalidPresetSetException("RG not found: " + pasp.getRgIdentifier());
                    }
                    IPrivacySetting ps = rg.getPrivacySetting(pasp.getPsIdentifier());
                    if (ps == null) {
                        throw new InvalidPresetSetException("PS not found: " + pasp.getPsIdentifier() + " in "
                                + pasp.getRgIdentifier());
                    }
                    try {
                        toWrite.assignPrivacySetting(ps, pasp.getValue());
                    } catch (PrivacySettingValueException psve) {
                        throw new InvalidPresetSetException("PS value was invalid: " + pasp.getValue(), psve);
                    }
                    
                    // each ca
                    for (IPresetPSContext ppsc : pasp.getContexts()) {
                        
                        IContext c = null;
                        for (IContext c2 : m.getContexts()) {
                            if (c2.getIdentifier().equals(ppsc.getType())) {
                                c = c2;
                                break;
                            }
                        }
                        if (c == null) {
                            throw new InvalidPresetSetException("Context not found: " + ppsc.getType());
                        }
                        
                        try {
                            toWrite.assignContextAnnotation(ps, c, ppsc.getCondition(), ppsc.getOverrideValue());
                        } catch (InvalidConditionException e) {
                            throw new InvalidPresetSetException(
                                    "Context condition was invalid: " + ppsc.getCondition(), e);
                        } catch (PrivacySettingValueException e) {
                            throw new InvalidPresetSetException("Context PS value was invalid: "
                                    + ppsc.getOverrideValue(), e);
                        }
                    }
                }
                
            }
            
        } finally {
            IPCProvider.getInstance().endUpdate();
        }
        
    }
}
