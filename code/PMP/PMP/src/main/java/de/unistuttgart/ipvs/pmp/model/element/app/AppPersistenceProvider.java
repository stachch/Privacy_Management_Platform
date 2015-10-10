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
package de.unistuttgart.ipvs.pmp.model.element.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.model.PersistenceConstants;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelIntegrityError;
import de.unistuttgart.ipvs.pmp.model.element.ElementPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.preset.Preset;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.ServiceFeature;
import de.unistuttgart.ipvs.pmp.xmlutil.XMLUtilityProxy;

/**
 * The persistence provider for {@link App}s.
 * 
 * @author Tobias Kuhn
 * 
 */
public class AppPersistenceProvider extends ElementPersistenceProvider<App> {
    
    /**
     * Whether to ignore not readable resources when loading the app solely to delete it.
     */
    private boolean suppressResources;
    
    
    public AppPersistenceProvider(App element) {
        super(element);
        this.suppressResources = false;
    }
    
    
    @Override
    protected void loadElementData(SQLiteDatabase rdb, SQLiteQueryBuilder qb) {
        
        this.element.serviceFeatures = getCache().getServiceFeatures().get(this.element);
        
        this.element.assignedPresets = new ArrayList<Preset>();
        for (Preset p : getCache().getAllPresets()) {
            if (p.isAppAssigned(this.element)) {
                this.element.assignedPresets.add(p);
            }
        }
        
        if (this.suppressResources) {
            // do not load AIS
            return;
        }
        
        InputStream is = null;
        try {
            Resources appResources = this.element.resourcesOfIdentifierPackage(PMPApplication.getContext());
            Assert.nonNull(appResources, ModelIntegrityError.class, Assert.ILLEGAL_UNINSTALLED_ACCESS, "app",
                    this.element);
            
            is = appResources.getAssets().open(PersistenceConstants.APP_XML_NAME);
            this.element.ais = XMLUtilityProxy.getAppUtil().parse(is);
            
        } catch (IOException e) {
            Log.e(this, "Did no longer find the app XML during loading its data.");
            e.printStackTrace();
        }
    }
    
    
    @Override
    protected void storeElementData(SQLiteDatabase wdb, SQLiteQueryBuilder qb) {
        // this method should never be called
        throw new ModelIntegrityError(Assert.format(Assert.ILLEGAL_METHOD, "storeElementData", this));
    }
    
    
    @Override
    protected void deleteElementData(SQLiteDatabase wdb, SQLiteQueryBuilder qb) {
        // app preset references are not supposed to be deleted,
        // the preset will handle unavailable elements itself  
        
        // delete app
        wdb.delete(TBL_APP, PACKAGE + " = ?", new String[] { this.element.getIdentifier() });
        
        // delete service features
        for (ServiceFeature sf : this.element.serviceFeatures.values()) {
            sf.delete();
        }
    }
    
    
    /**
     * Creates the data <b>in the persistence</b> for the {@link App} specified with the parameters. Links this
     * {@link AppPersistenceProvider} to the newly created object.
     * 
     * @param appPackage
     *            package of the app
     * @return an {@link App} object that is linked to the newly created persistence data and this
     *         {@link AppPersistenceProvider}, or null, if the creation was not possible
     */
    public App createElementData(String appPackage) {
        // store in db
        SQLiteDatabase sqldb = getDoh().getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(PACKAGE, appPackage);
            if (sqldb.insert(TBL_APP, null, cv) == -1) {
                return null;
            }
        } finally {
            sqldb.close();
        }
        
        // create associated object
        App result = new App(appPackage);
        this.element = result;
        result.setPersistenceProvider(this);
        
        return result;
    }
    
    
    public void setSuppressResources() {
        this.suppressResources = true;
    }
    
}
