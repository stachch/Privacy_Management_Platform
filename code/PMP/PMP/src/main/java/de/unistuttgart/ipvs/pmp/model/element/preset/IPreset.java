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

import java.util.List;

import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.element.IAtomicTransaction;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.missing.MissingApp;
import de.unistuttgart.ipvs.pmp.model.element.missing.MissingPrivacySettingValue;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.ServiceFeature;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;

/**
 * {@link IPreset} is the chain link between the {@link IResourceGroup}, its {@link IPrivacySetting}s and the
 * {@link IApp} . It grants the assigned {@link IApp} access to the assigned {@link IPrivacySetting}s.
 * 
 * @author Jakob Jarosch
 */
public interface IPreset extends IModelElement {
    
    /**
     * @return the <b>unique</b> identifier of the {@link IPreset}.
     */
    @Override
    public String getIdentifier();
    
    
    /**
     * 
     * @return the unique identifier for creator
     */
    public String getLocalIdentifier();
    
    
    /**
     * Returns whether this preset was bundled with an {@link IApp} or {@link IResourceGroup} or whether it was created
     * by the user.
     * 
     * @return false, if the preset was created by the user, true, if it was bundled with an {@link IApp} or
     *         {@link IResourceGroup}.
     */
    public boolean isBundled();
    
    
    /**
     * Returns the creator element of this {@link IPreset}. This can be either an {@link IApp} or {@link IResourceGroup}
     * , if the preset was delivered with them, or the user, in which case this method gracefully refers to the user as
     * "null".
     * 
     * @return the {@link IApp} or {@link IResourceGroup} with which this preset was bundled, or null, if it was not
     *         bundled
     */
    public IModelElement getCreator();
    
    
    /**
     * @return the name of the {@link IPreset}.
     */
    public String getName();
    
    
    /**
     * Sets the name of this {@link IPreset} to name.
     * 
     * @param name
     */
    public void setName(String name);
    
    
    /**
     * @return the description of the {@link IPreset}.
     */
    public String getDescription();
    
    
    /**
     * Sets the description of this {@link IPreset} to description.
     * 
     * @param description
     */
    public void setDescription(String description);
    
    
    /**
     * @return the used {@link IPrivacySetting}s by this preset.
     */
    public List<IPrivacySetting> getGrantedPrivacySettings();
    
    
    /**
     * @param privacySetting
     *            the privacy setting in question
     * @return the granted minimum value (assuming no context is active) of the {@link IPrivacySetting} for this service
     *         feature or null if none set
     */
    public String getGrantedPrivacySettingValue(IPrivacySetting privacySetting);
    
    
    /**
     * @return the assigned {@link IApp}s to this {@link IPreset}.
     */
    public List<IApp> getAssignedApps();
    
    
    /**
     * @return true if the {@link IApp} is assigned, false otherwise.
     */
    public boolean isAppAssigned(IApp app);
    
    
    /**
     * <p>
     * Adds an {@link IApp} to the {@link IPreset}.
     * </p>
     * 
     * <p>
     * <b>This method will cause a new verification of the {@link IApp}s {@link IServiceFeature}s.</b> So, if you have
     * multiple changes on the {@link IPreset} be sure to use {@link IPreset#startUpdate()} and
     * {@link IPreset#endUpdate()}.
     * </p>
     * 
     * @param app
     *            The {@link IApp} which should be added.
     */
    public void assignApp(IApp app);
    
    
    /**
     * Removes an {@link IApp} from the {@link IPreset}.
     * 
     * <p>
     * <b>This method will cause a new verification of the {@link IApp}s {@link IServiceFeature}s.</b> So, if you have
     * multiple changes on the {@link IPreset} be sure to use {@link IPreset#startUpdate()} and
     * {@link IPreset#endUpdate()}.
     * </p>
     * 
     * @param app
     *            The {@link IApp} which should be removed.
     */
    public void removeApp(IApp app);
    
    
    /**
     * Sets an {@link IPrivacySetting} to the {@link IPreset}.
     * 
     * <p>
     * <b>This method will cause a new verification of the {@link IServiceFeature}s of the {@link IApp}s assigned.</b>
     * So, if you have multiple changes on the {@link IPreset} be sure to use {@link IPreset#startUpdate()} and
     * {@link IPreset#endUpdate()}.
     * </p>
     * 
     * @param privacySetting
     *            The {@link IPrivacySetting} which should be set.
     * @param value
     *            the value of the privacy setting
     * @throws PrivacySettingValueException
     *             if the privacy setting is not compatible with the value
     */
    public void assignPrivacySetting(IPrivacySetting privacySetting, String value) throws PrivacySettingValueException;
    
    
    /**
     * Removes an {@link IPrivacySetting} from the {@link IPreset}.
     * 
     * <p>
     * <b>This method will cause a new verification of the {@link IServiceFeature}s of the {@link IApp}s assigned.</b>
     * So, if you have multiple changes on the {@link IPreset} be sure to use {@link IPreset#startUpdate()} and
     * {@link IPreset#endUpdate()}.
     * </p>
     * 
     * @param privacySetting
     *            The {@link IPrivacySetting} which should be removed.
     */
    public void removePrivacySetting(IPrivacySetting privacySetting);
    
    
    /**
     * Convenience method to add all required {@link PrivacySetting} values for a {@link ServiceFeature}.
     * Will subsequently call {@link IPreset#assignPrivacySetting(IPrivacySetting, String)}.
     * 
     * @param serviceFeature
     *            the service feature that shall be enabled by using this plugin
     * @throws PrivacySettingValueException
     *             if the privacy setting is not compatible with the value
     */
    public void assignServiceFeature(IServiceFeature serviceFeature) throws PrivacySettingValueException;
    
    
    /**
     * Stores the deleted property for this preset.
     * 
     * @param deleted
     *            whether this preset is currently deleted
     */
    public void setDeleted(boolean deleted);
    
    
    /**
     * 
     * @return true, if and only if this preset is marked as deleted
     */
    public boolean isDeleted();
    
    
    /**
     * @return true, if all {@link IResourceGroup}s and {@link IApp}s which this preset requires are available in PMP
     */
    public boolean isAvailable();
    
    
    /**
     * @return a list containing all the {@link MissingPrivacySettingValue}s that are missing for this preset
     *         to be available
     */
    public List<MissingPrivacySettingValue> getMissingPrivacySettings();
    
    
    /**
     * @return a list containing all the {@link MissingApp}s that are missing for this preset
     *         to be available
     */
    public List<MissingApp> getMissingApps();
    
    
    /**
     * Removes a {@link MissingApp} from the preset to make it available again.
     * 
     * @param missingApp
     *            the app to remove
     * @return true, if and only if the missing app was actually missing in this presets and the removal was successful
     */
    public boolean removeMissingApp(MissingApp missingApp);
    
    
    /**
     * Gets all the {@link IContextAnnotation}s for a privacy setting in this preset.
     * 
     * @param privacySetting
     *            annotated {@link IPrivacySetting}
     * @return a list of all {@link IContextAnnotation} that annotate privacySetting
     */
    public List<IContextAnnotation> getContextAnnotations(IPrivacySetting privacySetting);
    
    
    /**
     * Assigns a new {@link IContextAnnotation} to a privacy setting in this preset.
     * 
     * @param privacySetting
     *            annotated {@link IPrivacySetting}
     * @param context
     *            the context to use
     * @param contextCondition
     *            the condition for the context under which this annotation shall be active
     * @param overrideValue
     *            the privacy setting value that should override the preset value, if this context is active
     * @throws InvalidConditionException
     * @throws {@link PrivacySettingValueException}
     */
    public void assignContextAnnotation(IPrivacySetting privacySetting, IContext context, String contextCondition,
            String overrideValue) throws InvalidConditionException, PrivacySettingValueException;
    
    
    /**
     * Removes an existing {@link IContextAnnotation} from a privacy setting in this preset.
     * 
     * @param privacySetting
     *            annotated {@link IPrivacySetting}
     * @param contextAnnotation
     *            the context annotation to remove
     */
    public void removeContextAnnotation(IPrivacySetting privacySetting, IContextAnnotation contextAnnotation);
    
    
    /**
     * @param preset
     *            the preset which shall be checked for conflicts
     * @return a list of {@link IContextAnnotation}s from the preset <code>preset</code> where each entry of the list
     *         could possibly override this preset's context annotations' values.
     */
    public List<IContextAnnotation> getCACAConflicts(IPreset preset);
    
    
    /**
     * @param preset
     *            the preset which shall be checked for conflicts
     * @return a list of {@link IPrivacySetting} from the preset <code>preset</code> where each entry of the list
     *         could possibly override this preset's context annotations' values.
     */
    public List<IPrivacySetting> getCAPSConflicts(IPreset preset);
    
    
    /**
     * @param preset
     *            the preset which shall be checked for conflicts
     * @param ps
     *            the privacy setting which shall be checked for conflicts
     * @return a list of {@link IPrivacySetting} from the preset <code>preset</code> where each entry of the list
     *         could possibly override this preset's privacy settings' values.
     */
    public List<IPrivacySetting> getPSPSConflicts(IPreset preset);
    
    
    /**
     * 
     * @return a {@link PresetTransaction} that is capable of executing all the {@link IPreset} functionality in an
     *         atomic transaction context, implementing {@link IAtomicTransaction}.
     */
    public PresetTransaction getTransaction();
    
    
    /**
     * @param preset
     *            the preset which shall be checked for conflicts
     * @param ps
     *            the privacy setting which shall be checked for conflicts
     * @return true, if and only if the value of ps from the preset <code>preset</code> could
     *         possibly override this preset's value for ps.
     */
    public boolean isPrivacySettingConflicting(IPreset preset, IPrivacySetting ps);
    
}
