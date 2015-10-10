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
package de.unistuttgart.ipvs.pmp.model.element.resourcegroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import android.graphics.drawable.Drawable;
import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.model.Model;
import de.unistuttgart.ipvs.pmp.model.PersistenceConstants;
import de.unistuttgart.ipvs.pmp.model.PresetController;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelMisuseError;
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySetting;
import de.unistuttgart.ipvs.pmp.resource.RGMode;
import de.unistuttgart.ipvs.pmp.resource.Resource;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.util.FileLog;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGIS;

/**
 * @see IResourceGroup
 * @author Tobias Kuhn
 * 
 */
public class ResourceGroup extends ModelElement implements IResourceGroup {
    
    /**
     * localized values
     */
    protected IRGIS rgis;
    
    /**
     * internal data & links
     */
    protected Map<String, PrivacySetting> privacySettings;
    protected Drawable icon;
    protected de.unistuttgart.ipvs.pmp.resource.ResourceGroup link;
    protected long revision;
    
    /**
     * will be used when the linked resource fails somehow.
     */
    protected Throwable unexpectedThrowable;
    
    
    /* organizational */
    
    public ResourceGroup(String rgPackage) {
        super(rgPackage);
        this.unexpectedThrowable = null;
    }
    
    
    @Override
    public String toString() {
        return super.toString()
                + String.format(" [rgis = %s, ps = %s, link = %s, rev = %d, ut = %s]", this.rgis,
                        ModelElement.collapseMapToString(this.privacySettings), this.link, this.revision,
                        this.unexpectedThrowable);
    }
    
    
    /* interface */
    
    @Override
    public String getName() {
        checkCached();
        String name = this.rgis.getNameForLocale(Locale.getDefault());
        if (name == null) {
            name = this.rgis.getNameForLocale(Locale.ENGLISH);
        }
        return name;
    }
    
    
    @Override
    public String getDescription() {
        checkCached();
        String description = this.rgis.getDescriptionForLocale(Locale.getDefault());
        if (description == null) {
            description = this.rgis.getDescriptionForLocale(Locale.ENGLISH);
        }
        return description;
    }
    
    
    @Override
    public Drawable getIcon() {
        checkCached();
        return this.icon;
    }
    
    
    @Override
    public long getRevision() {
        checkCached();
        return this.revision;
    }
    
    
    @Override
    public List<IPrivacySetting> getPrivacySettings() {
        checkCached();
        return new ArrayList<IPrivacySetting>(this.privacySettings.values());
    }
    
    
    @Override
    public IPrivacySetting getPrivacySetting(String privacySettingIdentifier) {
        checkCached();
        Assert.nonNull(privacySettingIdentifier, ModelMisuseError.class, Assert.ILLEGAL_NULL,
                "privacySettingIdentifier", privacySettingIdentifier);
        return this.privacySettings.get(privacySettingIdentifier);
    }
    
    
    @Override
    public IBinder getResource(String appPackage, String resource) {
        checkCached();
        Assert.nonNull(appPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "appPackage", appPackage);
        Assert.nonNull(resource, ModelMisuseError.class, Assert.ILLEGAL_NULL, "resource", resource);
        
        Resource res = null;
        try {
            res = this.link.getResource(resource);
        } catch (Throwable t) {
            deactivate(t);
        }
        IApp a = Model.getInstance().getApp(appPackage);
        
        if ((a == null) || (res == null)) {
            return null;
        }
        
        // find the state
        RGMode mode = null;
        try {
            String bestValue = PresetController.findBestValue(a,
                    this.privacySettings.get(PersistenceConstants.MODE_PRIVACY_SETTING));
            mode = (bestValue == null) ? RGMode.NORMAL : RGMode.valueOf(bestValue);
        } catch (PrivacySettingValueException e) {
            e.printStackTrace();
        }
        
        switch (mode) {
            default:
                return res.getAndroidInterface(appPackage);
            case MOCK:
                return res.getMockedAndroidInterface(appPackage);
            case CLOAK:
                return res.getCloakedAndroidInterface(appPackage);
        }
        
    }
    
    
    @Override
    public boolean isDeactivated() {
        return this.unexpectedThrowable != null;
    }
    
    
    @Override
    public Throwable getReasonForDeactivation() {
        return this.unexpectedThrowable;
    }
    
    
    /* inter-model communication */
    public IRGIS getRgis() {
        checkCached();
        return this.rgis;
    }
    
    
    public void deactivate(Throwable t) {
        FileLog.get().logWithForward(this, t, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.SEVERE,
                "ResourceGroup %s deactivated.", getIdentifier());
        this.unexpectedThrowable = t;
    }
    
    
    @Override
    public boolean checkCached() {
        if (this.unexpectedThrowable != null) {
            throw new IllegalStateException("ResourceGroup is deactivated.");
        }
        return super.checkCached();
    }
    
}
