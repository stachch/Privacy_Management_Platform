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
package de.unistuttgart.ipvs.pmp.model.xml;

import java.util.List;

import de.unistuttgart.ipvs.pmp.model.Model;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetSet;

/**
 * Interface for the {@link Model} to the PMP-XML-UTILITIES
 * 
 * @author Tobias Kuhn
 * 
 */
public interface IXMLInterface {
    
    /**
     * Exports the selected presets into an {@link IPreset}.
     * 
     * @param presets
     * @return
     */
    public IPresetSet exportPresets(List<IPreset> presets);
    
    
    /**
     * Imports the selected presets into the Model.
     * 
     * @param presets
     * @param override
     *            if existing presets shall be overridden or a new preset shall be generated
     */
    public void importPresets(List<de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPreset> presets, boolean override)
            throws InvalidPresetSetException;
    
}
