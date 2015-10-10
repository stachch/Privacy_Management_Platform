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
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;

/**
 * The "Assigned Apps" tab of a Preset
 * 
 * @author Marcus Vetter
 * 
 */
public class TabApps extends Activity {
    
    /**
     * The preset instance
     */
    private IPreset preset;
    
    /**
     * ListView of all Apps
     */
    private ListView appsListView;
    
    /**
     * List of all Apps
     */
    private List<IApp> appList;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get the preset
        String presetIdentifier = super.getIntent().getStringExtra(GUIConstants.PRESET_IDENTIFIER);
        this.preset = ModelProxy.get().getPreset(null, presetIdentifier);
        
        // Set view
        setContentView(R.layout.tab_preset_apps);
        
        // Initialize
        init();
        
        // Fill the list
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
        inflater.inflate(R.menu.preset_menu_apps_tab, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    /**
     * React to a selected menu item
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preset_tab_apps_assign_apps:
                DialogAppsAssign dialog = new DialogAppsAssign(TabApps.this, this.preset);
                
                // Check, if there are Apps available which are not assigned yet
                if (dialog.calcDisplayApps().size() > 0) {
                    dialog.show();
                } else {
                    Toast.makeText(this, getString(R.string.preset_tab_apps_all_apps_assigned), Toast.LENGTH_LONG)
                            .show();
                }
                
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    
    
    /**
     * Initialize the data structures
     */
    private void init() {
        
        // Setup the appsListView
        this.appsListView = (ListView) findViewById(R.id.listview_assigned_apps);
        this.appsListView.setClickable(true);
        this.appsListView.setLongClickable(false);
        registerForContextMenu(this.appsListView);
        
        // Add a context menu listener for long clicks
        this.appsListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(R.string.choose_your_action);
                menu.add(0, 0, 0, R.string.show_details);
                menu.add(1, 1, 0, R.string.remove);
            }
        });
        
        // React on clicked item
        this.appsListView.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
                
                openContextMenu(view);
                
            }
        });
    }
    
    
    /**
     * Update the list of apps
     * 
     */
    public void updateList() {
        
        this.appList = new ArrayList<IApp>();
        
        for (IApp app : this.preset.getAssignedApps()) {
            this.appList.add(app);
        }
        
        AdapterApps presetAppsAdapter = new AdapterApps(this, this.appList);
        this.appsListView.setAdapter(presetAppsAdapter);
        
        // Show or hide the text view about no apps assigned
        TextView noAssignedApps = (TextView) findViewById(R.id.preset_tab_apps_no_assigned);
        if (this.appList.size() == 0) {
            noAssignedApps.setVisibility(View.VISIBLE);
        } else {
            noAssignedApps.setVisibility(View.GONE);
        }
    }
    
    
    /**
     * React on a clicked item of the context menu
     */
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        // The menu information
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) menuItem.getMenuInfo();
        IApp app = this.appList.get(menuInfo.position);
        
        // Context menu of a deleted preset
        switch (menuItem.getItemId()) {
            case 0: // Clicked on "Show App details" 
                Intent intent = GUITools.createAppActivityIntent(app);
                GUITools.startIntent(intent);
                return true;
            case 1: // Clicked on "Delete App"
                this.preset.removeApp(app);
                updateList();
                return true;
        }
        
        return false;
    }
    
}
