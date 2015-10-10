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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;

/**
 * Implementation of the {@link IConflictModel}.
 * 
 * @author Jakob Jarosch
 */
public class ConflictModel implements IConflictModel {
    
    private static final ConflictModel instance = new ConflictModel();
    
    /**
     * List holds all hashes from the Presets used in the last update.
     */
    private Map<IPreset, String> lastUpdatedHashes = new HashMap<IPreset, String>();
    
    /**
     * List holds all conflicting pairs found during the last calculation.
     */
    private List<ConflictPair> conflictPairs = new ArrayList<ConflictPair>();
    
    
    /**
     * Private constructor, singleton pattern.
     */
    private ConflictModel() {
    }
    
    
    /**
     * @return Returns the one and only instance of the {@link IConflictModel} implmentation.
     */
    public static IConflictModel getInstance() {
        return instance;
    }
    
    
    @Override
    public void calculateConflicts(IProcessingCallback callback) {
        new ConflictCalculator(callback).start();
    }
    
    
    @Override
    public boolean isUpToDate() {
        
        for (IPreset preset : ModelProxy.get().getPresets()) {
            if (!preset.toString().equals(this.lastUpdatedHashes.get(preset))) {
                return false;
            }
        }
        
        return true;
    }
    
    
    @Override
    public List<ConflictPair> getConflicts() {
        return this.conflictPairs;
    }
    
    /**
     * Internal thread for conflict calculation.
     * 
     * @author Jakob Jarosch
     */
    class ConflictCalculator extends Thread {
        
        private IProcessingCallback callback;
        
        
        public ConflictCalculator(IProcessingCallback callback) {
            this.callback = callback;
            
            /* Prevent from NullPointerException */
            if (this.callback == null) {
                this.callback = new NullProcessingCallback();
            }
        }
        
        
        @Override
        public void run() {
            this.callback.stepMessage("Checking for updated Presets...");
            
            /* temporary store all to be updated presets. */
            List<IPreset> presets = ModelProxy.get().getPresets();
            List<IPreset> updatedPresets = new ArrayList<IPreset>();
            int currentCount = 0;
            int totalCount = presets.size();
            
            for (IPreset preset : presets) {
                currentCount++;
                this.callback.progressUpdate(currentCount, totalCount);
                
                String presetString = preset.toString();
                if (!presetString.equals(ConflictModel.this.lastUpdatedHashes.get(preset))) {
                    updatedPresets.add(preset);
                    
                    /* directly update the stored hash, following execution will update the conflicts */
                    ConflictModel.this.lastUpdatedHashes.put(preset, presetString);
                    
                    /* Remove all conflict pairs where the updated preset is inside. */
                    Iterator<ConflictPair> iter = ConflictModel.this.conflictPairs.iterator();
                    while (iter.hasNext()) {
                        if (iter.next().inPair(preset)) {
                            iter.remove();
                        }
                    }
                }
            }
            
            this.callback.stepMessage("Checking for possible conflicts...");
            /* now iterating over all updated presets and compare them with all others */
            currentCount = 0;
            totalCount = updatedPresets.size() * presets.size();
            
            for (IPreset preset : updatedPresets) {
                for (IPreset comparedPreset : presets) {
                    if (comparedPreset.equals(preset)) {
                        continue;
                    }
                    
                    currentCount++;
                    this.callback.progressUpdate(currentCount, totalCount);
                    
                    /* For optimization skip all already known conflicts. */
                    if (!ConflictModel.this.conflictPairs.contains(new ConflictPair(preset, comparedPreset))) {
                        if (preset.getPSPSConflicts(comparedPreset).size() > 0
                                || preset.getCACAConflicts(comparedPreset).size() > 0
                                || preset.getCAPSConflicts(comparedPreset).size() > 0) {
                            ConflictModel.this.conflictPairs.add(new ConflictPair(preset, comparedPreset));
                        }
                    }
                }
            }
            
            this.callback.finished();
        }
    }
}
