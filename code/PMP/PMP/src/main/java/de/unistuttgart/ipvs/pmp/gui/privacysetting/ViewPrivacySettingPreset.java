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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.context.DialogConflictingContexts;
import de.unistuttgart.ipvs.pmp.gui.context.DialogContextChange;
import de.unistuttgart.ipvs.pmp.gui.preset.AdapterPrivacySettings;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.OnShortLongClickListener;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;

/**
 * The {@link ViewPrivacySettingPreset} represents one assigned {@link IPrivacySetting}. Each view has an expandable
 * list of all assigned {@link IContextAnnotation}s. If no annotations are assigned the list can't be expanded. So the
 * context menu will be shown directly on a short touch.
 * 
 * @author Jakob Jarosch
 */
public class ViewPrivacySettingPreset extends LinearLayout {
    
    /**
     * The {@link IPreset} to which the {@link IPrivacySetting} is assigned.
     */
    private IPreset preset;
    
    /**
     * The {@link IPrivacySetting} which is represented by this view.
     */
    private IPrivacySetting privacySetting;
    
    /**
     * The {@link Adapter} which holds all the {@link IPrivacySetting}s assigned to the {@link IPreset}.
     */
    private AdapterPrivacySettings adapter;
    
    
    /**
     * Creates a new view for a {@link IPrivacySetting} shown in a {@link IPreset}.
     * 
     * @param context
     *            {@link Context} which is required for view creation.
     * @param preset
     *            {@link IPreset} which contains the {@link IPrivacySetting}.
     * @param privacySetting
     *            {@link IPrivacySetting} which should be represented by this view.
     * @param adapter
     *            {@link Adapter} which holds all {@link IPrivacySetting} of the {@link IPreset}.
     */
    @SuppressWarnings("deprecation")
    public ViewPrivacySettingPreset(Context context, IPreset preset, IPrivacySetting privacySetting,
            AdapterPrivacySettings adapter) {
        super(context);
        
        this.preset = preset;
        this.privacySetting = privacySetting;
        this.adapter = adapter;
        
        // Load the layout
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.listitem_preset_ps, null);
        
        v.setLayoutParams(new ListView.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        
        addView(v);
        
        // Set LayoutParams of view to fill_parent for width.
        setLayoutParams(new ListView.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        
        addListener();
        
        refresh();
    }
    
    
    /**
     * Updates the UI elements.
     */
    public void refresh() {
        /*
         * Update the Privacy Setting name.
         */
        ((TextView) findViewById(R.id.TextView_Name_PS)).setText(this.privacySetting.getName());
        
        /*
         * Update the Privacy Setting value. When the value which should be assigned is invalid,
         * then show the plain text and mark it with red.
         */
        TextView value = (TextView) findViewById(R.id.TextView_Value);
        try {
            value.setText(getContext().getString(R.string.value)
                    + ": "
                    + this.privacySetting.getHumanReadableValue(this.preset
                            .getGrantedPrivacySettingValue(this.privacySetting)));
            value.setTextColor(GUIConstants.COLOR_TEXT_GRAYED_OUT);
        } catch (PrivacySettingValueException e) {
            Log.e(this, "The Privacy Setting value is invalid and is beeing marked red in the GUI", e);
            value.setText(getContext().getString(R.string.value) + ": "
                    + this.preset.getGrantedPrivacySettingValue(this.privacySetting));
            value.setTextColor(GUIConstants.COLOR_BG_RED);
        }
        
        /*
         * Update the list of contexts.
         */
        ((LinearLayout) findViewById(R.id.LinearLayout_Contexts)).removeAllViews();
        for (IContextAnnotation context : this.preset.getContextAnnotations(this.privacySetting)) {
            addContext(context);
        }
        
        /*
         * Check whether the number of contexts is greater than 0.
         * If not, hide the toggle indicator and hide the menu/empty context list.
         */
        if (this.preset.getContextAnnotations(this.privacySetting).size() == 0) {
            ((ImageView) findViewById(R.id.ImageView_State)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.LinearLayout_MenuAndContexts)).setVisibility(View.GONE);
        } else {
            ((ImageView) findViewById(R.id.ImageView_State)).setVisibility(View.VISIBLE);
        }
    }
    
    
    /**
     * Add listener to all clickable UI elements.
     */
    private void addListener() {
        /*
         * Add a on click listener to the Privacy Setting linear layout.
         * Opens menu directly when no context annotations are assigned,
         * otherwise the menu and context linear layout will be toggled.
         */
        ((LinearLayout) findViewById(R.id.LinearLayout_BasicInformations)).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (ViewPrivacySettingPreset.this.preset.getContextAnnotations(
                        ViewPrivacySettingPreset.this.privacySetting).size() > 0) {
                    toggleMenuAndContexts();
                } else {
                    ViewPrivacySettingPreset.this.adapter.reactOnItemClick(ViewPrivacySettingPreset.this);
                }
            }
        });
        
        /*
         * Add a on long click listener to the Privacy Setting linear layout,
         * which opens always a menu.
         */
        ((LinearLayout) findViewById(R.id.LinearLayout_BasicInformations))
                .setOnLongClickListener(new OnLongClickListener() {
                    
                    @Override
                    public boolean onLongClick(View v) {
                        ViewPrivacySettingPreset.this.adapter.reactOnItemClick(ViewPrivacySettingPreset.this);
                        
                        return true;
                    }
                });
        
        /*
         * Add a listener for the info button.
         */
        ((ImageButton) findViewById(R.id.ImageButton_Info)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new DialogPrivacySettingInformation(getContext(), ViewPrivacySettingPreset.this.privacySetting).show();
            }
        });
        
        /*
         * Add a listener for the edit button.
         */
        ((ImageButton) findViewById(R.id.ImageButton_Edit)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                /*
                 * Open the edit dialog and react on a change.
                 */
                new DialogPrivacySettingEdit(getContext(), ViewPrivacySettingPreset.this.privacySetting,
                        ViewPrivacySettingPreset.this.preset
                                .getGrantedPrivacySettingValue(ViewPrivacySettingPreset.this.privacySetting),
                        new DialogPrivacySettingEdit.ICallback() {
                            
                            @Override
                            public void result(boolean changed, String newValue) {
                                if (changed) {
                                    try {
                                        ViewPrivacySettingPreset.this.preset.assignPrivacySetting(
                                                ViewPrivacySettingPreset.this.privacySetting, newValue);
                                    } catch (PrivacySettingValueException e) {
                                        Log.e(ViewPrivacySettingPreset.this,
                                                "Couldn't set new value for PrivacySetting, PSVE", e);
                                        GUITools.showToast(getContext(),
                                                getContext().getString(R.string.failure_invalid_ps_value),
                                                Toast.LENGTH_LONG);
                                    }
                                    
                                    refresh();
                                }
                            }
                        }).show();
            }
        });
        
        /*
         * Add a listener to the add context button.
         */
        ((ImageButton) findViewById(R.id.ImageButton_AddContext)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                /*
                 * Open a dialog for configuring the new context.
                 */
                new DialogContextChange(getContext(), ViewPrivacySettingPreset.this.preset,
                        ViewPrivacySettingPreset.this.privacySetting, null, new DialogContextChange.ICallback() {
                            
                            @Override
                            public void callback() {
                                refresh();
                            }
                        });
            }
        });
        
        /*
         * Add a listener to the delete button.
         */
        ((ImageButton) findViewById(R.id.ImageButton_Delete)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ViewPrivacySettingPreset.this.adapter
                        .removePrivacySetting(ViewPrivacySettingPreset.this.privacySetting);
            }
        });
    }
    
    
    /**
     * Adds a new {@link IContextAnnotation} to the view container.
     * 
     * @param context
     *            {@link Context} which is required to create the new view.
     */
    private void addContext(final IContextAnnotation context) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.listitem_preset_ps_context, null);
        
        /*
         * Load correct texts into the view elements.
         */
        ((TextView) v.findViewById(R.id.TextView_Context_Name)).setText(context.getContext().getName());
        ((TextView) v.findViewById(R.id.TextView_Context_Value)).setText(getContext().getString(
                R.string.context_value_when_active)
                + ": " + context.getOverridePrivacySettingValue());
        try {
            ((TextView) v.findViewById(R.id.TextView_Context_Description)).setText(context
                    .getHumanReadableContextCondition());
        } catch (InvalidConditionException e) {
            ((TextView) v.findViewById(R.id.TextView_Context_Description)).setText(context.getContextCondition());
        }
        
        /*
         * Determine between, active, inactive and problems 'cause it is overridden by another preset
         */
        /* GEt all conflicting presets */
        final List<IPreset> conflictingPrivacySettings = new ArrayList<IPreset>();
        for (IPreset pr : ModelProxy.get().getPresets()) {
            if (!pr.equals(this.preset) && context.isPrivacySettingConflicting(pr)) {
                conflictingPrivacySettings.add(pr);
            }
        }
        
        /* Get all conflicting context annotations */
        final List<IContextAnnotation> conflictingContextAnnotations = new ArrayList<IContextAnnotation>();
        for (IPreset pr : ModelProxy.get().getPresets()) {
            List<IContextAnnotation> temp;
            if (!pr.equals(this.preset) && (temp = context.getConflictingContextAnnotations(pr)).size() > 0) {
                conflictingContextAnnotations.addAll(temp);
            }
        }
        
        OnClickListener oclConflicting = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new DialogConflictingContexts(getContext(), context, ViewPrivacySettingPreset.this).show();
            }
        };
        
        ImageView state = (ImageView) v.findViewById(R.id.ImageView_Context_State);
        if (conflictingContextAnnotations.size() > 0 || conflictingPrivacySettings.size() > 0) {
            state.setImageResource(R.drawable.icon_alert);
            state.setVisibility(View.VISIBLE);
            state.setOnClickListener(oclConflicting);
        } else if (context.isActive()) {
            state.setImageResource(R.drawable.icon_success);
            state.setVisibility(View.VISIBLE);
            state.setOnClickListener(null);
        } else {
            state.setVisibility(View.GONE);
            state.setOnClickListener(null);
        }
        
        /*
         * On click listener which reacts on short and long clicks.
         */
        OnShortLongClickListener ocl = new OnShortLongClickListener() {
            
            @Override
            public void onClick(View v) {
                new DialogContextChange(getContext(), ViewPrivacySettingPreset.this.preset,
                        ViewPrivacySettingPreset.this.privacySetting, context, new DialogContextChange.ICallback() {
                            
                            @Override
                            public void callback() {
                                refresh();
                            }
                        }).show();
            }
        };
        ((LinearLayout) v.findViewById(R.id.LinearLayout_Context)).setOnClickListener(ocl);
        ((LinearLayout) v.findViewById(R.id.LinearLayout_Context)).setOnLongClickListener(ocl);
        
        ((LinearLayout) findViewById(R.id.LinearLayout_Contexts)).addView(v);
    }
    
    
    /**
     * Toggles the visibility of the menu and context container.
     */
    public void toggleMenuAndContexts() {
        ImageView stateView = (ImageView) findViewById(R.id.ImageView_State);
        stateView.setImageResource(isListExpanded() ? R.drawable.icon_expand_closed : R.drawable.icon_expand_opened);
        
        LinearLayout menuAndContextsLayout = (LinearLayout) findViewById(R.id.LinearLayout_MenuAndContexts);
        menuAndContextsLayout.setVisibility(isListExpanded() ? View.GONE : View.VISIBLE);
    }
    
    
    /**
     * @return Returns whether the menu and context container is visible or not.
     */
    private boolean isListExpanded() {
        return (((LinearLayout) findViewById(R.id.LinearLayout_MenuAndContexts)).getVisibility() == View.VISIBLE);
    }
}
