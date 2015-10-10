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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.model.conflicts.ConflictPair;

/**
 * Adapter which holds all conflicts between the Presets.
 * 
 * @author Jakob Jarosch
 */
public class ConflictAdapter extends BaseAdapter {
    
    private Context context;
    private List<ConflictPair> conflictList;
    
    
    public ConflictAdapter(Context context, List<ConflictPair> conflictList) {
        this.context = context;
        this.conflictList = conflictList;
    }
    
    
    @Override
    public int getCount() {
        return this.conflictList.size();
    }
    
    
    @Override
    public Object getItem(int arg0) {
        return this.conflictList.get(arg0);
    }
    
    
    @Override
    public long getItemId(int arg0) {
        return arg0;
    }
    
    
    @Override
    public View getView(int position, View arg1, ViewGroup arg2) {
        ConflictPair conflict = this.conflictList.get(position);
        
        /* load the layout from the xml file */
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout entryView = (LinearLayout) inflater.inflate(R.layout.listitem_preset_conflict, null);
        
        /* Set the names of the conflicting presets */
        TextView preset1 = (TextView) entryView.findViewById(R.id.TextView_Preset1);
        TextView preset2 = (TextView) entryView.findViewById(R.id.TextView_Preset2);
        preset1.setText(conflict.getPreset1().getName());
        preset2.setText(conflict.getPreset2().getName());
        
        return entryView;
    }
    
}
