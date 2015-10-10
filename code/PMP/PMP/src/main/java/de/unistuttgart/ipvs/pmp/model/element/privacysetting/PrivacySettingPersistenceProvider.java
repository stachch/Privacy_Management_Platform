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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.model.PersistenceConstants;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelIntegrityError;
import de.unistuttgart.ipvs.pmp.model.element.ElementPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.ContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.ResourceGroup;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidPluginException;
import de.unistuttgart.ipvs.pmp.model.plugin.PluginProvider;
import de.unistuttgart.ipvs.pmp.resource.RGMode;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.library.EnumPrivacySetting;

/**
 * The persistence provider for {@link PrivacySetting}s.
 * 
 * @author Tobias Kuhn
 * 
 */
public class PrivacySettingPersistenceProvider extends ElementPersistenceProvider<PrivacySetting> {
    
    public PrivacySettingPersistenceProvider(PrivacySetting element) {
        super(element);
    }
    
    
    @Override
    protected void loadElementData(SQLiteDatabase rdb, SQLiteQueryBuilder qb) {
        if (this.element.getLocalIdentifier().equals(PersistenceConstants.MODE_PRIVACY_SETTING)) {
            this.element.link = new EnumPrivacySetting<RGMode>(RGMode.class, RGMode.NORMAL);
            this.element.requestable = false;
            return;
        }
        
        try {
            this.element.link = PluginProvider.getInstance()
                    .getResourceGroupObject(this.element.getResourceGroup().getIdentifier())
                    .getPrivacySetting(this.element.getLocalIdentifier());
            
            qb.setTables(TBL_PRIVACYSETTING);
            
            // load privacy setting values
            Cursor c = qb.query(rdb, new String[] { REQUESTABLE }, IDENTIFIER + " = ? AND " + RESOURCEGROUP_PACKAGE
                    + " = ?",
                    new String[] { this.element.getLocalIdentifier(), this.element.resourceGroup.getIdentifier() },
                    null, null, null);
            
            this.element.requestable = c.moveToFirst() ? c.getInt(c.getColumnIndex(REQUESTABLE)) != 0 : false;
            c.close();
            
        } catch (InvalidPluginException ipe) {
            this.element.resourceGroup.deactivate(ipe);
        }
    }
    
    
    @Override
    protected void storeElementData(SQLiteDatabase wdb, SQLiteQueryBuilder qb) {
        // this method should never be called
        throw new ModelIntegrityError(Assert.format(Assert.ILLEGAL_METHOD, "storeElementData", this));
    }
    
    
    @Override
    protected void deleteElementData(SQLiteDatabase wdb, SQLiteQueryBuilder qb) {
        // service feature required privacy setting values references are not supposed to be deleted,
        // we still need those for telling the SF is unavailable
        
        // preset granted privacy setting values references are not supposed to be deleted,
        // the preset will handle unavailable elements itself 
        
        // delete privacy settings
        wdb.delete(TBL_PRIVACYSETTING, RESOURCEGROUP_PACKAGE + " = ? AND " + IDENTIFIER + " = ?", new String[] {
                this.element.getResourceGroup().getIdentifier(), this.element.getLocalIdentifier() });
        
        // delete context annotations
        for (IContextAnnotation ca : getCache().getAllContextAnnotations()) {
            if (ca.getPrivacySetting() == this.element) {
                Assert.instanceOf(ca, ContextAnnotation.class, ModelIntegrityError.class, Assert.ILLEGAL_CLASS, "ca",
                        ca);
                ((ContextAnnotation) ca).delete();
            }
        }
        
    }
    
    
    /**
     * Creates the data <b>in the persistence</b> for the {@link PrivacySetting} specified with the parameters. Links
     * this {@link PrivacySettingPersistenceProvider} to the newly created object.
     * 
     * @param rg
     *            the rg whom this privacy setting belongs to
     * @param identifier
     *            the identifier of this privacy setting
     * @param requestable
     *            whether this privacy setting can be requested by service features
     * @return an {@link PrivacySetting} object that is linked to the newly created persistence data and this
     *         {@link PrivacySettingPersistenceProvider}, or null, if the creation was not possible
     */
    public PrivacySetting createElementData(ResourceGroup rg, String identifier, boolean requestable) {
        // store in db
        SQLiteDatabase sqldb = getDoh().getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(RESOURCEGROUP_PACKAGE, rg.getIdentifier());
            cv.put(IDENTIFIER, identifier);
            cv.put(REQUESTABLE, requestable ? 1 : 0);
            if (sqldb.insert(TBL_PRIVACYSETTING, null, cv) == -1) {
                Log.e(this, "Could not write privacy setting.");
                return null;
            }
        } finally {
            sqldb.close();
        }
        
        // create associated object
        PrivacySetting result = new PrivacySetting(rg, identifier);
        this.element = result;
        result.setPersistenceProvider(this);
        
        return result;
    }
}
