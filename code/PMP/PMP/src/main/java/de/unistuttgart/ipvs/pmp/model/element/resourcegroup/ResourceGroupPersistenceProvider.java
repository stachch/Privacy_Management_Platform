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

import java.util.Locale;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import de.unistuttgart.ipvs.pmp.model.PersistenceConstants;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelIntegrityError;
import de.unistuttgart.ipvs.pmp.model.element.ElementPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySetting;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidPluginException;
import de.unistuttgart.ipvs.pmp.model.plugin.PluginProvider;
import de.unistuttgart.ipvs.pmp.xmlutil.common.LocalizedString;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGIS;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.RGISPrivacySetting;

/**
 * The persistence provider for {@link ResourceGroup}s.
 * 
 * @author Tobias Kuhn
 * 
 */
public class ResourceGroupPersistenceProvider extends ElementPersistenceProvider<ResourceGroup> {
    
    public ResourceGroupPersistenceProvider(ResourceGroup element) {
        super(element);
    }
    
    
    @Override
    protected void loadElementData(SQLiteDatabase rdb, SQLiteQueryBuilder qb) {
        
        // set RGIS via XML file        
        try {
            this.element.rgis = PluginProvider.getInstance().getRGIS(this.element.getIdentifier());
            addModePS(this.element.rgis);
            this.element.link = PluginProvider.getInstance().getResourceGroupObject(this.element.getIdentifier());
            this.element.icon = PluginProvider.getInstance().getIcon(this.element.getIdentifier());
            this.element.revision = PluginProvider.getInstance().getRevision(this.element.getIdentifier());
        } catch (InvalidPluginException ipe) {
            this.element.deactivate(ipe);
        }
        
        this.element.privacySettings = getCache().getPrivacySettings().get(this.element);
        if (!this.element.privacySettings.containsKey(PersistenceConstants.MODE_PRIVACY_SETTING)) {
            throw new ModelIntegrityError(String.format("The mode privacy setting of %s was not found during loading!",
                    this.element.getIdentifier()));
        }
    }
    
    
    private void addModePS(IRGIS rgis) {
        RGISPrivacySetting modePS = new RGISPrivacySetting(PersistenceConstants.MODE_PRIVACY_SETTING, "");
        
        // I assume this is very evil for L10N purposes, but whatever, better than nothing
        modePS.addName(getLS(Locale.ENGLISH, "Mode"));
        modePS.addDescription(getLS(Locale.ENGLISH, "What kind of data the resource group will provide."));
        modePS.addName(getLS(Locale.GERMAN, "Modus"));
        modePS.addDescription(getLS(Locale.GERMAN, "Welche Art von Daten die Ressourcengruppe zur Verfügung stellt."));
        
        rgis.addPrivacySetting(modePS);
        
    }
    
    
    private LocalizedString getLS(Locale l, String s) {
        LocalizedString locStr = new LocalizedString();
        locStr.setLocale(l);
        locStr.setString(s);
        return locStr;
    }
    
    
    @Override
    protected void storeElementData(SQLiteDatabase wdb, SQLiteQueryBuilder qb) {
        // this method should never be called
        throw new ModelIntegrityError(Assert.format(Assert.ILLEGAL_METHOD, "storeElementData", this));
    }
    
    
    @Override
    protected void deleteElementData(SQLiteDatabase wdb, SQLiteQueryBuilder qb) {
        // delete resource group
        wdb.delete(TBL_RESOURCEGROUP, PACKAGE + " = ?", new String[] { this.element.getIdentifier() });
        
        // delete privacy settings
        for (PrivacySetting ps : this.element.privacySettings.values()) {
            ps.delete();
        }
    }
    
    
    /**
     * Creates the data <b>in the persistence</b> for the {@link ResourceGroup} specified with the parameters.
     * 
     * @param rgPackage
     *            package of the resource group.
     * @return a {@link ResourceGroup} object that is linked to the newly created persistence data and this
     *         {@link ResourceGroupPersistenceProvider}, or null, if the creation was not possible
     */
    public ResourceGroup createElementData(String rgPackage) {
        // store in db
        SQLiteDatabase sqldb = getDoh().getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(PACKAGE, rgPackage);
            if (sqldb.insert(TBL_RESOURCEGROUP, null, cv) == -1) {
                return null;
            }
        } finally {
            sqldb.close();
        }
        
        // create associated object
        ResourceGroup result = new ResourceGroup(rgPackage);
        this.element = result;
        result.setPersistenceProvider(this);
        
        return result;
    }
    
}
