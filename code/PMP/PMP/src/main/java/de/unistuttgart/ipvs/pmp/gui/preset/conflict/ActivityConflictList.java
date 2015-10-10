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
package de.unistuttgart.ipvs.pmp.gui.preset.conflict;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.ActivityKillReceiver;
import de.unistuttgart.ipvs.pmp.model.conflicts.ConflictModel;
import de.unistuttgart.ipvs.pmp.model.conflicts.ConflictPair;

/**
 * The {@link Activity} displays all conflicts between different Presets.
 * 
 * @author Jakob Jarosch
 */
public class ActivityConflictList extends Activity {
    
    /**
     * List of all Conflicts
     */
    private List<ConflictPair> conflictList;
    
    /**
     * Conflict adapter which holds the list of conflicts
     */
    private ConflictAdapter conflictAdapter;
    
    /**
     * GUI-component for displaying the conflict list.
     */
    private ListView conflictListView;
    
    /**
     * The {@link ActivityKillReceiver}.
     */
    private ActivityKillReceiver akr;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_conflicts_list);
        
        /* Initiating the ActivityKillReceiver. */
        this.akr = new ActivityKillReceiver(this);
        
        init();
        
        updateConflicts();
        
    }
    
    
    /**
     * Updates the list of conflicts (opens a dialog for processing).
     */
    private void updateConflicts() {
        new ScanningProgressDialog(this, new ScanningProgressDialog.ICallback() {
            
            @Override
            public void finished() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        }).start();
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        unregisterReceiver(this.akr);
    }
    
    
    /**
     * Refreshes the list of conflicts.
     */
    public void refresh() {
        this.conflictAdapter.notifyDataSetChanged();
        
        TextView noConflicts = (TextView) findViewById(R.id.TextView_NoConflicts);
        if (this.conflictList.size() == 0) {
            noConflicts.setVisibility(View.VISIBLE);
        } else {
            noConflicts.setVisibility(View.GONE);
        }
    }
    
    
    /**
     * Initializes the Activitys GUI-components.
     */
    private void init() {
        this.conflictList = ConflictModel.getInstance().getConflicts();
        this.conflictAdapter = new ConflictAdapter(this, this.conflictList);
        
        this.conflictListView = (ListView) findViewById(R.id.ListView_PresetConflicts);
        this.conflictListView.setAdapter(this.conflictAdapter);
        
        this.conflictListView.setClickable(true);
        this.conflictListView.setLongClickable(false);
        
        this.conflictListView.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
                openConflict(ActivityConflictList.this.conflictList.get(pos));
            }
            
        });
    }
    
    
    /**
     * Opens the {@link Activity} to display a conflict.
     * 
     * @param conflictPair
     *            Conflict which should be displayed.
     */
    private void openConflict(ConflictPair conflictPair) {
        Intent i = new Intent(this, ActivityConflicts.class);
        i.putExtra("p1", conflictPair.getPreset1().getLocalIdentifier());
        i.putExtra("p2", conflictPair.getPreset2().getLocalIdentifier());
        startActivity(i);
    }
}
