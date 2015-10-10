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
package de.unistuttgart.ipvs.pmp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelIntegrityError;
import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.context.location.LocationContext;
import de.unistuttgart.ipvs.pmp.model.context.time.TimeContext;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.App;
import de.unistuttgart.ipvs.pmp.model.element.app.AppPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.ContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.ContextAnnotationPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.preset.Preset;
import de.unistuttgart.ipvs.pmp.model.element.preset.PresetPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySettingPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.ResourceGroup;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.ResourceGroupPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.ServiceFeature;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.ServiceFeaturePersistenceProvider;

/**
 * General persistence provider which provides all the data necessary for the model. Can be hooked in by
 * {@link Observer}s to get the updated {@link ModelCache}s.
 * 
 * @author Tobias Kuhn
 * 
 */
public class PersistenceProvider extends Observable implements PersistenceConstants {
    
    /**
     * Singleton stuff
     */
    private static final PersistenceProvider instance = new PersistenceProvider();
    
    
    public static PersistenceProvider getInstance() {
        return instance;
    }
    
    
    /**
     * Singleton constructor
     */
    private PersistenceProvider() {
    }
    
    
    /**
     * Constructor for descendants.
     * 
     * @param parent
     *            the singleton object obviously
     */
    protected PersistenceProvider(PersistenceProvider parent) {
        this.doh = parent.doh;
        this.cache = parent.cache;
    }
    
    /**
     * D'oh!
     */
    private DatabaseOpenHelper doh;
    
    /**
     * Cache
     */
    private ModelCache cache;
    
    
    /**
     * Reinitializes the database connection. Strongly recommended after a context change in
     * {@link PMPApplication#getContext()}.
     */
    public void reloadDatabaseConnection() {
        this.doh = new DatabaseOpenHelper(PMPApplication.getContext());
        regenerateCache();
    }
    
    
    /**
     * Tells the {@link PersistenceProvider} and conclusively the {@link Model} to release the cached data.
     * Should probably only be invoked when Android explicitly requests this behavior. Afterwards, all data
     * from the persistence has to be cached again.
     */
    public void releaseCache() {
        this.cache = null;
        setChanged();
        notifyObservers(this.cache);
    }
    
    
    /**
     * 
     * @return the {@link DatabaseOpenHelper} for this persistence layer.
     */
    protected DatabaseOpenHelper getDoh() {
        return this.doh;
    }
    
    
    /**
     * @return the current cache, if one is present, or null if none was created
     */
    protected ModelCache getCache() {
        return this.cache;
    }
    
    
    /**
     * Caches all objects right now. This means, that the {@link ModelCache} object will only contain fully cached,
     * fully linked objects. Useful, if you want the non-lazy initialization behavior, maybe because you are going to
     * access all data of it in rapid succession anyway.
     */
    public void cacheEverythingNow() {
        if (this.doh == null) {
            reloadDatabaseConnection();
        }
        if (this.cache == null) {
            regenerateCache();
        }
        
        for (ResourceGroup rg : this.cache.getResourceGroups().values()) {
            rg.checkCached();
        }
        for (Map<String, PrivacySetting> psMap : this.cache.getPrivacySettings().values()) {
            for (PrivacySetting pl : psMap.values()) {
                pl.checkCached();
            }
        }
        for (App a : this.cache.getApps().values()) {
            a.checkCached();
        }
        for (Map<String, ServiceFeature> sfMap : this.cache.getServiceFeatures().values()) {
            for (ServiceFeature sl : sfMap.values()) {
                sl.checkCached();
            }
        }
        for (Map<String, Preset> pMap : this.cache.getPresets().values()) {
            for (Preset p : pMap.values()) {
                p.checkCached();
            }
        }
        for (Map<IPrivacySetting, List<ContextAnnotation>> psMap : this.cache.getContextAnnotations().values()) {
            for (List<ContextAnnotation> caList : psMap.values()) {
                for (ContextAnnotation ca : caList) {
                    ca.checkCached();
                }
            }
        }
        
    }
    
    
    /**
     * Creates a new {@link ModelCache} by creating a bare framework of unlinked objects from the persistence layer.
     * Also invokes the Observable connection so the {@link Model} knows the new cache.
     */
    private void regenerateCache() {
        this.cache = new ModelCache();
        
        SQLiteDatabase db = this.doh.getReadableDatabase();
        try {
            
            cacheAppsSFs(db);
            cacheRGsPSs(db);
            cachePresets(db);
            cacheCAs(db);
            cacheContexts(db);
            
        } finally {
            db.close();
        }
        
        setChanged();
        notifyObservers(this.cache);
    }
    
    
    /**
     * Caches all the {@link App}s and their {@link ServiceFeature}s.
     * 
     * @param db
     */
    private void cacheAppsSFs(SQLiteDatabase db) {
        SQLiteQueryBuilder builder = this.doh.builder();
        builder.setTables(TBL_APP);
        
        Cursor appCursor = builder.query(db, new String[] { PACKAGE }, null, null, null, null, null);
        
        if (appCursor.moveToFirst()) {
            do {
                String appPackage = appCursor.getString(appCursor.getColumnIndex(PACKAGE));
                App app = new App(appPackage);
                app.setPersistenceProvider(new AppPersistenceProvider(app));
                
                Map<String, ServiceFeature> thisAppsSFs = new HashMap<String, ServiceFeature>();
                
                // find the local SFs (don't think join is a wise idea)
                builder.setTables(TBL_SERVICEFEATURE);
                
                Cursor sfCursor = builder.query(db, new String[] { IDENTIFIER }, APP_PACKAGE + " = ?",
                        new String[] { appPackage }, null, null, null);
                
                if (sfCursor.moveToFirst()) {
                    do {
                        String sfIdentifier = sfCursor.getString(sfCursor.getColumnIndex(IDENTIFIER));
                        ServiceFeature sf = new ServiceFeature(app, sfIdentifier);
                        sf.setPersistenceProvider(new ServiceFeaturePersistenceProvider(sf));
                        
                        thisAppsSFs.put(sfIdentifier, sf);
                    } while (sfCursor.moveToNext());
                }
                sfCursor.close();
                
                // finalize App
                this.cache.getServiceFeatures().put(app, thisAppsSFs);
                this.cache.getApps().put(appPackage, app);
            } while (appCursor.moveToNext());
        }
        appCursor.close();
    }
    
    
    /**
     * Caches all the {@link ResourceGroup}s and their {@link PrivacySetting}s.
     * 
     * @param db
     */
    private void cacheRGsPSs(SQLiteDatabase db) {
        SQLiteQueryBuilder builder = this.doh.builder();
        builder.setTables(TBL_RESOURCEGROUP);
        
        Cursor rgCursor = builder.query(db, new String[] { PACKAGE }, null, null, null, null, null);
        
        if (rgCursor.moveToFirst()) {
            do {
                String rgPackage = rgCursor.getString(rgCursor.getColumnIndex(PACKAGE));
                ResourceGroup rg = new ResourceGroup(rgPackage);
                rg.setPersistenceProvider(new ResourceGroupPersistenceProvider(rg));
                
                Map<String, PrivacySetting> thisRGsPSs = new HashMap<String, PrivacySetting>();
                
                // find the local PSs (don't think join is a wise idea)
                builder.setTables(TBL_PRIVACYSETTING);
                Cursor psCursor = builder.query(db, new String[] { IDENTIFIER }, RESOURCEGROUP_PACKAGE + " = ?",
                        new String[] { rgPackage }, null, null, null);
                
                if (psCursor.moveToFirst()) {
                    do {
                        String psIdentifier = psCursor.getString(psCursor.getColumnIndex(IDENTIFIER));
                        PrivacySetting ps = new PrivacySetting(rg, psIdentifier);
                        ps.setPersistenceProvider(new PrivacySettingPersistenceProvider(ps));
                        
                        thisRGsPSs.put(psIdentifier, ps);
                    } while (psCursor.moveToNext());
                }
                psCursor.close();
                
                // finalize RG
                this.cache.getPrivacySettings().put(rg, thisRGsPSs);
                this.cache.getResourceGroups().put(rgPackage, rg);
            } while (rgCursor.moveToNext());
        }
        rgCursor.close();
    }
    
    
    /**
     * Caches all the {@link Preset}s.
     * 
     * @param db
     */
    private void cachePresets(SQLiteDatabase db) {
        SQLiteQueryBuilder builder = this.doh.builder();
        builder.setTables(TBL_PRESET);
        
        Cursor cursor = builder.query(db, new String[] { CREATOR, IDENTIFIER }, null, null, null, null, null);
        
        if (cursor.moveToFirst()) {
            do {
                // find the data, translate it
                String creator = cursor.getString(cursor.getColumnIndex(CREATOR));
                IModelElement creatorElement = this.cache.getApps().get(creator);
                if (creatorElement == null) {
                    creatorElement = this.cache.getResourceGroups().get(creator);
                }
                String identifier = cursor.getString(cursor.getColumnIndex(IDENTIFIER));
                
                // create item
                Preset p = new Preset(creatorElement, identifier);
                p.setPersistenceProvider(new PresetPersistenceProvider(p));
                
                // apply to cache
                Map<String, Preset> creatorMap = this.cache.getPresets().get(creatorElement);
                if (creatorMap == null) {
                    creatorMap = new HashMap<String, Preset>();
                    this.cache.getPresets().put(creatorElement, creatorMap);
                }
                creatorMap.put(identifier, p);
                
            } while (cursor.moveToNext());
        }
        cursor.close();
        
    }
    
    
    /**
     * Caches all the {@link ContextAnnotation}s.
     * 
     * @param db
     */
    private void cacheCAs(SQLiteDatabase db) {
        SQLiteQueryBuilder builder = this.doh.builder();
        builder.setTables(TBL_CONTEXT_ANNOTATIONS);
        
        Cursor cursor = builder
                .query(db, new String[] { PRESET_CREATOR, PRESET_IDENTIFIER, PRIVACYSETTING_RESOURCEGROUP_PACKAGE,
                        PRIVACYSETTING_IDENTIFIER, PRESET_PRIVACY_SETTING_ANNOTATION_ID }, null, null, null, null, null);
        
        // keep a list of all the missing CAs
        List<String[]> missing = new ArrayList<String[]>();
        
        if (cursor.moveToFirst()) {
            do {
                // find the data, translate it
                String pCreator = cursor.getString(cursor.getColumnIndex(PRESET_CREATOR));
                String pIdentifier = cursor.getString(cursor.getColumnIndex(PRESET_IDENTIFIER));
                String psRGPackage = cursor.getString(cursor.getColumnIndex(PRIVACYSETTING_RESOURCEGROUP_PACKAGE));
                String psIdentifier = cursor.getString(cursor.getColumnIndex(PRIVACYSETTING_IDENTIFIER));
                int presetPSAId = cursor.getInt(cursor.getColumnIndex(PRESET_PRIVACY_SETTING_ANNOTATION_ID));
                
                // preset                              
                IModelElement pCreatorElement = this.cache.getApps().get(pCreator);
                if (pCreatorElement == null) {
                    pCreatorElement = this.cache.getResourceGroups().get(pCreator);
                }
                
                // translate
                Map<String, Preset> creatorPresets = this.cache.getPresets().get(pCreatorElement);
                if (creatorPresets == null) {
                    missing.add(new String[] { pCreator, pIdentifier, psRGPackage, psIdentifier });
                    continue;
                }
                Preset preset = creatorPresets.get(pIdentifier);
                if (preset == null) {
                    missing.add(new String[] { pCreator, pIdentifier, psRGPackage, psIdentifier });
                    continue;
                }
                
                // translate ps
                ResourceGroup psRG = this.cache.getResourceGroups().get(psRGPackage);
                if (psRG == null) {
                    missing.add(new String[] { pCreator, pIdentifier, psRGPackage, psIdentifier });
                    continue;
                }
                PrivacySetting ps = this.cache.getPrivacySettings().get(psRG).get(psIdentifier);
                if (ps == null) {
                    missing.add(new String[] { pCreator, pIdentifier, psRGPackage, psIdentifier });
                    continue;
                }
                
                // create item
                ContextAnnotation ca = new ContextAnnotation(preset, ps, presetPSAId);
                ca.setPersistenceProvider(new ContextAnnotationPersistenceProvider(ca));
                
                // apply to cache
                Map<IPrivacySetting, List<ContextAnnotation>> psList = this.cache.getContextAnnotations().get(preset);
                if (psList == null) {
                    psList = new HashMap<IPrivacySetting, List<ContextAnnotation>>();
                    this.cache.getContextAnnotations().put(preset, psList);
                }
                List<ContextAnnotation> caList = psList.get(ps);
                if (caList == null) {
                    caList = new ArrayList<ContextAnnotation>();
                    psList.put(ps, caList);
                }
                caList.add(ca);
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        // remove CAs where objects where missing
        SQLiteDatabase wdb = this.doh.getWritableDatabase();
        for (String[] ids : missing) {
            wdb.execSQL("DELETE FROM " + TBL_CONTEXT_ANNOTATIONS + " WHERE " + PRESET_CREATOR + " = ? AND "
                    + PRESET_IDENTIFIER + " = ? AND " + PRIVACYSETTING_RESOURCEGROUP_PACKAGE + " = ? AND "
                    + PRIVACYSETTING_IDENTIFIER + " = ?", ids);
        }
        wdb.close();
        
    }
    
    
    /**
     * Caches all the {@link IContext}s.
     * 
     * @param db
     */
    private void cacheContexts(SQLiteDatabase db) {
        this.cache.getContexts().add(new TimeContext());
        this.cache.getContexts().add(new LocationContext());
        
    }
    
    
    /**
     * Translates the creator of a preset into a DB string
     * 
     * @param preset
     * @return
     */
    public final static String getPresetCreatorString(IPreset preset) {
        if (preset.getCreator() == null) {
            return PersistenceConstants.PACKAGE_SEPARATOR;
        } else {
            return preset.getCreator().getIdentifier();
        }
    }
    
    
    /**
     * 
     * @param contextId
     * @return the {@link IContext} for contextName
     */
    protected final static IContext findContext(String contextId) {
        for (IContext context : Model.getInstance().getContexts()) {
            if (context.getIdentifier().equals(contextId)) {
                return context;
            }
        }
        
        throw new ModelIntegrityError(Assert.format(Assert.ILLEGAL_MISSING_CONTEXT, "contextId", contextId));
    }
    
}
