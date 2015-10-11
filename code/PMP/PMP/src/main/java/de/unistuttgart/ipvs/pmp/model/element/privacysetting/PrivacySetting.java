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
package de.unistuttgart.ipvs.pmp.model.element.privacysetting;

import java.util.Locale;

import android.content.Context;
import android.view.View;
import de.unistuttgart.ipvs.pmp.model.PersistenceConstants;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelMisuseError;
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.ResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.AbstractPrivacySetting;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;

/**
 * @see IPrivacySetting
 * @author Tobias Kuhn
 * 
 */
public class PrivacySetting extends ModelElement implements IPrivacySetting {
    
    /**
     * identifying attributes
     */
    protected ResourceGroup resourceGroup;
    protected String localIdentifier;
    
    /**
     * internal data & links
     */
    protected AbstractPrivacySetting<?> link;
    protected boolean requestable;
    
    
    /* organizational */
    
    public PrivacySetting(ResourceGroup resourceGroup, String identifier) {
        super(resourceGroup.getIdentifier() + PersistenceConstants.PACKAGE_SEPARATOR + identifier);
        this.resourceGroup = resourceGroup;
        this.localIdentifier = identifier;
    }
    
    
    @Override
    public String toString() {
        return super.toString() + String.format(" [link = %s]", this.link);
    }
    
    
    /* interface */
    
    @Override
    public IResourceGroup getResourceGroup() {
        return this.resourceGroup;
    }
    
    
    @Override
    public String getName() {
        String name = this.resourceGroup.getRgis().getPrivacySettingForIdentifier(getLocalIdentifier())
                .getNameForLocale(Locale.getDefault());
        if (name == null) {
            name = this.resourceGroup.getRgis().getPrivacySettingForIdentifier(getLocalIdentifier())
                    .getNameForLocale(Locale.ENGLISH);
        }
        return name;
    }
    
    
    @Override
    public String getDescription() {
        String description = this.resourceGroup.getRgis().getPrivacySettingForIdentifier(getLocalIdentifier())
                .getDescriptionForLocale(Locale.getDefault());
        if (description == null) {
            description = this.resourceGroup.getRgis().getPrivacySettingForIdentifier(getLocalIdentifier())
                    .getDescriptionForLocale(Locale.ENGLISH);
        }
        return description;
    }
    
    
    @Override
    public String getChangeDescription() {
        String changeDescription = this.resourceGroup.getRgis().getPrivacySettingForIdentifier(getLocalIdentifier())
                .getChangeDescriptionForLocale(Locale.getDefault());
        if (changeDescription == null) {
            changeDescription = this.resourceGroup.getRgis().getPrivacySettingForIdentifier(getLocalIdentifier())
                    .getChangeDescriptionForLocale(Locale.ENGLISH);
        }
        return changeDescription;
    }
    
    
    @Override
    public String getLocalIdentifier() {
        return this.localIdentifier;
    }
    
    
    @Override
    public boolean isValueValid(String value) {
        checkCached();
        try {
            this.link.parseValue(value);
            return true;
        } catch (PrivacySettingValueException psve) {
            // don't care here, that's expected
        } catch (Throwable t) {
            this.resourceGroup.deactivate(t);
        }
        return false;
    }
    
    
    @Override
    public void valueValidOrThrow(String value) throws PrivacySettingValueException {
        checkCached();
        try {
            this.link.parseValue(value);
        } catch (PrivacySettingValueException psve) {
            throw psve;
        } catch (Throwable t) {
            this.resourceGroup.deactivate(t);
        }
    }
    
    
    @Override
    public String getHumanReadableValue(String value) throws PrivacySettingValueException {
        checkCached();
        try {
            return this.link.getHumanReadableValue(value);
        } catch (PrivacySettingValueException psve) {
            throw psve;
        } catch (Throwable t) {
            this.resourceGroup.deactivate(t);
            return "";
        }
    }
    
    
    @Override
    public boolean permits(String reference, String value) throws PrivacySettingValueException {
        checkCached();
        try {
            return this.link.permits(value, reference);
        } catch (PrivacySettingValueException psve) {
            throw psve;
        } catch (Throwable t) {
            this.resourceGroup.deactivate(t);
            return false;
        }
    }
    
    
    @Override
    public View getView(Context context) {
        checkCached();
        Assert.nonNull(context, ModelMisuseError.class, Assert.ILLEGAL_NULL, "context", context);
        try {
            return this.link.getView(context).asView();
        } catch (Throwable t) {
            this.resourceGroup.deactivate(t);
            return new View(context);
        }
    }
    
    
    @Override
    public void setViewValue(Context context, String value) throws PrivacySettingValueException {
        checkCached();
        Assert.nonNull(context, ModelMisuseError.class, Assert.ILLEGAL_NULL, "context", context);
        try {
            this.link.setViewValue(context, value);
        } catch (PrivacySettingValueException psve) {
            throw psve;
        } catch (Throwable t) {
            this.resourceGroup.deactivate(t);
        }
    }
    
    
    @Override
    public String getViewValue(Context context) {
        checkCached();
        Assert.nonNull(context, ModelMisuseError.class, Assert.ILLEGAL_NULL, "context", context);
        try {
            return this.link.getViewValue(context);
        } catch (Throwable t) {
            this.resourceGroup.deactivate(t);
            return "";
        }
    }
    
    
    @Override
    public boolean isRequestable() {
        checkCached();
        return this.requestable;
    }
    
    
    /* inter-model communication */
    
    @Override
    public boolean checkCached() {
        if (this.resourceGroup.isDeactivated()) {
            throw new IllegalStateException("ResourceGroup is deactivated.");
        }
        return super.checkCached();
    }
    
}
