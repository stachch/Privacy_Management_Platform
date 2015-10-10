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

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.jpmpps.model.LocalizedResourceGroup;
import de.unistuttgart.ipvs.pmp.model.server.IServerDownloadCallback;
import de.unistuttgart.ipvs.pmp.model.server.ServerProvider;

/**
 * The {@link TabAvailable} contains all available Resourcegroups.
 * 
 * @author Jakob Jarosch
 */
public class TabAvailable extends Activity {
    
    /**
     * ProgressBar displays the progress of updating the list of available Resourcegroups.
     */
    protected ProgressBar updateTaskProgressBar;
    
    /*
     * Linear Layouts to show or hide header informations.
     */
    private LinearLayout updateProgressContainer;
    private TextView updateInfoText;
    private LinearLayout lastUpdateContainer;
    
    /**
     * The lastUpdateTextView displays the date when the list was last updated.
     */
    private TextView lastUpdateTextView;
    
    /**
     * List of all registered Apps.
     */
    protected List<LocalizedResourceGroup> rgisList;
    
    /**
     * {@link ListView} is the view reference for the Resource Groups list.
     */
    private ListView rgisViewList;
    
    /**
     * {@link AdapterAvailable} for displaying the rgisList.
     */
    protected AdapterAvailable rgisAdapter;
    
    /**
     * Filter which should be used for filtering the available RGs.
     */
    protected String filter;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.tab_rgs_available);
        
        this.updateTaskProgressBar = (ProgressBar) findViewById(R.id.ProgressBar_TaskState);
        this.lastUpdateTextView = (TextView) findViewById(R.id.TextView_LastUpdate);
        this.updateInfoText = (TextView) findViewById(R.id.TextView_UpdateInfoText);
        this.updateProgressContainer = (LinearLayout) findViewById(R.id.LinearLayout_UpdatingList);
        this.lastUpdateContainer = (LinearLayout) findViewById(R.id.LinearLayout_Refresh);
        this.rgisViewList = (ListView) findViewById(R.id.ListView_RGs);
        
        this.rgisViewList.setClickable(true);
        
        this.filter = GUITools.getRgFilterFromIntent(getIntent());
        ((EditText) findViewById(R.id.EditText_Search)).setText(this.filter);
        
        addListener();
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        updateDownloadList();
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rg_menu, menu);
        return true;
    }
    
    
    /**
     * Initiates a new update of the available Resourcegroups list. (done asynchronously)
     */
    public void updateDownloadList() {
        
        this.lastUpdateContainer.setVisibility(View.GONE);
        this.updateInfoText.setVisibility(View.GONE);
        this.updateProgressContainer.setVisibility(View.VISIBLE);
        this.rgisViewList.setAdapter(null);
        
        new Thread() {
            
            @Override
            public void run() {
                ServerProvider.getInstance().setCallback(new IServerDownloadCallback() {
                    
                    @Override
                    public void step(int position, int length) {
                        TabAvailable.this.updateTaskProgressBar.setMax(length);
                        TabAvailable.this.updateTaskProgressBar.setProgress(position);
                    }
                    
                    
                    @Override
                    public void download(int position, int length) {
                    }
                });
                
                final LocalizedResourceGroup[] informationSets = ServerProvider.getInstance().findResourceGroups(
                        TabAvailable.this.filter);
                
                /* Parse the downloaded list */
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        parseDownloadedList(informationSets);
                    }
                });
            }
            
        }.start();
        
    }
    
    
    /**
     * Parses the downloadedList of informationsets.
     * 
     * @param informationSets
     *            Downloaded informationsets.
     */
    @SuppressWarnings("deprecation")
    protected void parseDownloadedList(LocalizedResourceGroup[] informationSets) {
        this.lastUpdateContainer.setVisibility(View.VISIBLE);
        this.updateProgressContainer.setVisibility(View.GONE);
        
        if (informationSets == null) {
            this.updateInfoText.setText(getString(R.string.rg_server_not_available));
            this.updateInfoText.setVisibility(View.VISIBLE);
        } else if (informationSets.length == 0) {
            this.updateInfoText.setText(getString(R.string.rg_no_matching_rgs_found));
            this.updateInfoText.setVisibility(View.VISIBLE);
        } else {
            this.updateInfoText.setVisibility(View.GONE);
            this.lastUpdateTextView.setText(getResources().getString(R.string.last_update_at) + ": "
                    + ServerProvider.getInstance().getFindResourceGroupsCacheDate(this.filter).toGMTString());
            
            this.rgisList = Arrays.asList(informationSets);
            
            this.rgisViewList.setAdapter(new AdapterAvailable(this, this.rgisList));
            
        }
    }
    
    
    /**
     * Adds the listener to the Activity layout.
     */
    private void addListener() {
        this.rgisViewList.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int item, long arg3) {
                new DialogAvailableDetails(TabAvailable.this, TabAvailable.this.rgisList.get(item)).show();
            }
        });
        
        ((EditText) findViewById(R.id.EditText_Search)).setOnKeyListener(new View.OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    TabAvailable.this.filter = ((EditText) findViewById(R.id.EditText_Search)).getText().toString();
                    updateDownloadList();
                    return true;
                }
                return false;
            }
        });
        ((ImageView) findViewById(R.id.ImageView_Search)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                TabAvailable.this.filter = ((EditText) findViewById(R.id.EditText_Search)).getText().toString();
                updateDownloadList();
            }
        });
    }
    
    
    private void toggleSearchView() {
        LinearLayout searchLayout = (LinearLayout) findViewById(R.id.LinearLayout_Search);
        if (searchLayout.getVisibility() == View.GONE) {
            searchLayout.setVisibility(View.VISIBLE);
        } else {
            searchLayout.setVisibility(View.GONE);
        }
    }
    
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_rg_refresh:
                updateDownloadList();
                break;
            
            case R.id.menu_rg_search:
                toggleSearchView();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
