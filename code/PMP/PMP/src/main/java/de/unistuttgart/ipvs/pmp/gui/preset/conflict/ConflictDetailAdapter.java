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

/**
 * Adapter which holds all description of conflicts between two Presets.
 * 
 * @author Marcus Vetter
 */
public class ConflictDetailAdapter extends BaseAdapter {
    
    private List<String> conflictDescriptions;
    private Context context;
    
    
    public ConflictDetailAdapter(List<String> conflictDescriptions, Context context) {
        this.conflictDescriptions = conflictDescriptions;
        this.context = context;
    }
    
    
    @Override
    public int getCount() {
        return this.conflictDescriptions.size();
    }
    
    
    @Override
    public Object getItem(int arg0) {
        return this.conflictDescriptions.get(arg0);
    }
    
    
    @Override
    public long getItemId(int arg0) {
        return arg0;
    }
    
    
    @Override
    public View getView(int position, View arg1, ViewGroup arg2) {
        String conflictDescr = this.conflictDescriptions.get(position);
        
        /* load the layout from the xml file */
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout entryView = (LinearLayout) inflater.inflate(R.layout.listitem_preset_conflict_detail, null);
        
        /* Set the description of the conflict */
        TextView tv = (TextView) entryView.findViewById(R.id.tv_conflict_description);
        tv.setText(conflictDescr);
        
        return entryView;
    }
    
}
