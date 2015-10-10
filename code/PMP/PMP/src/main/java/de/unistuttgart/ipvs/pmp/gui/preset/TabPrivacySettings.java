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
import java.util.List;
import java.util.Map.Entry;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.context.DialogContextChange;
import de.unistuttgart.ipvs.pmp.gui.privacysetting.DialogPrivacySettingEdit;
import de.unistuttgart.ipvs.pmp.gui.privacysetting.DialogPrivacySettingInformation;
import de.unistuttgart.ipvs.pmp.gui.privacysetting.ViewPrivacySettingPreset;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.dialog.DialogConfirmDelete;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;

/**
 * The "Assigned Privacy Settings" tab of a Preset
 * 
 * @author Marcus Vetter, Jakob Jarosch
 * 
 */
public class TabPrivacySettings extends Activity {
    
    /**
     * The preset instance
     */
    protected IPreset preset;
    
    /**
     * The Priavcy Setting list
     */
    private List<IPrivacySetting> allassignedPSList;
    
    /**
     * The list with all RGs and their PSs
     */
    private ArrayList<ArrayList<IPrivacySetting>> psList;
    private ArrayList<IResourceGroup> rgList;
    /**
     * The Privacy Setting expandable list view
     */
    private ExpandableListView psExpandableListView;
    
    /**
     * The Adapter
     */
    private AdapterPrivacySettings ppsAdapter;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get the preset
        String presetIdentifier = super.getIntent().getStringExtra(GUIConstants.PRESET_IDENTIFIER);
        this.preset = ModelProxy.get().getPreset(null, presetIdentifier);
        
        // Set view
        setContentView(R.layout.tab_preset_pss);
        
        // Initialize the data structures and add listener
        init();
        
        // Update the list of RGs and PSs
        updateList();
        
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }
    
    
    /**
     * Create the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preset_menu_pss_tab, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    /**
     * React to a selected menu item
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preset_tab_pss_assign_pss:
                DialogPrivacySettingAssign dialog = new DialogPrivacySettingAssign(TabPrivacySettings.this, this,
                        this.preset);
                
                // Check, if there are Apps available which are not assigned yet
                if (dialog.getSizeOfRGList() > 0) {
                    dialog.show();
                    dialog.getWindow().setGravity(Gravity.TOP);
                } else {
                    Toast.makeText(this, getString(R.string.preset_tab_pss_all_pss_already_assigned), Toast.LENGTH_LONG)
                            .show();
                }
                
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    
    
    /**
     * React on a selected context menu item
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        
        // Parse the menu info
        final ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item
                .getMenuInfo();
        int rgPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int psPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
        
        // Get the Privacy Setting
        final IPrivacySetting ps = TabPrivacySettings.this.psList.get(rgPos).get(psPos);
        
        // Handle
        switch (item.getItemId()) {
            case 0:
                new DialogPrivacySettingInformation(this, ps).show();
                
                return true;
            case 1:
                new DialogPrivacySettingEdit(this, ps, this.preset.getGrantedPrivacySettingValue(ps),
                        new DialogPrivacySettingEdit.ICallback() {
                            
                            @Override
                            public void result(boolean changed, String newValue) {
                                if (changed) {
                                    try {
                                        TabPrivacySettings.this.preset.assignPrivacySetting(ps, newValue);
                                    } catch (PrivacySettingValueException e) {
                                        Log.e(TabPrivacySettings.this,
                                                "Couldn't set new value for PrivacySetting, PSVE", e);
                                        GUITools.showToast(TabPrivacySettings.this,
                                                TabPrivacySettings.this.getString(R.string.failure_invalid_ps_value),
                                                Toast.LENGTH_LONG);
                                    }
                                    updateList();
                                }
                            }
                        }).show();
                
                return true;
                
            case 2:
                new DialogContextChange(TabPrivacySettings.this, this.preset, ps, null,
                        new DialogContextChange.ICallback() {
                            
                            @Override
                            public void callback() {
                                if (info.targetView != null && info.targetView instanceof ViewPrivacySettingPreset) {
                                    ((ViewPrivacySettingPreset) info.targetView).refresh();
                                }
                            }
                        }).show();
                return true;
                
            case 3:
                removePrivacySetting(ps);
                
                return true;
        }
        
        return false;
    }
    
    
    public void removePrivacySetting(final IPrivacySetting privacySetting) {
        new DialogConfirmDelete(this, getString(R.string.preset_confirm_remove_ps), getString(
                R.string.preset_confirm_remove_ps_description, privacySetting.getResourceGroup().getName() + " - "
                        + privacySetting.getName()), new DialogConfirmDelete.ICallback() {
            
            @Override
            public void callback(boolean confirmed) {
                if (confirmed) {
                    // Save expanded states
                    ArrayList<Boolean> expandedStates = new ArrayList<Boolean>();
                    for (int groupID = 0; groupID < TabPrivacySettings.this.ppsAdapter.getGroupCount(); groupID++) {
                        expandedStates.add(TabPrivacySettings.this.psExpandableListView.isGroupExpanded(groupID));
                    }
                    // Save index of RG, if this RG will disappear later
                    int tmpIndexOfRG = TabPrivacySettings.this.rgList.indexOf(privacySetting.getResourceGroup());
                    
                    // Remove the PS
                    TabPrivacySettings.this.preset.removePrivacySetting(privacySetting);
                    
                    // Update the list
                    updateList();
                    
                    // Restore the expanded states of the groups
                    if (!TabPrivacySettings.this.rgList.contains(privacySetting.getResourceGroup())) {
                        propagateExpandedGroups(expandedStates, tmpIndexOfRG);
                    }
                    
                    Toast.makeText(TabPrivacySettings.this, getString(R.string.preset_removed_ps_success),
                            Toast.LENGTH_LONG).show();
                }
            }
        }).show();
    }
    
    
    /**
     * Initialize the data structures
     */
    private void init() {
        // Setup the Preset ExpandableListView
        this.psExpandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view_assigned_pss);
        
        this.psExpandableListView.setClickable(true);
        this.psExpandableListView.setLongClickable(false);
        registerForContextMenu(this.psExpandableListView);
        
        // Add a context menu listener for long clicks
        this.psExpandableListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                
                ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
                int type = ExpandableListView.getPackedPositionType(info.packedPosition);
                
                // Open a context menu, if this was a click on a Privacy Setting (type=1)
                if (type == 1) {
                    menu.setHeaderTitle(R.string.choose_your_action);
                    menu.add(0, 0, 0, R.string.show_details);
                    menu.add(1, 1, 1, R.string.change_value);
                    menu.add(2, 2, 2, R.string.add_context);
                    menu.add(3, 3, 3, R.string.remove);
                }
            }
        });
        
        // Add the adapter
        this.ppsAdapter = new AdapterPrivacySettings(this, this.preset, this);
        this.psExpandableListView.setAdapter(this.ppsAdapter);
    }
    
    
    /**
     * Update the list of Apps and notify the addapter
     * 
     */
    public void updateList() {
        this.allassignedPSList = new ArrayList<IPrivacySetting>();
        
        /* Build a hash map with the RGs and their PSs */
        HashMap<IResourceGroup, ArrayList<IPrivacySetting>> RGPSMap = new HashMap<IResourceGroup, ArrayList<IPrivacySetting>>();
        
        for (IPrivacySetting ps : this.preset.getGrantedPrivacySettings()) {
            IResourceGroup rg = ps.getResourceGroup();
            
            // Add the PS to the allPsList
            this.allassignedPSList.add(ps);
            
            if (!RGPSMap.containsKey(ps.getResourceGroup())) {
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
        
        /* Build two lists, separated into RGs and PSs out of the map */
        this.rgList = new ArrayList<IResourceGroup>();
        this.psList = new ArrayList<ArrayList<IPrivacySetting>>();
        
        for (Entry<IResourceGroup, ArrayList<IPrivacySetting>> entry : RGPSMap.entrySet()) {
            this.rgList.add(entry.getKey());
            this.psList.add(entry.getValue());
        }
        
        // Show or hide the text view about no pss assigned
        TextView noAssignedPSs = (TextView) findViewById(R.id.preset_tab_pss_no_assigned);
        
        if (this.allassignedPSList.size() == 0) {
            noAssignedPSs.setVisibility(View.VISIBLE);
        } else {
            noAssignedPSs.setVisibility(View.GONE);
        }
        
        this.ppsAdapter.setPsList(this.psList);
        this.ppsAdapter.setRgList(this.rgList);
        this.ppsAdapter.notifyDataSetChanged();
    }
    
    
    /**
     * Restore the expanded-state of the nodes by using a list with the expanded states before removing one.
     * 
     * @param oldExpandedStates
     *            a list with the old expanded states
     * @param startByGroupID
     *            the start index (start with propagating by this groupID)
     */
    private void propagateExpandedGroups(ArrayList<Boolean> oldExpandedStates, int startByGroupID) {
        for (int groupID = startByGroupID; groupID < this.ppsAdapter.getGroupCount(); groupID++) {
            if (oldExpandedStates.get(groupID + 1)) {
                this.psExpandableListView.expandGroup(groupID);
            } else {
                this.psExpandableListView.collapseGroup(groupID);
            }
        }
    }
}
