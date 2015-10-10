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

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;

/**
 * The {@link AdapterPresets} is the list of presets in the {@link ActivityPresets}.
 * 
 * @author Marcus Vetter
 */
public class AdapterPresets extends BaseAdapter {
    
    private Context context;
    private List<IPreset> presets;
    
    
    public AdapterPresets(Context context, List<IPreset> presets) {
        this.context = context;
        this.presets = presets;
    }
    
    
    @Override
    public int getCount() {
        return this.presets.size();
    }
    
    
    @Override
    public Object getItem(int position) {
        return this.presets.get(position);
    }
    
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IPreset preset = this.presets.get(position);
        
        /* load the layout from the xml file */
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout entryView = (LinearLayout) inflater.inflate(R.layout.listitem_preset, null);
        
        /* Set name and description of the requested Preset */
        TextView name = (TextView) entryView.findViewById(R.id.TextView_Name);
        name.setText(preset.getName());
        
        TextView description = (TextView) entryView.findViewById(R.id.TextView_Description);
        description.setText(preset.getDescription());
        
        /* Set text color to dark gray, if item is deleted or unavailable */
        if (preset.isDeleted() || !preset.isAvailable()) {
            name.setTextColor(GUIConstants.COLOR_TEXT_GRAYED_OUT);
            description.setTextColor(GUIConstants.COLOR_TEXT_GRAYED_OUT);
        }
        
        return entryView;
    }
}
