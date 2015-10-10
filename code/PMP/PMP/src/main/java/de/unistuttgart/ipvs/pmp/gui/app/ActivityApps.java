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

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
import de.unistuttgart.ipvs.pmp.gui.util.ActivityKillReceiver;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;

/**
 * The {@link ActivityApps} displays all at PMP registered Apps.
 * If the user may tab on one of the listed Apps he will get the {@link ActivityApp}.
 * 
 * @author Jakob Jarosch
 */
public class ActivityApps extends Activity {
    
    /**
     * List of all registered Apps.
     */
    private List<IApp> appsList;
    
    /**
     * {@link ListView} is the view reference for the Apps list.
     */
    private ListView appsViewList;
    
    /**
     * {@link AdapterApps} for displaying the appsList.
     */
    protected AdapterApps appsAdapter;
    
    /**
     * The {@link ActivityKillReceiver}.
     */
    private ActivityKillReceiver akr;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_apps);
        
        /* Initiating the ActivityKillReceiver. */
        this.akr = new ActivityKillReceiver(this);
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        this.appsViewList = (ListView) findViewById(R.id.ListView_Apps);
        this.appsViewList.setClickable(true);
        
        addListener();
        
        updateAppsList();
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        unregisterReceiver(this.akr);
    }
    
    
    /**
     * Is called when a long press on an App was done.
     */
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        // The menu information
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) menuItem.getMenuInfo();
        IApp app = this.appsList.get(menuInfo.position);
        
        if (menuItem.getItemId() == 0) {
            // open details
            Intent intent = GUITools.createAppActivityIntent(app);
            GUITools.startIntent(intent);
            
        } else if (menuItem.getItemId() == 1) {
            // Open the Apps Main Activity
            Intent intent = getPackageManager().getLaunchIntentForPackage(app.getIdentifier());
            startActivity(intent);
            
        } else if (menuItem.getItemId() == 2) {
            // remove app from model
            ModelProxy.get().unregisterApp(app.getIdentifier());
            
            // Show Toast
            Toast.makeText(ActivityApps.this, getString(R.string.app_successfully_unregistered), Toast.LENGTH_LONG)
                    .show();
            
            // update the app list (item just removed)
            updateAppsList();
            
            // inform the user
            Toast.makeText(this, getResources().getString(R.string.app_removed), Toast.LENGTH_LONG).show();
        }
        
        return true;
    }
    
    
    /**
     * Adds the required listeners to the view.
     */
    private void addListener() {
        this.appsViewList.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // recover App identifier
                IApp app = ((IApp) ActivityApps.this.appsAdapter.getItem(position));
                
                // create intent and start ActivityApp
                Intent intent = GUITools.createAppActivityIntent(app);
                GUITools.startIntent(intent);
            }
        });
        
        this.appsViewList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(getString(R.string.choose_your_action));
                menu.add(0, 0, 0, R.string.app_details);
                menu.add(1, 1, 0, R.string.open_app);
                menu.add(2, 2, 0, R.string.remove_app);
            }
        });
    }
    
    
    /**
     * Updates the AppsList, when a change occurred (like an App was installed or removed).
     */
    private void updateAppsList() {
        this.appsList = ModelProxy.get().getApps();
        this.appsAdapter = new AdapterApps(this, this.appsList);
        this.appsViewList.setAdapter(this.appsAdapter);
        
        /* Determine if the no-apps available hint should be shown. */
        TextView tv = (TextView) findViewById(R.id.TextView_NoApps);
        if (this.appsList.size() > 0) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
        }
    }
}
