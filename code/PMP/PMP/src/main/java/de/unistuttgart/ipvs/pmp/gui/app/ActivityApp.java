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

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.ActivityKillReceiver;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.gui.view.BasicTitleView;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;

/**
 * The {@link ActivityApp} displays a at PMP registered App.
 * For Details, Service Features and Presets is a tab available to display it.
 * 
 * @author Jakob Jarosch
 */
public class ActivityApp extends Activity {
    
    /**
     * The reference to the real App in the model.
     */
    private IApp app;
    
    /**
     * Activity manager is used to setup the {@link TabHost}.
     */
    @SuppressWarnings("deprecation")
    private LocalActivityManager lam;
    
    /**
     * {@link TabHost} for the displayed tabs in the GUI.
     */
    private TabHost mTabHost;
    
    /**
     * Tab tags
     */
    private static final String TAB_DETAIL = "tab_detail";
    private static final String TAB_SF = "tab_sf";
    private static final String TAB_PRESET = "tab_preset";
    
    /**
     * The {@link ActivityKillReceiver}.
     */
    private ActivityKillReceiver akr;
    
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_app);
        
        String activeTab = checkExtendedIntentActions();
        
        this.lam = new LocalActivityManager(this, true);
        this.lam.dispatchCreate(savedInstanceState);
        
        this.mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        this.mTabHost.setup(this.lam);
        
        setupTabs();
        
        BasicTitleView title = (BasicTitleView) findViewById(R.id.activity_title);
        
        title.setTitle(this.app.getName());
        title.setIcon(this.app.getIcon());
        
        this.mTabHost.setCurrentTabByTag(activeTab);
        
        /* Initiating the ActivityKillReceiver. */
        this.akr = new ActivityKillReceiver(this);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        
        this.lam.dispatchResume();
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
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
    
    
    private void setupTabs() {
        /* Details Tab */
        TabSpec details = this.mTabHost.newTabSpec(TAB_DETAIL);
        details.setIndicator(getResources().getString(R.string.details));
        
        // Create an Intent to start the inner activity
        Intent intentDetails = new Intent(this, TabDetails.class);
        intentDetails.putExtra(GUIConstants.APP_IDENTIFIER, this.app.getIdentifier());
        
        details.setContent(intentDetails);
        this.mTabHost.addTab(details);
        
        // Change the preferred size of the Tab-header
        View tab1 = this.mTabHost.getTabWidget().getChildAt(0);
        LayoutParams lp = tab1.getLayoutParams();
        lp.width = LayoutParams.WRAP_CONTENT;
        tab1.setLayoutParams(lp);
        
        /* Service Features Tab */
        TabSpec sfs = this.mTabHost.newTabSpec(TAB_SF);
        sfs.setIndicator(getResources().getString(R.string.service_features));
        
        // Create an Intent to start the inner activity
        Intent intentSfs = new Intent(this, TabServiceFeatures.class);
        intentSfs.putExtra(GUIConstants.APP_IDENTIFIER, this.app.getIdentifier());
        intentSfs.putExtra(GUIConstants.ACTIVITY_ACTION, getIntent().getStringExtra(GUIConstants.ACTIVITY_ACTION));
        intentSfs.putExtra(GUIConstants.REQUIRED_SERVICE_FEATURE,
                getIntent().getStringArrayExtra(GUIConstants.REQUIRED_SERVICE_FEATURE));
                
        sfs.setContent(intentSfs);
        this.mTabHost.addTab(sfs);
        
        // Change the preferred size of the Tab-header
        View tab2 = this.mTabHost.getTabWidget().getChildAt(1);
        lp = tab2.getLayoutParams();
        lp.width = LayoutParams.WRAP_CONTENT;
        tab2.setLayoutParams(lp);
        
        /* Presets Tab */
        TabSpec presets = this.mTabHost.newTabSpec(TAB_PRESET);
        presets.setIndicator(getResources().getString(R.string.presets));
        
        // Create an Intent to start the inner activity
        Intent intentPresets = new Intent(this, TabPresets.class);
        intentPresets.putExtra(GUIConstants.APP_IDENTIFIER, this.app.getIdentifier());
        
        presets.setContent(intentPresets);
        this.mTabHost.addTab(presets);
        
        // Change the preferred size of the Tab-header
        View tab3 = this.mTabHost.getTabWidget().getChildAt(2);
        lp = tab3.getLayoutParams();
        lp.width = LayoutParams.WRAP_CONTENT;
        tab3.setLayoutParams(lp);
    }
    
    
    /**
     * Checks if the Activity has been started with extended parameters like requested service features.
     */
    private String checkExtendedIntentActions() {
        this.app = GUITools.getIAppFromIntent(getIntent());
        if (GUITools.getIntentAction(getIntent()) != null
                && GUITools.getIntentAction(getIntent()).equals(GUIConstants.CHANGE_SERVICEFEATURE)) {
            return TAB_SF;
        }
        
        return TAB_DETAIL;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_app_open:
                // Open the Apps Main Activity
                String appPackageName = ActivityApp.this.app.getIdentifier();
                Intent intent = getPackageManager().getLaunchIntentForPackage(appPackageName);
                if (intent != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(ActivityApp.this, getString(R.string.app_not_opened), Toast.LENGTH_LONG).show();
                }
                break;
                
            case R.id.menu_app_unregister:
                ModelProxy.get().unregisterApp(ActivityApp.this.app.getIdentifier());
                Toast.makeText(ActivityApp.this, getString(R.string.app_successfully_unregistered), Toast.LENGTH_LONG)
                        .show();
                ActivityApp.this.finish();
                break;
                
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
