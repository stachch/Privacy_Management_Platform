/*
 * Copyright 2012 pmp-android development team
 * Project: PMP-API
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
package de.unistuttgart.ipvs.pmp.resource.privacysetting;

import android.content.Context;
import de.unistuttgart.ipvs.pmp.resource.ResourceGroup;

/**
 * An internal PrivacySetting interface for a standard of accessing privacy settings. Note that this class saves the
 * parsing
 * of the privacy settings. It's only needed implementing feature however is the
 * {@link AbstractPrivacySetting#parseValue(String)} function.
 * 
 * @param <T>
 *            the type that is stored in this {@link AbstractPrivacySetting}.
 */
public abstract class AbstractPrivacySetting<T> {
    
    /**
     * The resource group that this resource is assigned to.
     */
    private ResourceGroup resourceGroup;
    
    /**
     * The identifier of this privacy setting in resource group.
     */
    private String identifier;
    
    /**
     * The view to display and change values
     */
    private IPrivacySettingView<T> view = null;
    
    
    /**
     * Assigns the resource group during registration.
     * 
     * <b>Do not call this method.</b>
     * 
     * @param resourceGroup
     */
    public final void assignResourceGroup(ResourceGroup resourceGroup, String identifier) {
        this.resourceGroup = resourceGroup;
        this.identifier = identifier;
    }
    
    
    /**
     * 
     * @return the associated {@link ResourceGroup}.
     */
    public final ResourceGroup getResourceGroup() {
        return this.resourceGroup;
    }
    
    
    /**
     * 
     * @return the human readable representation of the value for this privacy setting for the given locale
     * @throws PrivacySettingValueException
     *             if the supplied value does not match the format criteria.
     */
    public abstract String getHumanReadableValue(String value) throws PrivacySettingValueException;
    
    
    /**
     * <p>
     * Should return true, if the tuple (value, reference) meets the "permits more or equal" partial order. That is
     * reference ">=" value where ">=" is your defined "permits more or equal" partial order (which needs not to match
     * the classical >= partial order on numbers).
     * </p>
     * <p>
     * Note that since this is a partial order it must meet the reflexivity, antisymmetry and transitivity property.
     * </p>
     * 
     * @param value
     *            the value that needs to permit more or equal than reference
     * @param reference
     *            the reference for the check
     * @return true, if value permits more or equal to reference
     */
    public abstract boolean permits(T value, T reference);
    
    
    /**
     * Convenience method for checking {@link AbstractPrivacySetting#permits(Object, Object)} on String representations.
     * 
     * @see {@link AbstractPrivacySetting#permits(Object, Object)}
     * @throws PrivacySettingValueException
     *             if either reference or value do not meet the format criteria.
     */
    public boolean permits(String value, String reference) throws PrivacySettingValueException {
        return permits(parseValue(value), parseValue(reference));
    }
    
    
    /**
     * Convenience method for checking {@link AbstractPrivacySetting#permits(Object, Object)} for a specific app.
     * 
     * @param appIdentifier
     *            identifier of the app.
     * @param reference
     *            the reference instance of T for the check
     * @return true, if value permits more or equal to reference
     * @throws PrivacySettingValueException
     */
    public boolean permits(String appIdentifier, T reference) throws PrivacySettingValueException {
        T value = parseValue(getValue(appIdentifier));
        if (value == null) {
            return false;
        } else {
            return permits(value, reference);
        }
    }
    
    
    /**
     * Should create the representation of the string value for this {@link AbstractPrivacySetting} based on a given
     * String value that can later be recognized by {@link AbstractPrivacySetting#valueToString(Object)}.
     * If value is null, it should create an object that corresponds to "no privacy setting value set".
     * 
     * @param value
     *            the value stored in PMP
     * @return an object corresponding to value, an object corresponding to "no privacy setting value set", if value is
     *         null
     * @throws PrivacySettingValueException
     *             if the supplied value does not match the format criteria.
     */
    public abstract T parseValue(String value) throws PrivacySettingValueException;
    
    
    /**
     * Should create the string representation for an actual object for this {@link AbstractPrivacySetting} that can
     * later be recognized by {@link AbstractPrivacySetting#parseValue(String)}.
     * If value is null, it should return null.
     * 
     * @param value
     *            the value requested to be translated to string
     * @return a string corresponding to value, null, if value is null or not the desired input type
     */
    public abstract String valueToString(T value);
    
    
    /**
     * Gets the value for this {@link AbstractPrivacySetting} from PMP, if this {@link AbstractPrivacySetting} is
     * registered on a {@link ResourceGroup}.
     * 
     * @param appIdentifier
     * @return the value as stored in PMP for this privacy setting for appIdentifier, or null, if none found or none set
     */
    public String getValue(String appIdentifier) {
        return getResourceGroup().getPMPPrivacySettingValue(this.identifier, appIdentifier);
    }
    
    
    /**
     * Gets a {@link IPrivacySettingView} to display and edit the privacy setting value.
     * 
     * @param context
     *            context to use for the view
     * 
     * @return an {@link IPrivacySettingView} that can display and change privacy setting values
     */
    public IPrivacySettingView<T> getView(Context context) {
        if (this.view == null) {
            this.view = makeView(context);
        }
        return this.view;
    }
    
    
    /**
     * Must create a new {@link IPrivacySettingView} that can be stored for further edits. Note that there is only 1
     * View for each {@link AbstractPrivacySetting}.
     * 
     * @param context
     *            context to use for the view
     * @return an instance of {@link IPrivacySettingView} that can display and change privacy setting values and can be
     *         stored for further use
     */
    public abstract IPrivacySettingView<T> makeView(Context context);
    
    
    /**
     * Convenience method for {@link IPrivacySettingView#setViewValue(Object)} if you only have a string.
     * 
     * @param context
     *            context to use for the view
     * @param value
     *            the string to parse
     * @throws PrivacySettingValueException
     *             if the privacy setting rejected the value
     */
    public void setViewValue(Context context, String value) throws PrivacySettingValueException {
        getView(context).setViewValue(parseValue(value));
    }
    
    
    /**
     * Convenience method for {@link IPrivacySettingView#getViewValue(Object)} if you only want a string.
     * 
     * @param context
     *            context to use for the view
     * @return the compiled string
     * @throws PrivacySettingValueException
     *             if the privacy setting rejected the value
     */
    public String getViewValue(Context context) {
        return valueToString(getView(context).getViewValue());
    }
}
