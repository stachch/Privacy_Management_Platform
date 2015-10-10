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
package de.unistuttgart.ipvs.pmp.model.element;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import de.unistuttgart.ipvs.pmp.model.ModelCache;
import de.unistuttgart.ipvs.pmp.model.PersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelIntegrityError;

/**
 * The basic model element functions to provide access to the persistence layer by dynamic data fetching.
 * 
 * @author Tobias Kuhn
 * 
 */
public abstract class ModelElement {
    
    /**
     * The identifier for this specific model element.
     */
    private String identifier;
    
    /**
     * If true, the actual content has been successfully loaded. If false, the element is just a carcass until a request
     * requires it to initialize.
     */
    private boolean cached;
    
    /**
     * The persistence provider for this model element.
     */
    protected ElementPersistenceProvider<? extends ModelElement> persistenceProvider;
    
    
    public ModelElement(String identifier) {
        Assert.nonNull(identifier, ModelIntegrityError.class, Assert.ILLEGAL_NULL, "identifier", identifier);
        this.cached = false;
        
        if (identifier == null) {
            throw new NullPointerException();
        }
        
        this.identifier = identifier;
    }
    
    
    /**
     * 
     * @return the identifier
     */
    public String getIdentifier() {
        return this.identifier;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else {
            try {
                ModelElement me = (ModelElement) o;
                return this.identifier.equals(me.identifier);
            } catch (ClassCastException cce) {
                return false;
            }
        }
    }
    
    
    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }
    
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + '@' + getIdentifier();
    }
    
    
    /**
     * Sets the {@link ElementPersistenceProvider} for this {@link ModelElement}. Should only invoked by another
     * {@link PersistenceProvider}.
     * 
     * @param persistenceProvider
     */
    public void setPersistenceProvider(ElementPersistenceProvider<? extends ModelElement> persistenceProvider) {
        this.persistenceProvider = persistenceProvider;
    }
    
    
    /**
     * <p>
     * Checks whether this element is already cached. If not, loads the data from the persistence and thus caches the
     * element.
     * </p>
     * 
     * <p>
     * <b>You should NEVER need to call this method outside of the model.</b> In lazy-initialization style it will be
     * automatically called from within the {@link ModelElement}. In eager-initialization style it will be automatically
     * called for you by the {@link PersistenceProvider}.
     * </p>
     * 
     * @return whether this object is cached after this call. false should only be possible, if no
     *         {@link ElementPersistenceProvider} was assigned.
     */
    public boolean checkCached() {
        if (!isCached()) {
            if (this.persistenceProvider != null) {
                this.persistenceProvider.loadElementData();
                this.cached = true;
            }
        }
        
        return this.cached;
    }
    
    
    /**
     * <p>
     * Forces the model to re-cache this element, <i>only if it was already cached</i>. This means all the changed data
     * associated with the component may get lost and the persistence state is READ once again.
     * </p>
     * <p>
     * <b>You should NEVER need to call this method outside of the model.</b>
     * </p>
     * 
     * @return whether the object was cached again
     */
    public boolean forceRecache() {
        if (isCached()) {
            if (this.persistenceProvider != null) {
                this.persistenceProvider.loadElementData();
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * <p>
     * Deletes this element non-reversibly. Does not update the {@link ModelCache}.
     * </p>
     * 
     * <p>
     * <b>You should NEVER need to call this method outside of the model.</b> It will be automatically called for you by
     * the {@link PersistenceProvider}. All calls on the object after it was deleted, will result in undefined behavior.
     * </p>
     */
    public void delete() {
        if (this.persistenceProvider == null) {
            return;
        }
        
        // assure persistence != null and all data available
        if (!checkCached()) {
            throw new ModelIntegrityError(Assert.format(Assert.ILLEGAL_UNCACHED, "ModelElement", this));
        }
        
        this.persistenceProvider.deleteElementData();
    }
    
    
    /**
     * Persists this element, i.e. writes its contents to the persistence layer.
     * 
     * @throws IllegalStateException
     *             if the element is not yet cached (it would make no sense to persist an element whose values were not
     *             edited before - if they were, it would be cached)
     * @return whether this object is persisted after this call. false should only be possible, if no
     *         {@link ElementPersistenceProvider} was assigned.
     */
    protected boolean persist() {
        if (this.persistenceProvider == null) {
            return false;
        }
        
        if (!isCached()) {
            throw new ModelIntegrityError(Assert.format(Assert.ILLEGAL_UNCACHED, "ModelElement", this));
        }
        
        this.persistenceProvider.storeElementData();
        return true;
    }
    
    
    /**
     * 
     * @return true, if this element is cached i.e. it represents the current persistence state, false otherwise
     */
    public boolean isCached() {
        return this.cached;
    }
    
    
    /**
     * Fetches the resources that the identifier package contains.
     * 
     * @param context
     *            context to fetch the package manager
     * @return the {@link Resources} for the identifier package or null, if none found or unable to load
     */
    public Resources resourcesOfIdentifierPackage(Context context) {
        try {
            return context.getPackageManager().getResourcesForApplication(getIdentifier());
        } catch (NameNotFoundException nnfe) {
            return null;
        }
    }
    
    
    protected static String collapseMapToString(Map<?, ?> map) {
        if (map == null) {
            return "null";
        } else if (map.isEmpty()) {
            return "empty";
        }
        
        StringBuilder sb = new StringBuilder("{");
        
        boolean firstLoopDone = false;
        for (Entry<?, ?> e : map.entrySet()) {
            if (firstLoopDone) {
                sb.append(", ");
            }
            sb.append("<");
            sb.append(objToString(e.getKey()));
            sb.append(" = ");
            sb.append(objToString(e.getValue()));
            sb.append(">");
            firstLoopDone = true;
        }
        
        return sb.append("}").toString();
    }
    
    
    protected static String collapseListToString(List<?> list) {
        if (list == null) {
            return "null";
        } else if (list.isEmpty()) {
            return "empty";
        }
        
        StringBuilder sb = new StringBuilder("{");
        
        boolean firstLoopDone = false;
        for (Object o : list) {
            if (firstLoopDone) {
                sb.append(", ");
            }
            sb.append(objToString(o));
            firstLoopDone = true;
        }
        
        return sb.append("}").toString();
    }
    
    
    private static String objToString(Object o) {
        if (o == null) {
            return "null";
        } else if (o instanceof ModelElement) {
            ModelElement me = (ModelElement) o;
            return me.getClass().getName() + '@' + me.getIdentifier();
        } else {
            return o.toString();
        }
    }
    
}
