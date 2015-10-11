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
package de.unistuttgart.ipvs.pmp.shared.api.handler;

import android.os.Bundle;
import de.unistuttgart.ipvs.pmp.shared.api.ipc.command.IPC2PMPUpdateServiceFeaturesCommand;

/**
 * Handles reactions for the {@link IPC2PMPUpdateServiceFeaturesCommand}. Handlers are guaranteed to be called in a
 * separate {@link Thread}.
 * 
 * 
 * @author Tobias Kuhn
 * 
 */
public class PMPServiceFeatureUpdateHandler extends PMPHandler {
    
    /**
     * Called when the service feature update has arrived.
     * 
     * @param serviceFeatures
     *            a bundle, mapping the service feature identifiers to a boolean that corresponds to their enabled state
     */
    public void onUpdate(Bundle serviceFeatures) {
    }
    
    
    /**
     * Called when the update request has failed e.g. because the app is not yet registered.
     */
    public void onUpdateFailed() {
    }
    
}
