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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;

/**
 * The {@link AdapterInstalled} is the list of all at PMP registered Resourcegroups in the {@link TabInstalled}.
 * 
 * @author Jakob Jarosch, Frieder Schüler
 */
public class AdapterInstalled extends BaseAdapter {
    
    private Context context;
    private List<IResourceGroup> resourceGroups;
    
    
    public AdapterInstalled(Context context, List<IResourceGroup> resourceGroups) {
        this.context = context;
        this.resourceGroups = resourceGroups;
    }
    
    
    @Override
    public int getCount() {
        return this.resourceGroups.size();
    }
    
    
    @Override
    public Object getItem(int position) {
        return this.resourceGroups.get(position);
    }
    
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IResourceGroup resourceGroup = this.resourceGroups.get(position);
        
        /* load the layout from the xml file */
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout entryView = (LinearLayout) inflater.inflate(R.layout.listitem_resourcegroups, null);
        
        /* Set icon, name, description of the requested ResourceGroup */
        ImageView icon = (ImageView) entryView.findViewById(R.id.ImageView_Icon);
        icon.setImageDrawable(resourceGroup.getIcon());
        
        TextView name = (TextView) entryView.findViewById(R.id.TextView_Name);
        name.setText(resourceGroup.getName());
        
        TextView description = (TextView) entryView.findViewById(R.id.TextView_Description);
        description.setText(resourceGroup.getDescription());
        
        return entryView;
    }
}
