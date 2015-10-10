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
package de.unistuttgart.ipvs.pmp.gui.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.preset.ActivityPreset;
import de.unistuttgart.ipvs.pmp.gui.preset.AdapterPresets;
import de.unistuttgart.ipvs.pmp.gui.preset.DialogPresetEdit;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.PMPPreferences;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;

/**
 * The {@link TabPresets} displays all Presets which are assigned to this App.
 * 
 * @author Jakob Jarosch
 */
public class TabPresets extends Activity {
    
    /**
     * The reference to the real App in the model.
     */
    protected IApp app;
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        setContentView(R.layout.tab_app_presets);
        
        this.app = GUITools.getIAppFromIntent(getIntent());
        
        /* Switch between Expert Mode and Normal Mode */
        TextView tvDescriptionNormalMode = (TextView) findViewById(R.id.TextView_Description_Normal);
        LinearLayout tvDescriptionExpertMode = (LinearLayout) findViewById(R.id.TextView_Description_Expert);
        if (PMPPreferences.getInstance().isExpertMode()) {
            tvDescriptionNormalMode.setVisibility(View.GONE);
            tvDescriptionExpertMode.setVisibility(View.VISIBLE);
            
            refresh();
        } else {
            tvDescriptionNormalMode.setVisibility(View.VISIBLE);
            tvDescriptionExpertMode.setVisibility(View.GONE);
        }
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu_presets_tab, menu);
        return true;
    }
    
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_app_add_preset:
                if (!PMPPreferences.getInstance().isExpertMode()) {
                    Toast.makeText(this, getString(R.string.main_presets_disabled), Toast.LENGTH_LONG).show();
                    break;
                }
                
                DialogPresetEdit.ICallback callback = new DialogPresetEdit.ICallback() {
                    
                    @Override
                    public void refresh() {
                    }
                    
                    
                    @Override
                    public void openPreset(IPreset preset) {
                        preset.assignApp(TabPresets.this.app);
                        TabPresets.this.refresh();
                    }
                };
                new DialogPresetEdit(TabPresets.this, null, callback).show();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    
    
    /**
     * Initiates the list of all assigned Presets.
     */
    protected void refresh() {
        final List<IPreset> presetsList = new ArrayList<IPreset>();
        /* Only add undeleted presets to the list. */
        for (IPreset preset : this.app.getAssignedPresets()) {
            if (!preset.isDeleted()) {
                presetsList.add(preset);
            }
        }
        
        AdapterPresets presetsAdapter = new AdapterPresets(getApplicationContext(), presetsList);
        
        ListView presetListView = (ListView) findViewById(R.id.ListView_Presets);
        presetListView.setAdapter(presetsAdapter);
        
        presetListView.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
                openPreset(presetsList.get(position));
            }
        });
        
        /* Determine if the Presets-List is empty, and display a text instead. */
        TextView tv = (TextView) findViewById(R.id.TextView_NoPresets);
        if (presetsList.size() > 0) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
        }
    }
    
    
    /**
     * Open the PresetActivity for one Preset
     * 
     * @param preset
     *            Preset to open
     */
    public void openPreset(IPreset preset) {
        Intent i = new Intent(TabPresets.this, ActivityPreset.class);
        i.putExtra(GUIConstants.PRESET_IDENTIFIER, preset.getLocalIdentifier());
        startActivity(i);
    }
}
