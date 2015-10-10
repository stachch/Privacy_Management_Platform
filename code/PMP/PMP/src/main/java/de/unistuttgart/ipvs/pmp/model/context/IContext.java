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

import android.content.Context;
import android.graphics.drawable.Drawable;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;
import de.unistuttgart.ipvs.pmp.service.PMPService;

/**
 * Interface to describe a context that can switch its activity state based on its surrounding conditions (hence the
 * term "Context"). Has nothing to do with the Android {@link Context}.
 * 
 * @author Tobias Kuhn
 * 
 */
public interface IContext {
    
    /**
     * 
     * @return a unique, simple text identifier for this context
     */
    public String getIdentifier();
    
    
    /**
     * @return the localized name of the context
     */
    public String getName();
    
    
    /**
     * @return the localized description of the context
     */
    public String getDescription();
    
    
    /**
     * @return the icon of the context
     */
    public Drawable getIcon();
    
    
    /**
     * 
     * @param context
     *            the {@link Context} to use for this {@link IContextView} (don't confuse these!)
     * @return the {@link IContextView} for this {@link IContext}
     */
    public IContextView getView(Context context);
    
    
    /**
     * The main state update called by the {@link PMPService} in a different thread.
     * 
     * @param context
     *            the context of the {@link PMPService}
     * 
     * @return the time in {@link System#currentTimeMillis()} domain to perform the next update on
     */
    public long update(Context context);
    
    
    /**
     * Validates the last state against condition.
     * 
     * @param condition
     *            the condition String for this to check
     * @return true, if and only if condition was true after the last state update and the condition is valid
     */
    public boolean getLastState(String condition);
    
    
    /**
     * Creates a human-readable representation for condition.
     * 
     * @param condition
     * @return condition, in human-readable form
     * @throws InvalidConditionException
     *             if the value was not supported by this {@link IContext}.
     */
    public String makeHumanReadable(String condition) throws InvalidConditionException;
    
    
    /**
     * Checks whether condition is a valid condition for this context
     * 
     * @param condition
     *            the condition to check
     * @throws InvalidConditionException
     *             if condition is not valid
     */
    public void conditionValidOrThrow(String condition) throws InvalidConditionException;
    
}
