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
package de.unistuttgart.ipvs.pmp.model.simple;

import de.unistuttgart.ipvs.pmp.model.IModel;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;

/**
 * The {@link ISimpleModel} is a contract for the model's "Simple Mode Addon" which helps simplify the UI's access to
 * model structures when in simple mode.
 * 
 * @author Tobias Kuhn
 * 
 */
public interface ISimpleModel {
    
    /**
     * Will convert the supplied model from expert to simple mode. <b>Note that all existing presets and privacy setting
     * configurations will be irreversibly deleted when this method is called!</b>
     * 
     * 
     * @param model
     */
    public void convertExpertToSimple(IModel model);
    
    
    /**
     * Checks whether you can operate on the supplied model in simple mode. If the result is false, you first have to
     * convert the model to simple mode by calling {@link ISimpleModel#convertExpertToSimple(IModel)}.
     * 
     * @param model
     * @return true, if and only if all other methods can perform on the supplied model
     */
    public boolean isSimpleMode(IModel model);
    
    
    /**
     * Activates and deactivates the specified service feature in the specified model. If the model is not configured to
     * work with simple mode, the result will be false.
     * 
     * @param model
     *            the model where this serviceFeature resides (for getting the presets)
     * @param serviceFeature
     * @param active
     *            whether the serviceFeature shall be active
     * @return true, if and only if the model was configured for simple mode and the service feature could be changed
     * @throws PrivacySettingValueException
     *             , if the serviceFeature has value requests that seem illegal to the underlying privacy setting
     */
    public boolean setServiceFeatureActive(IModel model, IServiceFeature serviceFeature, boolean active)
            throws PrivacySettingValueException;
    
}
