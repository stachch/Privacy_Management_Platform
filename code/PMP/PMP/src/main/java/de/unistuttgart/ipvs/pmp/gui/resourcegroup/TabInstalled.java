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

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;

/**
 * The {@link TabInstalled} contains all installed Resourcegroups.
 * 
 * @author Jakob Jarosch
 */
public class TabInstalled extends Activity {
    
    /**
     * List of all installed Resourcegroups.
     */
    protected List<IResourceGroup> resourceGroups;
    
    /**
     * ListView of all installed Resourcegroups.
     */
    private ListView installedRgsListView;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.tab_rgs_installed);
        
        this.installedRgsListView = (ListView) findViewById(R.id.ListView_RGs);
        
        addListener();
        
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        refreshList();
    }
    
    
    /**
     * Refreshs the list of installed Resourcegroups.
     */
    public void refreshList() {
        this.resourceGroups = ModelProxy.get().getResourceGroups();
        AdapterInstalled rgsAdapter = new AdapterInstalled(this, this.resourceGroups);
        
        this.installedRgsListView.setAdapter(rgsAdapter);
        
        /* Determine if the no-apps available hint should be shown. */
        TextView tv = (TextView) findViewById(R.id.TextView_NoRGs);
        if (this.resourceGroups.size() > 0) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
        }
    }
    
    
    /**
     * Adds the listener to the Activity layout.
     */
    private void addListener() {
        this.installedRgsListView.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int item, long arg3) {
                new DialogInstalledDetails(TabInstalled.this, TabInstalled.this, TabInstalled.this.resourceGroups
                        .get(item)).show();
            }
        });
    }
}
