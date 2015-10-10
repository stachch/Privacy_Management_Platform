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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;

/**
 * Simple SettingCheckBox
 * 
 * @author Marcus Vetter
 * 
 */
public class SettingCheckBox extends SettingAbstract<Boolean> {
    
    /**
     * The evaluator for setting each setting independently.
     */
    private ISettingEvaluator<Boolean> evaluator;
    
    /**
     * The CheckBox
     */
    protected CheckBox checkBox;
    
    /**
     * Linear layout of this view
     */
    private LinearLayout linlay;
    
    
    public SettingCheckBox(SettingsAdapter adapter, int name, int description, int icon,
            ISettingEvaluator<Boolean> evaluator) {
        super(adapter, name, description, icon);
        this.evaluator = evaluator;
    }
    
    
    @Override
    public Boolean getValue() {
        return this.evaluator.getValue();
    }
    
    
    @Override
    public void setValue(Boolean newValue) {
        this.evaluator.setValue(newValue);
    }
    
    
    @Override
    public View getView(Context context) {
        /* load the layout from the xml file */
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View entryView = inflater.inflate(R.layout.listitem_setting, null);
        
        /* Set name, description, icon and checked state of the checkbox of the requested SettingCheckBox */
        TextView name = (TextView) entryView.findViewById(R.id.Setting_Name);
        name.setText(getName());
        
        TextView description = (TextView) entryView.findViewById(R.id.Setting_Description);
        description.setText(getDescription());
        
        ImageView icon = (ImageView) entryView.findViewById(R.id.Setting_Icon);
        icon.setImageDrawable(getIcon());
        
        /* CheckBox and LinearLayout */
        this.checkBox = (CheckBox) entryView.findViewById(R.id.Setting_CheckBox);
        this.linlay = (LinearLayout) entryView.findViewById(R.id.Setting_Linear_Layout);
        
        /* Update check box */
        this.checkBox.setChecked(getValue());
        
        /* Add Listener */
        addListener();
        
        return entryView;
    }
    
    
    /**
     * Update the settingCheckbox, when the CheckBox has changed
     * 
     * @param checked
     *            true, if the CheckBox is now selected
     */
    private void checkBoxChanged(boolean checked) {
        setValue(checked);
        this.adapter.notifyDataSetChanged();
    }
    
    
    /**
     * Add listener to the CheckBox and LinearLayout
     */
    private void addListener() {
        OnClickListener changeValueListener = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                boolean newState = !getValue();
                SettingCheckBox.this.checkBox.setChecked(newState);
                checkBoxChanged(newState);
            }
        };
        
        this.checkBox.setOnClickListener(changeValueListener);
        this.linlay.setOnClickListener(changeValueListener);
    }
}
