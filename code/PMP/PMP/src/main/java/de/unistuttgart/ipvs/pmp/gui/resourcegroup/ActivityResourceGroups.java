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
package de.unistuttgart.ipvs.pmp.gui.resourcegroup;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.ActivityKillReceiver;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;

/**
 * The {@link ActivityResourceGroups} contains two tabs with the installed and the available Resourcegroups.
 * 
 * @author Jakob Jarosch
 */
public class ActivityResourceGroups extends TabActivity {
    
    /**
     * The {@link ActivityKillReceiver}.
     */
    private ActivityKillReceiver akr;
    
    private TabHost tabHost;
    
    private String activeTab;
    
    /**
     * Tab-Tags
     */
    private static final String TAB_INSTALLED = "installed";
    private static final String TAB_AVAILABLE = "available";
    
    
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_rgs);
        
        this.activeTab = checkExtendedIntentActions();
        
        setupTabs();
        
        this.tabHost.setCurrentTabByTag(this.activeTab);
        
        /* Initiating the ActivityKillReceiver. */
        this.akr = new ActivityKillReceiver(this);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        unregisterReceiver(this.akr);
    }
    
    
    /**
     * Checks if the Activity has been started with extended parameters like requested service features.
     */
    private void setupTabs() {
        this.tabHost = (TabHost) findViewById(android.R.id.tabhost);
        this.tabHost.setup();
        TabHost.TabSpec spec;
        Intent intent;
        
        intent = new Intent(getIntent()).setClass(this, TabInstalled.class);
        spec = this.tabHost.newTabSpec(TAB_INSTALLED).setIndicator(getString(R.string.installed)).setContent(intent);
        this.tabHost.addTab(spec);
        
        intent = new Intent(getIntent()).setClass(this, TabAvailable.class);
        spec = this.tabHost.newTabSpec(TAB_AVAILABLE).setIndicator(getString(R.string.available)).setContent(intent);
        this.tabHost.addTab(spec);
    }
    
    
    private String checkExtendedIntentActions() {
        if (GUITools.getIntentAction(getIntent()) != null
                && GUITools.getIntentAction(getIntent()).equals(GUIConstants.FILTER_AVAILABLE_RGS)) {
            return TAB_AVAILABLE;
        }
        
        return TAB_INSTALLED;
    }
}
