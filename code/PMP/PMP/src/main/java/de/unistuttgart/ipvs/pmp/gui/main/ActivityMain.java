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
package de.unistuttgart.ipvs.pmp.gui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.app.ActivityApps;
import de.unistuttgart.ipvs.pmp.gui.preset.ActivityPresets;
import de.unistuttgart.ipvs.pmp.gui.resourcegroup.ActivityResourceGroups;
import de.unistuttgart.ipvs.pmp.gui.setting.ActivitySettings;
import de.unistuttgart.ipvs.pmp.gui.util.ActivityKillReceiver;
import de.unistuttgart.ipvs.pmp.gui.util.PMPPreferences;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.gui.util.view.AlwaysClickableButton;
import de.unistuttgart.ipvs.pmp.util.BootReceiver;

/**
 * The {@link ActivityMain} is the startup activity for PMP. It is also available in the App-Drawer.
 * 
 * @author Jakob Jarosch
 */
public class ActivityMain extends Activity {
    
    /**
     * The {@link ActivityKillReceiver}.
     */
    private ActivityKillReceiver akr;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        addListener();
        
        /* Initiating the ActivityKillReceiver. */
        this.akr = new ActivityKillReceiver(this);
        
        BootReceiver.startService(PMPApplication.getContext());
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        checkExpertMode();
        
        updateStatistics(ModelProxy.get().getApps().size(), ModelProxy.get().getResourceGroups().size(), ModelProxy
                .get().getPresets().size());
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        unregisterReceiver(this.akr);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    
    /**
     * Updates the statistics displayed in the bottom part of the main activity.
     * 
     * @param appsCount
     *            Count of the registered apps.
     * @param rgsCount
     *            Count of the installed resource groups.
     * @param presetsCount
     *            Count of the created presets.
     */
    public void updateStatistics(int appsCount, int rgsCount, int presetsCount) {
        TextView textApps = (TextView) findViewById(R.id.TextView_Apps);
        textApps.setText(getResources().getQuantityString(R.plurals.main_statistics_apps, appsCount, appsCount));
        
        TextView textRgs = (TextView) findViewById(R.id.TextView_RGs);
        textRgs.setText(getResources().getQuantityString(R.plurals.main_statistics_rgs, rgsCount, rgsCount));
        
        TextView textPresets = (TextView) findViewById(R.id.TextView_Presets);
        textPresets.setText(getResources().getQuantityString(R.plurals.main_statistics_presets, presetsCount,
                presetsCount));
    }
    
    
    /**
     * Registers all the listeners to the {@link Button}s.
     */
    private void addListener() {
        Button buttonApps = (Button) findViewById(R.id.Button_Apps);
        Button buttonRgs = (Button) findViewById(R.id.Button_RGs);
        AlwaysClickableButton buttonPresets = (AlwaysClickableButton) findViewById(R.id.Button_Presets);
        Button buttonSettings = (Button) findViewById(R.id.Button_Settings);
        
        /* The Apps-Button OnClickListener */
        buttonApps.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this, ActivityApps.class);
                ActivityMain.this.startActivity(intent);
            }
        });
        
        /* The RGs-Button OnClickListener */
        buttonRgs.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this, ActivityResourceGroups.class);
                ActivityMain.this.startActivity(intent);
            }
        });
        
        /* The Presets-Button OnClickListener */
        buttonPresets.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this, ActivityPresets.class);
                ActivityMain.this.startActivity(intent);
            }
        });
        // React when a touch occurs and the button is disabled.
        buttonPresets.setDisabledOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityMain.this, R.string.main_presets_disabled, Toast.LENGTH_LONG).show();
            }
        });
        
        /* The Settings-Button OnClickListener */
        buttonSettings.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this, ActivitySettings.class);
                ActivityMain.this.startActivity(intent);
            }
        });
    }
    
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                Intent intent = new Intent(ActivityMain.this, ActivityAbout.class);
                startActivity(intent);
                break;
            
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    
    
    /**
     * Hide the Presets if the Expert mode is disabled.
     */
    private void checkExpertMode() {
        Button buttonPresets = (Button) findViewById(R.id.Button_Presets);
        
        if (PMPPreferences.getInstance().isExpertMode()) {
            buttonPresets.setEnabled(true);
        } else {
            buttonPresets.setEnabled(false);
        }
    }
}
