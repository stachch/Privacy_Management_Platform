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
package de.unistuttgart.ipvs.pmp.model.element.preset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.unistuttgart.ipvs.pmp.model.element.IAtomicTransaction;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.ContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.missing.MissingApp;
import de.unistuttgart.ipvs.pmp.model.element.missing.MissingPrivacySettingValue;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.ipc.IPCProvider;

/**
 * <p>
 * Implements atomic transactions on Presets using the rollback strategy. Invoking methods changes the behavior of the
 * parent object. Getters will behave in a way, so that they reflect the changes in the transaction which may not have
 * been committed yet. However, the entire transaction may be rolled back, if necessary.
 * </p>
 * <p>
 * During a transaction, the {@link IPCProvider} will automatically use the same scope, i.e. no IPC connections until
 * the transaction was committed.
 * </p>
 * <p>
 * <i>Do note that it is highly advised not to change the rest of the model while performing a transaction.</i>
 * Inconsistent states may produce weird errors, e.g. a removed app is still used in the preset.
 * </p>
 * 
 * @author Tobias Kuhn
 * 
 */
public class PresetTransaction implements IAtomicTransaction {
    
    /**
     * The {@link Preset} onto which the commands shall be executed
     */
    protected Preset parent;
    
    /*
     * all the various things that can be undone
     */
    
    /**
     * localized values
     */
    private String name;
    private String description;
    
    /**
     * internal data & links
     */
    private Map<IPrivacySetting, String> privacySettingValues;
    private List<IApp> assignedApps;
    private Map<IPrivacySetting, List<ContextAnnotation>> contextAnnotations;
    
    private List<MissingPrivacySettingValue> missingPrivacySettings;
    private List<MissingApp> missingApps;
    private boolean deleted;
    
    
    protected PresetTransaction(Preset parent) {
        this.parent = parent;
    }
    
    
    @Override
    public void start() {
        this.name = this.parent.name;
        this.description = this.parent.description;
        this.privacySettingValues = new HashMap<IPrivacySetting, String>(this.parent.privacySettingValues);
        this.assignedApps = new ArrayList<IApp>(this.parent.assignedApps);
        
        // deep copy
        this.contextAnnotations = new HashMap<IPrivacySetting, List<ContextAnnotation>>();
        for (Entry<IPrivacySetting, List<ContextAnnotation>> e : this.parent.contextAnnotations.entrySet()) {
            this.contextAnnotations.put(e.getKey(), new ArrayList<ContextAnnotation>(e.getValue()));
        }
        
        this.missingPrivacySettings = new ArrayList<MissingPrivacySettingValue>(this.parent.missingPrivacySettings);
        this.missingApps = new ArrayList<MissingApp>(this.parent.missingApps);
        this.deleted = this.parent.deleted;
        
        IPCProvider.getInstance().startUpdate();
    }
    
    
    @Override
    public void commit() {
        IPCProvider.getInstance().endUpdate();
    }
    
    
    @Override
    public void abort() {
        this.parent.name = this.name;
        this.parent.description = this.description;
        this.parent.privacySettingValues = this.privacySettingValues;
        this.parent.assignedApps = this.assignedApps;
        this.parent.contextAnnotations = this.contextAnnotations;
        this.parent.missingPrivacySettings = this.missingPrivacySettings;
        this.parent.missingApps = this.missingApps;
        this.parent.deleted = this.deleted;
        
        this.parent.persistAndRollout();
        
        IPCProvider.getInstance().endUpdate();
    }
    
}
