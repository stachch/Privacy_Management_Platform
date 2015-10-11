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
package de.unistuttgart.ipvs.pmp.model.element.contextannotation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.model.PersistenceConstants;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelMisuseError;
import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.util.BootReceiver;
import de.unistuttgart.ipvs.pmp.util.FileLog;

/**
 * @see IContextAnnotation
 * @author Tobias Kuhn
 * 
 */
public class ContextAnnotation extends ModelElement implements IContextAnnotation {
    
    /**
     * identifying attributes
     */
    protected IPreset preset;
    protected IPrivacySetting privacySetting;
    protected int id;
    
    /**
     * internal data & links
     */
    protected IContext context;
    protected String condition;
    protected String overrideValue;
    
    /**
     * State before the last state, primarily needed for logging purposes
     */
    protected boolean lastState;
    
    
    /* organizational */
    
    public ContextAnnotation(IPreset preset, IPrivacySetting privacySetting, int id) {
        super(preset.getIdentifier() + PersistenceConstants.PACKAGE_SEPARATOR + PersistenceConstants.PACKAGE_SEPARATOR
                + privacySetting.getIdentifier() + Integer.toString(id));
        this.preset = preset;
        this.privacySetting = privacySetting;
        this.id = id;
        this.lastState = false;
    }
    
    
    @Override
    public String toString() {
        checkCached();
        return super.toString()
                + String.format(" [ctx = %s, cond = %s, ovrd = %s]", this.context.getName(), this.condition,
                        this.overrideValue);
    }
    
    
    /* interface */
    
    @Override
    public IPreset getPreset() {
        return this.preset;
    }
    
    
    @Override
    public IPrivacySetting getPrivacySetting() {
        return this.privacySetting;
    }
    
    
    @Override
    public IContext getContext() {
        checkCached();
        return this.context;
    }
    
    
    @Override
    public void setContext(IContext context, String condition) throws InvalidConditionException {
        checkCached();
        
        Assert.nonNull(context, ModelMisuseError.class, Assert.ILLEGAL_NULL, "context", context);
        Assert.nonNull(condition, ModelMisuseError.class, Assert.ILLEGAL_NULL, "condition", condition);
        
        // check validity
        context.conditionValidOrThrow(condition);
        
        this.context = context;
        this.condition = condition;
        
        persist();
        rolloutPreset();
    }
    
    
    @Override
    public String getContextCondition() {
        checkCached();
        return this.condition;
    }
    
    
    @Override
    public void setContextCondition(String condition) throws InvalidConditionException {
        checkCached();
        
        Assert.nonNull(condition, ModelMisuseError.class, Assert.ILLEGAL_NULL, "condition", condition);
        
        // check validity
        this.context.conditionValidOrThrow(condition);
        
        this.condition = condition;
        
        persist();
        rolloutPreset();
    }
    
    
    @Override
    public String getHumanReadableContextCondition() throws InvalidConditionException {
        checkCached();
        return this.context.makeHumanReadable(this.condition);
    }
    
    
    @Override
    public String getOverridePrivacySettingValue() {
        checkCached();
        return this.overrideValue;
    }
    
    
    @Override
    public void setOverridePrivacySettingValue(String value) throws PrivacySettingValueException {
        checkCached();
        
        Assert.nonNull(value, ModelMisuseError.class, Assert.ILLEGAL_NULL, "value", value);
        
        // check validity
        this.privacySetting.valueValidOrThrow(value);
        
        this.overrideValue = value;
        
        persist();
        rolloutPreset();
    }
    
    
    @Override
    public boolean isActive() {
        checkCached();
        boolean newState = this.context.getLastState(this.condition);
        
        if (newState != this.lastState) {
            FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_CONTEXT_CHANGES, Level.FINE,
                    "Context Annotation '%s' is now %s.", this.condition, newState ? "active" : "deactivated");
            
            this.lastState = newState;
        }
        return newState;
    }
    
    
    @Override
    public String getCurrentPrivacySettingValue() {
        checkCached();
        return isActive() ? getOverridePrivacySettingValue() : null;
    }
    
    
    @Override
    public List<IContextAnnotation> getConflictingContextAnnotations(IPreset preset) {
        Assert.nonNull(preset, ModelMisuseError.class, Assert.ILLEGAL_NULL, "preset", preset);
        
        boolean hasSameAppsAssigned = false;
        for (IApp app : this.preset.getAssignedApps()) {
            if (preset.isAppAssigned(app)) {
                hasSameAppsAssigned = true;
                break;
            }
        }
        
        if (!hasSameAppsAssigned) {
            return new ArrayList<IContextAnnotation>();
        }
        
        return preset.getContextAnnotations(this.privacySetting);
    }
    
    
    @Override
    public boolean isPrivacySettingConflicting(IPreset preset) {
        Assert.nonNull(preset, ModelMisuseError.class, Assert.ILLEGAL_NULL, "preset", preset);
        
        boolean hasSameAppsAssigned = false;
        for (IApp app : this.preset.getAssignedApps()) {
            if (preset.isAppAssigned(app)) {
                hasSameAppsAssigned = true;
                break;
            }
        }
        
        if (!hasSameAppsAssigned) {
            return false;
        }
        
        String grantedByPreset = preset.getGrantedPrivacySettingValue(this.privacySetting);
        
        // early exit cause null permit comparisons don't work that well
        // early exit cause == is no conflict
        if ((grantedByPreset == null) || (grantedByPreset.equals(this.overrideValue))) {
            return false;
        }
        
        try {
            return this.privacySetting.permits(this.overrideValue, grantedByPreset);
        } catch (PrivacySettingValueException e) {
            Log.e(this, "Invalid value while checking for CA/PS conflicts: ", e);
            return false;
        }
    }
    
    
    private void rolloutPreset() {
        // will calculate the CA's activity first, then rollout the presets
        BootReceiver.startService(PMPApplication.getContext());
    }
    
}
