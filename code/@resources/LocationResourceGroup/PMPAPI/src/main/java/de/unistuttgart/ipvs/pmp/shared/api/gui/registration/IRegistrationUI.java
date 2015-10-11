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
package de.unistuttgart.ipvs.pmp.shared.api.gui.registration;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * Interface for an equivalent use of methods for UI-operations in {@link Dialog}s and {@link Activity}s.
 * 
 * @author Jakob Jarosch
 * 
 */
public interface IRegistrationUI {
    
    /**
     * @see Dialog#getContext()
     */
    public Context getContext();
    
    
    /**
     * @see View#findViewById(int)
     */
    public View findViewById(int resource);
    
    
    /**
     * Invokes an event on the given ui.
     * 
     * @param eventType
     *            Type of the event which should be invoked.
     * @param parameters
     *            Parameters as for example an error message.
     */
    public void invokeEvent(final RegistrationEventTypes eventType, final Object... parameters);
    
    
    /**
     * Closes the UI.
     */
    public void close();
    
}
