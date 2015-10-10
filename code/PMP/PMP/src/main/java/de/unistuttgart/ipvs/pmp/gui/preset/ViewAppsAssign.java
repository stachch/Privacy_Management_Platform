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
package de.unistuttgart.ipvs.pmp.gui.preset;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;

/**
 * This is the View of one Element (IAPP) of the PresetAssignAppsAdapter
 * 
 * @author Marcus Vetter
 * 
 */
public class ViewAppsAssign extends LinearLayout {
    
    /**
     * The CheckBox
     */
    protected final CheckBox checkBox;
    
    /**
     * Linear layout of this view
     */
    private final LinearLayout linlay;
    
    /**
     * Preset Assign Apps Adapter
     */
    private AdapterAppsAssign adapter;
    
    /**
     * The app of the view
     */
    private IApp app;
    
    
    /**
     * Constructor to instantiate the view
     * 
     * @param context
     *            context of the view
     * @param app
     *            the app of the view
     * @param presetAssignAppsAdapter
     *            the adapter of the assigned apps
     */
    public ViewAppsAssign(Context context, IApp app, AdapterAppsAssign presetAssignAppsAdapter) {
        super(context);
        
        this.app = app;
        this.adapter = presetAssignAppsAdapter;
        
        /* load the layout from the xml file */
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View entryView = inflater.inflate(R.layout.listitem_preset_assign_app, null);
        addView(entryView);
        
        /* Set icon, name, description of the requested App */
        ImageView icon = (ImageView) entryView.findViewById(R.id.ImageView_Icon);
        icon.setImageDrawable(app.getIcon());
        
        TextView name = (TextView) entryView.findViewById(R.id.TextView_Name);
        name.setText(app.getName());
        
        /* CheckBox and LinearLayout */
        this.checkBox = (CheckBox) entryView.findViewById(R.id.CheckBox_AssignApp);
        this.linlay = (LinearLayout) entryView.findViewById(R.id.LinearLayout);
        
        /* Update check box */
        boolean checked = this.adapter.getCheckBoxMap().get(app);
        checkBoxChanged(checked);
        this.checkBox.setChecked(checked);
        
        /* Add Listener */
        addListener();
    }
    
    
    /**
     * Update the Linear Layout (Color) and the HashMap, when the CheckBox has changed
     * 
     * @param checked
     *            true, if the CheckBox is now selected
     */
    private void checkBoxChanged(boolean checked) {
        this.adapter.getCheckBoxMap().put(this.app, checked);
        if (checked) {
            this.linlay.setBackgroundColor(GUIConstants.COLOR_BG_GREEN);
        } else {
            this.linlay.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    
    
    /**
     * Add listener to the CheckBox and LinearLayout
     */
    private void addListener() {
        this.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxChanged(isChecked);
            }
        });
        
        this.linlay.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                checkBoxChanged(!ViewAppsAssign.this.checkBox.isChecked());
                ViewAppsAssign.this.checkBox.setChecked(!ViewAppsAssign.this.checkBox.isChecked());
            }
        });
    }
}
