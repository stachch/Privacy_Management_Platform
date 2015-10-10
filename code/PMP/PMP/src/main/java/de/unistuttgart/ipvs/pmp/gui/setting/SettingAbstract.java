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
package de.unistuttgart.ipvs.pmp.gui.setting;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import de.unistuttgart.ipvs.pmp.PMPApplication;

/**
 * Generic setting to store all kinds of settings. When implementing this class you should guarantee that name,
 * description and icon are shown appropriately.
 * 
 * @author Tobias Kuhn
 *         
 * @param <T>
 *            the type to be stored
 */
public abstract class SettingAbstract<T> implements ISettingEvaluator<T> {
    
    protected SettingsAdapter adapter;
    protected String name;
    protected String description;
    protected Drawable icon;
    
    
    /**
     * Creates a new setting.
     * 
     * @param name
     *            the name resource id
     * @param description
     *            the description resource id
     * @param icon
     *            the icon resource id
     */
    @SuppressWarnings("deprecation")
    public SettingAbstract(SettingsAdapter adapter, int name, int description, int icon) {
        this.adapter = adapter;
        this.name = PMPApplication.getContext().getString(name);
        this.description = PMPApplication.getContext().getString(name);
        this.icon = PMPApplication.getContext().getResources().getDrawable(icon);
    }
    
    
    /**
     * @return the name
     */
    protected String getName() {
        return this.name;
    }
    
    
    /**
     * @return the description
     */
    protected String getDescription() {
        return this.description;
    }
    
    
    /**
     * @return the icon
     */
    protected Drawable getIcon() {
        return this.icon;
    }
    
    
    /**
     * @param context
     *            {@link Context} that can be used for the {@link View}
     * @return a View that is capable of displaying T. Should have its listeners attached, so that a change on the UI
     *         component is directly reflected in this {@link SettingAbstract} via
     *         {@link ISettingEvaluator#setValue(Object)}.
     */
    public abstract View getView(Context context);
    
}
