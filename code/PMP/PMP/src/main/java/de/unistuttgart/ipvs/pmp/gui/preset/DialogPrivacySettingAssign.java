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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.privacysetting.DialogPrivacySettingEdit;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;

/**
 * Dialog for assigning Privacy Settings to the Preset
 * 
 * @author Marcus Vetter
 */
public class DialogPrivacySettingAssign extends Dialog {
    
    /**
     * The Preset
     */
    private IPreset preset;
    
    /**
     * The list with all unassigned RGs
     */
    private ArrayList<IResourceGroup> rgList;
    
    /**
     * The list with all unassigned RGs and their PSs
     */
    protected ArrayList<ArrayList<IPrivacySetting>> psList;
    
    /**
     * The Privacy Setting expandable list view
     */
    private ExpandableListView psExpandableListView;
    
    /**
     * The PresetPSsTab
     */
    protected TabPrivacySettings presetPSsTab;
    
    
    /**
     * Necessary constructor
     * 
     * @param context
     *            the context
     * @param preset
     *            the Preset
     */
    public DialogPrivacySettingAssign(Context context, TabPrivacySettings presetPSsTab, IPreset preset) {
        super(context);
        this.preset = preset;
        this.presetPSsTab = presetPSsTab;
        
        // Create the list
        updateList();
    }
    
    
    /**
     * Called when the dialog is first created. Gets all elements of the gui
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.dialog_preset_assign_pss);
        
        // Initialize
        updateList();
        init();
        
    }
    
    
    private void updateList() {
        /* Build a hash map with all unassigned RGs and their PSs */
        HashMap<IResourceGroup, ArrayList<IPrivacySetting>> RGPSMap = new HashMap<IResourceGroup, ArrayList<IPrivacySetting>>();
        
        // Loop all RGs and their PSs
        for (IResourceGroup rg : ModelProxy.get().getResourceGroups()) {
            Loop: for (IPrivacySetting ps : rg.getPrivacySettings()) {
                // Check, if this Privacy Setting is an assigned Privacy Setting
                for (IPrivacySetting assignedPS : this.preset.getGrantedPrivacySettings()) {
                    if (ps == assignedPS) {
                        continue Loop;
                    }
                }
                
                if (!RGPSMap.containsKey(rg)) {
                    // The map does not contain the RG: Add it as key and the PS as value
                    ArrayList<IPrivacySetting> newList = new ArrayList<IPrivacySetting>();
                    newList.add(ps);
                    RGPSMap.put(rg, newList);
                } else {
                    // The map already contains the RG: Add the PS
                    ArrayList<IPrivacySetting> existingList = RGPSMap.get(rg);
                    existingList.add(ps);
                }
                
            }
        }
        
        /* Build two lists, separated into RGs and PSs out of the map */
        this.rgList = new ArrayList<IResourceGroup>();
        this.psList = new ArrayList<ArrayList<IPrivacySetting>>();
        
        for (Entry<IResourceGroup, ArrayList<IPrivacySetting>> entry : RGPSMap.entrySet()) {
            this.rgList.add(entry.getKey());
            this.psList.add(entry.getValue());
        }
    }
    
    
    private void init() {
        // Setup the Preset ExpandableListView
        this.psExpandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view_assign_pss);
        
        // Add the adapter
        AdapterPrivacySettingsAssign ppsAdapter = new AdapterPrivacySettingsAssign(getContext(), this.preset,
                this.rgList, this.psList);
        this.psExpandableListView.setAdapter(ppsAdapter);
        
        // Add the listener
        this.psExpandableListView.setOnChildClickListener(new OnChildClickListener() {
            
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final IPrivacySetting privacySetting = DialogPrivacySettingAssign.this.psList.get(groupPosition).get(
                        childPosition);
                
                new DialogPrivacySettingEdit(getContext(), privacySetting, null,
                        new DialogPrivacySettingEdit.ICallback() {
                            
                            @Override
                            public void result(boolean changed, String newValue) {
                                if (changed) {
                                    try {
                                        DialogPrivacySettingAssign.this.preset.assignPrivacySetting(privacySetting,
                                                newValue);
                                    } catch (PrivacySettingValueException e) {
                                        Log.e(DialogPrivacySettingAssign.this,
                                                "Couldn't set new value for PrivacySetting, PSVE", e);
                                        GUITools.showToast(getContext(),
                                                getContext().getString(R.string.failure_invalid_ps_value),
                                                Toast.LENGTH_LONG);
                                    }
                                    
                                }
                                DialogPrivacySettingAssign.this.presetPSsTab.updateList();
                            }
                        }).show();
                dismiss();
                return true;
            }
        });
    }
    
    
    /**
     * Get the size of the ResourceGroups-List
     * 
     * @return size of RG-List
     */
    public int getSizeOfRGList() {
        return this.rgList.size();
    }
    
}
