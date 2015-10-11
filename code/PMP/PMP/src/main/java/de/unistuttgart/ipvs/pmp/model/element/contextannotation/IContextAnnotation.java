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
package de.unistuttgart.ipvs.pmp.model.element.contextannotation;

import java.util.List;

import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;

/**
 * {@link IContextAnnotation} is the chain link between the {@link IContext} and the {@link IPreset}. It can override
 * the assigned {@link IPrivacySetting} values based on the activity of the {@link IContext}s.
 * 
 * @author Jakob Jarosch
 */
public interface IContextAnnotation extends IModelElement {
    
    /**
     * @return the <b>unique</b> identifier of the {@link IContextAnnotation}.
     */
    @Override
    public String getIdentifier();
    
    
    /**
     * 
     * @return the {@link IPreset} where this {@link IContextAnnotation} annotates a privacy setting value
     */
    public IPreset getPreset();
    
    
    /**
     * 
     * @return the {@link IPrivacySetting} which is annotated
     */
    public IPrivacySetting getPrivacySetting();
    
    
    /**
     * 
     * @return the {@link IContext} that is calculated for this {@link IContextAnnotation}
     */
    public IContext getContext();
    
    
    /**
     * Sets the context for this annotation.
     * 
     * @param context
     * @throws InvalidConditionException
     */
    public void setContext(IContext context, String condition) throws InvalidConditionException;
    
    
    /**
     * 
     * @return the condition of the {@link IContext} under which this {@link IContextAnnotation} shall be overriding the
     *         {@link IPrivacySetting} value
     */
    public String getContextCondition();
    
    
    /**
     * Sets the condition of the context under which this annotation shall be overriding the ps value
     * 
     * @param condition
     * @throws InvalidConditionException
     */
    public void setContextCondition(String condition) throws InvalidConditionException;
    
    
    /**
     * 
     * @return the context condition in human-readable form
     */
    public String getHumanReadableContextCondition() throws InvalidConditionException;
    
    
    /**
     * 
     * @return the value of the {@link IPrivacySetting} that it is overridden by in the annotated preset, if the
     *         condition is true in the assigned {@link IContext}
     */
    public String getOverridePrivacySettingValue();
    
    
    /**
     * Sets the value of the {@link IPrivacySetting} that is overridden by in the annotated preset
     * 
     * @throws PrivacySettingValueException
     */
    public void setOverridePrivacySettingValue(String value) throws PrivacySettingValueException;
    
    
    /**
     * 
     * @return true, if and only if this annotation is currently active, i.e. it should override the privacy setting
     *         value
     */
    public boolean isActive();
    
    
    /**
     * 
     * @return if it is active, the value to override, else null
     */
    public String getCurrentPrivacySettingValue();
    
    
    /**
     * @param preset
     *            the preset which shall be checked for conflicts
     * @return a list of {@link IContextAnnotation} from the preset <code>preset</code> where each entry of the list
     *         could possibly override this context annotation's value.
     */
    public List<IContextAnnotation> getConflictingContextAnnotations(IPreset preset);
    
    
    /**
     * @param preset
     *            the preset which shall be checked for conflicts
     * @return true, if and only if the value of {@link IPrivacySetting} from the preset <code>preset</code> could
     *         possibly override this context annotation's value.
     */
    public boolean isPrivacySettingConflicting(IPreset preset);
}
