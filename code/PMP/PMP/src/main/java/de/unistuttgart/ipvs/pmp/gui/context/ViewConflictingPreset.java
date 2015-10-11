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
package de.unistuttgart.ipvs.pmp.gui.context;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.privacysetting.DialogPrivacySettingEdit;
import de.unistuttgart.ipvs.pmp.gui.privacysetting.DialogPrivacySettingEdit.ICallback;
import de.unistuttgart.ipvs.pmp.gui.util.dialog.DialogConfirmDelete;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;

public class ViewConflictingPreset extends LinearLayout {
    
    private IContextAnnotation contextAnnotation;
    private IPreset preset;
    
    private boolean conflictsRemaining = true;
    
    
    @SuppressWarnings("deprecation")
    public ViewConflictingPreset(Context context, IContextAnnotation contextAnnotation, IPreset preset) {
        super(context);
        
        this.contextAnnotation = contextAnnotation;
        this.preset = preset;
        
        /* load the layout from the xml file */
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View entryView = inflater.inflate(R.layout.listitem_context_conflict, null);
        
        entryView.setLayoutParams(new ListView.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        
        addView(entryView);
        
        refresh();
        addListener();
    }
    
    
    private void refresh() {
        boolean conflictsRemaining = false;
        
        ((TextView) findViewById(R.id.TextView_PresetName)).setText(this.preset.getName());
        
        /* List all apps */
        ((LinearLayout) findViewById(R.id.LinearLayout_ConflictingApps)).removeAllViews();
        
        for (IApp app : this.contextAnnotation.getPreset().getAssignedApps()) {
            if (this.preset.getAssignedApps().contains(app)) {
                /* Create an entry for the App */
                addView(app);
            }
        }
        
        LinearLayout psConflict = (LinearLayout) findViewById(R.id.LinearLayout_PrivacySettingsConflict);
        if (this.contextAnnotation.isPrivacySettingConflicting(this.preset)) {
            psConflict.setVisibility(View.VISIBLE);
            conflictsRemaining = true;
        } else {
            psConflict.setVisibility(View.GONE);
        }
        
        /* Update the PrivacySetting Description */
        ((TextView) findViewById(R.id.TextView_PS_Description)).setText(getContext().getString(
                R.string.context_conflict_ps_description, this.preset.getName()));
        
        /* List all contexts */
        boolean contextConflict = false;
        ((LinearLayout) findViewById(R.id.LinearLayout_ConflictingContexts)).removeAllViews();
        for (IContextAnnotation ca : this.contextAnnotation.getConflictingContextAnnotations(this.preset)) {
            addView(ca);
            
            contextConflict = true;
            conflictsRemaining = true;
        }
        
        LinearLayout contextConflicts = (LinearLayout) findViewById(R.id.LinearLayout_ContextConflicts);
        if (contextConflict) {
            contextConflicts.setVisibility(View.VISIBLE);
        } else {
            contextConflicts.setVisibility(View.GONE);
        }
        
        if (!conflictsRemaining) {
            ((ImageView) findViewById(R.id.ImageView_ExandedState)).setImageResource(R.drawable.icon_success);
            ((LinearLayout) findViewById(R.id.LinearLayout_DetailsContainer)).setVisibility(View.GONE);
        }
    }
    
    
    private void addListener() {
        ((LinearLayout) findViewById(R.id.LinearLayout_Toggle)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (!ViewConflictingPreset.this.conflictsRemaining) {
                    return;
                }
                
                LinearLayout container = (LinearLayout) findViewById(R.id.LinearLayout_DetailsContainer);
                ImageView indicator = (ImageView) findViewById(R.id.ImageView_ExandedState);
                if (container.getVisibility() == View.VISIBLE) {
                    container.setVisibility(View.GONE);
                    indicator.setImageResource(R.drawable.icon_expand_closed);
                } else {
                    container.setVisibility(View.VISIBLE);
                    indicator.setImageResource(R.drawable.icon_expand_opened);
                }
            }
        });
        
        ((Button) findViewById(R.id.Button_ChangePrivacySetting)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new DialogPrivacySettingEdit(getContext(), ViewConflictingPreset.this.contextAnnotation
                        .getPrivacySetting(),
                        ViewConflictingPreset.this.preset
                                .getGrantedPrivacySettingValue(ViewConflictingPreset.this.contextAnnotation
                                        .getPrivacySetting()), new ICallback() {
                            
                            @Override
                            public void result(boolean save, String newValue) {
                                if (save) {
                                    try {
                                        ViewConflictingPreset.this.preset.assignPrivacySetting(
                                                ViewConflictingPreset.this.contextAnnotation.getPrivacySetting(),
                                                newValue);
                                        refresh();
                                    } catch (PrivacySettingValueException e) {
                                        Toast.makeText(getContext(),
                                                getContext().getString(R.string.preset_invalid_ps_value),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }).show();
            }
        });
        
        ((Button) findViewById(R.id.Button_RemovePrivacySetting)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new DialogConfirmDelete(getContext(), getContext().getString(R.string.preset_confirm_remove_ps),
                        getContext().getString(
                                R.string.preset_confirm_remove_ps_description,
                                ViewConflictingPreset.this.contextAnnotation.getPrivacySetting().getResourceGroup()
                                        .getName()
                                        + " - "
                                        + ViewConflictingPreset.this.contextAnnotation.getPrivacySetting().getName()),
                        new DialogConfirmDelete.ICallback() {
                            
                            @Override
                            public void callback(boolean confirmed) {
                                if (confirmed) {
                                    ViewConflictingPreset.this.preset
                                            .removePrivacySetting(ViewConflictingPreset.this.contextAnnotation
                                                    .getPrivacySetting());
                                    refresh();
                                }
                            }
                        }).show();
            }
        });
        
    }
    
    
    private void addView(final IApp app) {
        LinearLayout conflictingApps = (LinearLayout) findViewById(R.id.LinearLayout_ConflictingApps);
        
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View appView = inflater.inflate(R.layout.listitem_context_conflict_app, null);
        
        ((TextView) appView.findViewById(R.id.TextView_AppName)).setText(app.getName());
        ((ImageView) appView.findViewById(R.id.ImageView_AppIcon)).setImageDrawable(app.getIcon());
        
        Button remove1 = (Button) appView.findViewById(R.id.Button_Remove1);
        remove1.setText(this.contextAnnotation.getPreset().getName());
        remove1.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ViewConflictingPreset.this.contextAnnotation.getPreset().removeApp(app);
                refresh();
            }
        });
        
        Button remove2 = (Button) appView.findViewById(R.id.Button_Remove2);
        remove2.setText(this.preset.getName());
        remove2.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ViewConflictingPreset.this.preset.removeApp(app);
                refresh();
            }
        });
        
        conflictingApps.addView(appView);
    }
    
    
    private void addView(IContextAnnotation ca) {
        LinearLayout conflictingContexts = (LinearLayout) findViewById(R.id.LinearLayout_ConflictingContexts);
        
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contextView = inflater.inflate(R.layout.listitem_context_conflict_context, null);
        
        /* Read out context condition */
        String condition;
        try {
            condition = this.contextAnnotation.getHumanReadableContextCondition();
        } catch (InvalidConditionException e) {
            Log.e(this, "Condition can't be converted into a human readable version", e);
            condition = ca.getContextCondition();
        }
        
        /* Read out override value */
        String overrideValue;
        try {
            overrideValue = this.contextAnnotation.getPrivacySetting().getHumanReadableValue(
                    this.contextAnnotation.getOverridePrivacySettingValue());
        } catch (PrivacySettingValueException e) {
            Log.e(this, "PrivacySetting value can't be converted into a human readable version", e);
            overrideValue = ca.getOverridePrivacySettingValue();
        }
        
        ((TextView) contextView.findViewById(R.id.TextView_ContextCondition)).setText(condition);
        ((TextView) contextView.findViewById(R.id.TextView_OverrideValue)).setText(overrideValue);
        
        ((ImageView) contextView.findViewById(R.id.ImageView_ContextIcon)).setImageDrawable(this.contextAnnotation
                .getContext().getIcon());
        
        ((Button) contextView.findViewById(R.id.Button_Change)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new DialogContextChange(getContext(), ViewConflictingPreset.this.preset,
                        ViewConflictingPreset.this.contextAnnotation.getPrivacySetting(),
                        ViewConflictingPreset.this.contextAnnotation, new DialogContextChange.ICallback() {
                            
                            @Override
                            public void callback() {
                                refresh();
                            }
                        });
            }
        });
        
        conflictingContexts.addView(contextView);
    }
}
