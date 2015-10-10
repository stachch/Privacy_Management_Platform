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
package de.unistuttgart.ipvs.pmp.model.conflicts;

import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;

/**
 * The {@link ConflictPair} is a pair of two Presets which have one or more conflicts.
 * 
 * @author Jakob Jarosch
 */
public class ConflictPair {
    
    /**
     * Preset 1
     */
    private IPreset preset1;
    
    /**
     * Preset 2
     */
    private IPreset preset2;
    
    
    /**
     * Creates a new {@link ConflictPair}. It is regardless of whether the one or the other Preset is the first one.
     * 
     * @param preset1
     *            Preset 1
     * @param preset2
     *            Preset 2
     * 
     * @throws IllegalArgumentException
     *             Throws a IllegalArgumentException when one of the arguments is null.
     */
    public ConflictPair(IPreset preset1, IPreset preset2) {
        if (preset1 == null || preset2 == null) {
            throw new IllegalArgumentException("None of the arguments should be null.");
        }
        
        this.preset1 = preset1;
        this.preset2 = preset2;
    }
    
    
    /**
     * @return Returns the first preset.
     */
    public IPreset getPreset1() {
        return this.preset1;
    }
    
    
    /**
     * @return Returns the second preset.
     */
    public IPreset getPreset2() {
        return this.preset2;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof ConflictPair) {
            ConflictPair pair = (ConflictPair) o;
            /* Checking if p1.preset1 is equal to p2.preset1 or p2.preset2, and the same for the other preset. */
            if ((pair.getPreset1().equals(this.preset1) || pair.getPreset1().equals(this.preset2))
                    && (pair.getPreset2().equals(this.preset1) || pair.getPreset2().equals(this.preset2))) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Check if a given Preset is part of the {@link ConflictPair}.
     * 
     * @param preset
     *            Preset which should be checked.
     * @return Returns true when the Preset is part of the {@link ConflictPair}, otherwise false.
     */
    public boolean inPair(IPreset preset) {
        if (this.preset1.equals(preset) || this.preset2.equals(preset)) {
            return true;
        } else {
            return false;
        }
    }
}
