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
package de.unistuttgart.ipvs.pmp.model.context;

import android.view.View;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;

/**
 * Defines the displaying view for an {@link IContext}.
 * 
 * @author Tobias Kuhn
 * 
 */
public interface IContextView {
    
    /**
     * 
     * @return the actual {@link View} to display in Android
     */
    public View asView();
    
    
    /**
     * 
     * @return the currently displayed value as a string
     */
    public String getViewCondition();
    
    
    /**
     * 
     * @param condition
     *            the value to change the display to
     * @throws InvalidConditionException
     *             if the value was not supported by this {@link IContext}.
     */
    public void setViewCondition(String condition) throws InvalidConditionException;
    
    
    /**
     * 
     * @return a default condition string that contains the initial state of the {@link IContextView}
     */
    public String getDefaultCondition();
    
}
