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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.model.Model;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;

/**
 * 
 * @author Marcus Vetter
 * 
 */
public class ActivityConflicts extends Activity {
    
    private IPreset p1;
    private IPreset p2;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_conflicts);
        
        String p1Id = getIntent().getStringExtra("p1");
        String p2Id = getIntent().getStringExtra("p2");
        
        this.p1 = Model.getInstance().getPreset(null, p1Id);
        this.p2 = Model.getInstance().getPreset(null, p2Id);
        
        if (this.p1 != null && this.p2 != null) {
            init();
        }
    }
    
    
    private void init() {
        // Set the title
        TextView title = (TextView) findViewById(R.id.tv_conflicts_between);
        title.setText(this.p1.getName() + " and " + this.p2.getName());
        
        // Instantiate the list
        List<String> conflictList = new ArrayList<String>();
        
        // Create the CACA conflict description
        for (IContextAnnotation ca : this.p1.getCACAConflicts(this.p2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Conflict between Context Annotations: \n");
            sb.append("Resourcegroup: " + ca.getPrivacySetting().getResourceGroup().getName() + "\n");
            sb.append("Privacy Setting: " + ca.getPrivacySetting().getName() + "\n");
            try {
                sb.append("Context condition: " + ca.getHumanReadableContextCondition() + "\n");
            } catch (InvalidConditionException e) {
                e.printStackTrace();
            }
            conflictList.add(sb.toString());
        }
        
        // Create the CAPS conflict description
        for (IPrivacySetting ps : this.p1.getCAPSConflicts(this.p2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Conflict between a Context Annotation and a Privacy Setting: \n");
            sb.append("Resourcegroup: " + ps.getResourceGroup().getName() + "\n");
            sb.append("Privacy Setting: " + ps.getName() + "\n");
            conflictList.add(sb.toString());
        }
        
        // Create the CAPS conflict description
        for (IPrivacySetting ps : this.p1.getPSPSConflicts(this.p2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Conflict between Privacy Settings: \n");
            sb.append("Resourcegroup: " + ps.getResourceGroup().getName() + "\n");
            sb.append("Privacy Setting: " + ps.getName() + "\n");
            conflictList.add(sb.toString());
        }
        
        ConflictDetailAdapter adapter = new ConflictDetailAdapter(conflictList, this);
        
        ListView conflictListView = (ListView) findViewById(R.id.lv_preset_conflict_details_list);
        conflictListView.setAdapter(adapter);
        
        conflictListView.setClickable(false);
        conflictListView.setLongClickable(false);
    }
}
