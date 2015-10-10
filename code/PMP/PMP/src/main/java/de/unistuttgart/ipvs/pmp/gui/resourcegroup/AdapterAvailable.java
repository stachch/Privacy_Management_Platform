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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.jpmpps.model.LocalizedResourceGroup;
import de.unistuttgart.ipvs.pmp.xmlutil.revision.RevisionReader;

/**
 * The {@link RgAvailableAdapter} is the list of available Resourcegroups in the {@link TabAvailable}.
 * 
 * @author Jakob Jarosch
 */
public class AdapterAvailable extends BaseAdapter {
    
    /**
     * {@link Context} which is used to create the Views of each Resource Groups.
     */
    private Context context;
    
    /**
     * List of all Resource Groups which should be displayed.
     */
    private List<LocalizedResourceGroup> rgs;
    
    
    public AdapterAvailable(Context context, List<LocalizedResourceGroup> rgs) {
        this.context = context;
        this.rgs = rgs;
    }
    
    
    @Override
    public int getCount() {
        return this.rgs.size();
    }
    
    
    @Override
    public Object getItem(int position) {
        return this.rgs.get(position);
    }
    
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LocalizedResourceGroup rgis = this.rgs.get(position);
        
        String rgId = rgis.getIdentifier();
        
        long rgRev = rgis.getRevision();
        String rgRevHR = RevisionReader.get().toHumanReadable(rgRev);
        
        /* load the layout from the xml file */
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout entryView = (LinearLayout) inflater.inflate(R.layout.listitem_resourcegroups_available, null);
        /* Set name, description and state of the requested Resource Group */
        TextView name = (TextView) entryView.findViewById(R.id.TextView_Name);
        String nameString = rgis.getName();
        name.setText(nameString);
        
        TextView description = (TextView) entryView.findViewById(R.id.TextView_Description);
        String descriptionString = rgis.getDescription();
        description.setText(descriptionString);
        
        TextView state = (TextView) entryView.findViewById(R.id.TextView_Status);
        if (ModelProxy.get().getResourceGroup(rgId) != null) {
            /* RG is already installed. */
            if (ModelProxy.get().getResourceGroup(rgId).getRevision() < rgRev) {
                /* A newer version is available */
                state.setText(this.context.getResources().getString(R.string.update) + " - rev. " + rgRevHR);
                state.setBackgroundColor(GUIConstants.COLOR_BG_RED);
            } else {
                /* already up to date */
                state.setText(this.context.getResources().getString(R.string.installed));
                state.setBackgroundColor(GUIConstants.COLOR_BG_GREEN);
            }
        } else {
            /* RG is not installed. */
            state.setText(this.context.getResources().getString(R.string.new_string) + " - rev. " + rgRevHR);
            state.setBackgroundColor(GUIConstants.COLOR_BG_GRAY);
        }
        
        return entryView;
    }
}
