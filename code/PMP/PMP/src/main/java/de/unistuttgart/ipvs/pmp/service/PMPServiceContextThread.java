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
package de.unistuttgart.ipvs.pmp.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import de.unistuttgart.ipvs.pmp.model.Model;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelIntegrityError;
import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.preset.Preset;
import de.unistuttgart.ipvs.pmp.model.ipc.IPCProvider;
import de.unistuttgart.ipvs.pmp.util.FileLog;

/**
 * A thread on the {@link PMPService} to update all the contexts.
 * 
 * @author Tobias Kuhn
 * 
 */
public class PMPServiceContextThread extends Thread {
    
    private PMPService service;
    
    
    public PMPServiceContextThread(PMPService service) {
        this.service = service;
    }
    
    
    @Override
    public void run() {
        boolean stop = false;
        
        // TODO these should actually be apps
        Set<IPreset> updatePresets = new HashSet<IPreset>();
        
        // check each context for a new state
        for (IContext context : Model.getInstance().getContexts()) {
            List<IContextAnnotation> cas = Model.getInstance().getContextAnnotations(context);
            
            if (cas.size() > 0) {
                FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_CONTEXT_CHANGES, Level.INFO,
                        "Context update '%s'", context.getName());
                
                // TODO actually use that return value
                context.update(this.service.getBaseContext());
                
                // log which presets have contexts
                for (IContextAnnotation ca : cas) {
                    updatePresets.add(ca.getPreset());
                }
                
                stop = false;
            }
        }
        
        IPCProvider.getInstance().startUpdate();
        try {
            // update the presets
            for (IPreset preset : updatePresets) {
                Assert.instanceOf(preset, Preset.class, ModelIntegrityError.class, Assert.ILLEGAL_CLASS, "preset",
                        preset);
                Preset castPreset = (Preset) preset;
                castPreset.rollout();
            }
        } finally {
            IPCProvider.getInstance().endUpdate();
        }
        
        // notify the service
        this.service.contextsDone(stop);
    }
}
