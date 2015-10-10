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
package de.unistuttgart.ipvs.pmp.resource.privacysetting;

import android.view.View;

/**
 * Defines the displaying view for an {@link AbstractPrivacySetting}.
 * 
 * @author Tobias Kuhn
 * 
 * @param <T>
 *            the type of the value
 */
public interface IPrivacySettingView<T> {
    
    /**
     * 
     * @return the actual {@link View} to display in Android
     */
    public View asView();
    
    
    /**
     * 
     * @return the currently displayed value as an instance of T
     */
    public T getViewValue();
    
    
    /**
     * 
     * @param value
     *            the value to change the display to
     * @throws PrivacySettingValueException
     *             if the value was not supported by this {@link AbstractPrivacySetting}.
     */
    public void setViewValue(T value) throws PrivacySettingValueException;
    
}
