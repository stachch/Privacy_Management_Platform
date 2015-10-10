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
package de.unistuttgart.ipvs.pmp.gui.privacysetting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;

/**
 * View used to display basic informations about a specific {@link IPrivacySetting}.
 * 
 * @author Jakob Jarosch
 */
public class ViewPrivacySettingBasicInformation extends LinearLayout {
    
    /**
     * {@link IPrivacySetting} which is represented by this view.
     */
    protected IPrivacySetting privacySetting;
    
    
    /**
     * Creates a new View.
     * 
     * @param context
     *            {@link Context} which is required for view creation.
     * @param privacySetting
     *            {@link IPrivacySetting} which should be represented.
     */
    public ViewPrivacySettingBasicInformation(Context context, IPrivacySetting privacySetting) {
        super(context);
        
        this.privacySetting = privacySetting;
        
        /* Load the layout */
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.listitem_rg_ps, null);
        addView(v);
        
        refresh();
        addListener();
    }
    
    
    /**
     * Updates the UI.
     */
    private void refresh() {
        ((TextView) findViewById(R.id.TextView_Title)).setText(this.privacySetting.getName());
        ((TextView) findViewById(R.id.TextView_Description)).setText(this.privacySetting.getDescription());
    }
    
    
    /**
     * Adds listener to all clickable UI elements.
     */
    private void addListener() {
        setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new DialogPrivacySettingInformation(getContext(),
                        ViewPrivacySettingBasicInformation.this.privacySetting).show();
            }
        });
    }
}
