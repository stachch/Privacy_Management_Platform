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

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.ActivityKillReceiver;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.gui.view.BasicTitleView;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;

/**
 * Activity of one Preset. It contains two tabs: "Assigned Apps" and "Assigned Privacy Settings"
 * 
 * @author Marcus Vetter
 * 
 */
public class ActivityPreset extends Activity {
    
    /**
     * The preset instance
     */
    private IPreset preset;
    
    /**
     * Tab components
     */
    @SuppressWarnings("deprecation")
    private LocalActivityManager lam;
    private TabHost mTabHost;
    
    /**
     * The {@link ActivityKillReceiver}.
     */
    private ActivityKillReceiver akr;
    
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get the preset
        String presetIdentifier = super.getIntent().getStringExtra(GUIConstants.PRESET_IDENTIFIER);
        this.preset = ModelProxy.get().getPreset(null, presetIdentifier);
        
        // Set view
        setContentView(R.layout.activity_preset);
        
        // Set up tabs
        this.lam = new LocalActivityManager(this, true);
        this.lam.dispatchCreate(savedInstanceState);
        
        this.mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        this.mTabHost.setup(this.lam);
        
        setupTabs();
        
        refresh();
        
        /* Initiating the ActivityKillReceiver. */
        this.akr = new ActivityKillReceiver(this);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        
        this.lam.dispatchResume();
    }
    
    
    public void refresh() {
        // Set up title view
        BasicTitleView title = (BasicTitleView) findViewById(R.id.activity_title);
        title.setTitle(this.preset.getName());
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();
        
        this.lam.dispatchPause(isFinishing());
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        this.lam.dispatchDestroy(isFinishing());
        
        unregisterReceiver(this.akr);
    }
    
    
    /**
     * Set up all tabs
     */
    private void setupTabs() {
        /* Assigned Apps Tab */
        TabSpec details = this.mTabHost.newTabSpec("tab_details");
        details.setIndicator(getString(R.string.details));
        
        // Create an Intent to start the inner activity
        Intent intentDetails = new Intent(this, TabDetails.class);
        intentDetails.putExtra(GUIConstants.PRESET_IDENTIFIER, this.preset.getLocalIdentifier());
        
        details.setContent(intentDetails);
        this.mTabHost.addTab(details);
        
        // Change the preferred size of the Tab-header
        View tab0 = this.mTabHost.getTabWidget().getChildAt(0);
        LayoutParams lp = tab0.getLayoutParams();
        lp.width = LayoutParams.WRAP_CONTENT;
        tab0.setLayoutParams(lp);
        
        /* Assigned Apps Tab */
        TabSpec apps = this.mTabHost.newTabSpec("tab_apps");
        apps.setIndicator(getString(R.string.apps));
        
        // Create an Intent to start the inner activity
        Intent intentApps = new Intent(this, TabApps.class);
        intentApps.putExtra(GUIConstants.PRESET_IDENTIFIER, this.preset.getLocalIdentifier());
        
        apps.setContent(intentApps);
        this.mTabHost.addTab(apps);
        
        // Change the preferred size of the Tab-header
        View tab1 = this.mTabHost.getTabWidget().getChildAt(1);
        lp = tab1.getLayoutParams();
        lp.width = LayoutParams.WRAP_CONTENT;
        tab1.setLayoutParams(lp);
        
        /* Assigned Privacy Settings Tab */
        TabSpec privacySettings = this.mTabHost.newTabSpec("tab_privacy_settings");
        privacySettings.setIndicator(getString(R.string.privacy_settings));
        
        // Create an Intent to start the inner activity
        Intent intentPrivacySettings = new Intent(this, TabPrivacySettings.class);
        intentPrivacySettings.putExtra(GUIConstants.PRESET_IDENTIFIER, this.preset.getLocalIdentifier());
        
        privacySettings.setContent(intentPrivacySettings);
        this.mTabHost.addTab(privacySettings);
        
        // Change the preferred size of the Tab-header
        View tab2 = this.mTabHost.getTabWidget().getChildAt(2);
        lp = tab2.getLayoutParams();
        lp.width = LayoutParams.WRAP_CONTENT;
        tab2.setLayoutParams(lp);
    }
    
}
