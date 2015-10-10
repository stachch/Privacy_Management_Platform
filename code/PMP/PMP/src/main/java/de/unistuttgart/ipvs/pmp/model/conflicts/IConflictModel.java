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

import java.util.List;

/**
 * The {@link IConflictModel} holds all conflicts between presets.
 * An instance of the interface can be created by calling {@link ConflictModel#getInstance()}.
 * 
 * @author Jakob Jarosch
 */
public interface IConflictModel {
    
    /**
     * Calculates all conflicts between the presets, on a second call it updates only the changed Presets.
     * 
     * @param callback
     *            The callback is invoked during the calculation.
     */
    public void calculateConflicts(IProcessingCallback callback);
    
    
    /**
     * Returns true when the list of conflicts is up to date, otherwise false.
     * To bring the List up to date invoke {@link IConflictModel#calculateConflicts(IProcessingCallback)}.
     * 
     * @return Returns true when the list of conflicts is up to date, otherwise false.
     */
    public boolean isUpToDate();
    
    
    /**
     * @return Returns all conflict pairs.
     */
    public List<ConflictPair> getConflicts();
}
