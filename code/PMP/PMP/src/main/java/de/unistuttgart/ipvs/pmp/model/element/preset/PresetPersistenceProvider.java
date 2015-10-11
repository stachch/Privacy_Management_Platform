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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import de.unistuttgart.ipvs.pmp.model.PersistenceConstants;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelIntegrityError;
import de.unistuttgart.ipvs.pmp.model.element.ElementPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.App;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.ContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.missing.MissingApp;
import de.unistuttgart.ipvs.pmp.model.element.missing.MissingPrivacySettingValue;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.ResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.Log;

/**
 * The persistence provider for {@link Preset}s.
 * 
 * @author Tobias Kuhn
 * 
 */
public class PresetPersistenceProvider extends ElementPersistenceProvider<Preset> {
    
    public PresetPersistenceProvider(Preset element) {
        super(element);
    }
    
    
    @Override
    protected void loadElementData(SQLiteDatabase rdb, SQLiteQueryBuilder qb) {
        qb.setTables(TBL_PRESET);
        Cursor c = qb.query(rdb, new String[] { NAME, DESCRIPTION, DELETED }, CREATOR + " = ? AND " + IDENTIFIER
                + " = ?", new String[] { getPresetCreatorString(this.element), this.element.getLocalIdentifier() },
                null, null, null);
        
        if (c.moveToFirst()) {
            this.element.name = c.getString(c.getColumnIndex(NAME));
            this.element.description = c.getString(c.getColumnIndex(DESCRIPTION));
            this.element.deleted = Boolean.valueOf(c.getString(c.getColumnIndex(DELETED)));
        } else {
            throw new ModelIntegrityError(Assert.format(Assert.ILLEGAL_DB, "Preset", this));
        }
        c.close();
        
        // load privacy setting values
        qb.setTables(TBL_GrantPSValue);
        Cursor cps = qb.query(rdb, new String[] { PRIVACYSETTING_RESOURCEGROUP_PACKAGE, PRIVACYSETTING_IDENTIFIER,
                GRANTEDVALUE }, PRESET_CREATOR + " = ? AND " + PRESET_IDENTIFIER + " = ?", new String[] {
                getPresetCreatorString(this.element), this.element.getLocalIdentifier() }, null, null, null);
        
        this.element.privacySettingValues = new HashMap<IPrivacySetting, String>();
        this.element.missingPrivacySettings = new ArrayList<MissingPrivacySettingValue>();
        this.element.missingApps = new ArrayList<MissingApp>();
        
        if (cps.moveToFirst()) {
            do {
                String rgPackage = cps.getString(cps.getColumnIndex(PRIVACYSETTING_RESOURCEGROUP_PACKAGE));
                String psIdentifier = cps.getString(cps.getColumnIndex(PRIVACYSETTING_IDENTIFIER));
                String grantValue = cps.getString(cps.getColumnIndex(GRANTEDVALUE));
                
                ResourceGroup rg = getCache().getResourceGroups().get(rgPackage);
                if (rg == null) {
                    Log.w(this, String.format("Unavailable preset cached (RG '%s' not present).", rgPackage));
                    this.element.missingPrivacySettings.add(new MissingPrivacySettingValue(rgPackage, psIdentifier,
                            grantValue));
                    
                } else {
                    PrivacySetting ps = getCache().getPrivacySettings().get(rg).get(psIdentifier);
                    if (ps == null) {
                        Log.w(this, String.format("Unavailable preset cached (PS '%s' not found in RG '%s').",
                                psIdentifier, rg));
                        this.element.missingPrivacySettings.add(new MissingPrivacySettingValue(rgPackage, psIdentifier,
                                grantValue));
                        
                    } else {
                        this.element.privacySettingValues.put(ps, grantValue);
                    }
                }
            } while (cps.moveToNext());
        }
        cps.close();
        
        // load assigned apps
        qb.setTables(TBL_PresetAssignedApp);
        Cursor capp = qb.query(rdb, new String[] { APP_PACKAGE }, PRESET_CREATOR + " = ? AND " + PRESET_IDENTIFIER
                + " = ?", new String[] { getPresetCreatorString(this.element), this.element.getLocalIdentifier() },
                null, null, null);
        this.element.assignedApps = new ArrayList<IApp>();
        
        if (capp.moveToFirst()) {
            do {
                String appPackage = capp.getString(capp.getColumnIndex(APP_PACKAGE));
                
                App app = getCache().getApps().get(appPackage);
                if (app == null) {
                    Log.w(this, String.format("Unavailable preset cached (App '%s' not found).", appPackage));
                    this.element.missingApps.add(new MissingApp(appPackage));
                    
                } else {
                    this.element.assignedApps.add(app);
                }
            } while (capp.moveToNext());
        }
        capp.close();
        
        // load context annotations out of the cache
        if (!getCache().getContextAnnotations().containsKey(this.element)) {
            getCache().getContextAnnotations().put(this.element,
                    new HashMap<IPrivacySetting, List<ContextAnnotation>>());
        }
        this.element.contextAnnotations = getCache().getContextAnnotations().get(this.element);
        
    }
    
    
    @Override
    protected void storeElementData(SQLiteDatabase wdb, SQLiteQueryBuilder qb) {
        ContentValues cv = new ContentValues();
        cv.put(NAME, this.element.name);
        cv.put(DESCRIPTION, this.element.description);
        cv.put(DELETED, String.valueOf(this.element.deleted));
        
        wdb.update(TBL_PRESET, cv, CREATOR + " = ? AND " + IDENTIFIER + " = ?", new String[] {
                getPresetCreatorString(this.element), this.element.getLocalIdentifier() });
    }
    
    
    @Override
    protected void deleteElementData(SQLiteDatabase wdb, SQLiteQueryBuilder qb) {
        // delete preset granted privacy setting value references
        wdb.delete(TBL_GrantPSValue, PRESET_CREATOR + " = ? AND " + PRESET_IDENTIFIER + " = ?", new String[] {
                getPresetCreatorString(this.element), this.element.getLocalIdentifier() });
        
        // delete preset assigned apps references
        wdb.delete(TBL_PresetAssignedApp, PRESET_CREATOR + " = ? AND " + PRESET_IDENTIFIER + " = ?", new String[] {
                getPresetCreatorString(this.element), this.element.getLocalIdentifier() });
        
        // delete preset
        wdb.delete(TBL_PRESET, CREATOR + " = ? AND " + IDENTIFIER + " = ?", new String[] {
                getPresetCreatorString(this.element), this.element.getLocalIdentifier() });
        
        // delete context annotations
        for (List<ContextAnnotation> cas : this.element.contextAnnotations.values()) {
            for (ContextAnnotation ca : cas) {
                ca.delete();
            }
        }
        
    }
    
    
    protected void assignApp(IApp app) {
        SQLiteDatabase wdb = getDoh().getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(PRESET_IDENTIFIER, this.element.getLocalIdentifier());
            cv.put(PRESET_CREATOR, getPresetCreatorString(this.element));
            cv.put(APP_PACKAGE, app.getIdentifier());
            
            wdb.insert(PersistenceConstants.TBL_PresetAssignedApp, null, cv);
        } finally {
            wdb.close();
        }
    }
    
    
    protected void removeApp(IApp app) {
        SQLiteDatabase wdb = getDoh().getWritableDatabase();
        try {
            wdb.rawQuery("DELETE FROM " + TBL_PresetAssignedApp + " WHERE " + PRESET_CREATOR + " = ? AND "
                    + PRESET_IDENTIFIER + " = ? AND " + APP_PACKAGE + " = ?", new String[] {
                    getPresetCreatorString(this.element), this.element.getLocalIdentifier(), app.getIdentifier() });
        } finally {
            wdb.close();
        }
    }
    
    
    protected void assignPrivacySetting(IPrivacySetting ps, String value) {
        SQLiteDatabase wdb = getDoh().getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(PRIVACYSETTING_RESOURCEGROUP_PACKAGE, ps.getResourceGroup().getIdentifier());
            cv.put(PRIVACYSETTING_IDENTIFIER, ps.getLocalIdentifier());
            cv.put(PRESET_CREATOR, getPresetCreatorString(this.element));
            cv.put(PRESET_IDENTIFIER, this.element.getLocalIdentifier());
            cv.put(GRANTEDVALUE, value);
            
            try {
                wdb.insertOrThrow(TBL_GrantPSValue, null, cv);
            } catch (SQLException sqle) {
                wdb.update(TBL_GrantPSValue, cv, PRIVACYSETTING_RESOURCEGROUP_PACKAGE + " = ? AND "
                        + PRIVACYSETTING_IDENTIFIER + " = ? AND " + PRESET_CREATOR + " = ? AND " + PRESET_IDENTIFIER
                        + " = ?", new String[] { ps.getResourceGroup().getIdentifier(), ps.getLocalIdentifier(),
                        getPresetCreatorString(this.element), this.element.getLocalIdentifier() });
            }
        } finally {
            wdb.close();
        }
    }
    
    
    protected void removePrivacySetting(IPrivacySetting ps) {
        SQLiteDatabase wdb = getDoh().getWritableDatabase();
        try {
            wdb.rawQuery("DELETE FROM " + TBL_GrantPSValue + " WHERE " + PRIVACYSETTING_RESOURCEGROUP_PACKAGE
                    + " = ? AND " + PRIVACYSETTING_IDENTIFIER + " = ? AND " + PRESET_CREATOR + " = ? AND "
                    + PRESET_IDENTIFIER + " = ?",
                    new String[] { ps.getResourceGroup().getIdentifier(), ps.getLocalIdentifier(),
                            getPresetCreatorString(this.element), this.element.getLocalIdentifier() });
        } finally {
            wdb.close();
        }
    }
    
    
    /**
     * Creates the data <b>in the persistence</b> for the {@link Preset} specified with the parameters. Links this
     * {@link PresetPersistenceProvider} to the newly created object.
     * 
     * @param creator
     *            creator of the preset, null for user
     * @param identifier
     *            identifier of the preset
     * @param name
     *            name of the preset
     * @param description
     *            description of the preset
     * @return a {@link Preset} object that is linked to the newly created persistence data and this
     *         {@link PresetPersistenceProvider}, or null, if the creation was not possible
     */
    public Preset createElementData(IModelElement creator, String identifier, String name, String description) {
        // store in db
        SQLiteDatabase sqldb = getDoh().getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(CREATOR, creator == null ? PACKAGE_SEPARATOR : creator.getIdentifier());
            cv.put(IDENTIFIER, identifier);
            cv.put(NAME, name);
            cv.put(DESCRIPTION, description);
            cv.put(DELETED, Boolean.FALSE.toString());
            if (sqldb.insert(TBL_PRESET, null, cv) == -1) {
                return null;
            }
        } finally {
            sqldb.close();
        }
        
        // create associated object
        Preset result = new Preset(creator, identifier);
        this.element = result;
        result.setPersistenceProvider(this);
        
        return result;
    }
    
}
