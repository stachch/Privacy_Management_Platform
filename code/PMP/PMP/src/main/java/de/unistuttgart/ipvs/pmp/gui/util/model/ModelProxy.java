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
package de.unistuttgart.ipvs.pmp.gui.util.model;

import android.content.Context;
import de.unistuttgart.ipvs.pmp.gui.util.model.mockup.MockupModel;
import de.unistuttgart.ipvs.pmp.model.IModel;
import de.unistuttgart.ipvs.pmp.model.Model;
import de.unistuttgart.ipvs.pmp.model.ipc.IPCProvider;

/**
 * Model proxy to easily switch between the real model and a mockup via ({@link ModelProxy#set(boolean, Context)}.
 * You can get the active model by {@link ModelProxy#get()}.
 * 
 * @author Tobias Kuhn
 * 
 */
public class ModelProxy {
    
    protected static final IModel real = Model.getInstance();
    protected static final IModel mockup = MockupModel.instance;
    
    private static boolean isMockup = false;
    private static IModel instance = real;
    
    
    /**
     * 
     * @return the model set in the model proxy.
     */
    public static IModel get() {
        return instance;
    }
    
    
    /**
     * 
     * @param toMockup
     *            true for mockup, false for real
     * @param context
     */
    public static void set(boolean toMockup, Context activityContext) {
        isMockup = toMockup;
        if (toMockup) {
            IPCProvider.getInstance().startUpdate();
            instance = mockup;
            MockupControl.init(activityContext);
        } else {
            IPCProvider.getInstance().endUpdate();
            instance = real;
        }
    }
    
    
    public static boolean isMockup() {
        return isMockup;
    }
    
    
    private ModelProxy() {
    }
    
}
