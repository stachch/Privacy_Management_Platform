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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;

/**
 * The {@link AdapterPrivacySettingsAssign} is the list of Privacy Settings in the {@link DialogAppsAssign}.
 * 
 * @author Marcus Vetter
 */
public class AdapterPrivacySettingsAssign extends BaseExpandableListAdapter {
    
    /**
     * Context of the {@link DialogAppsAssign}
     */
    private Context context;
    
    /**
     * List of all ResourceGroups
     */
    private ArrayList<IResourceGroup> rgList;
    
    /**
     * List of Lists of all Privacy Settings (all ResourceGroups with their Privacy Settings)
     */
    private ArrayList<ArrayList<IPrivacySetting>> psList;
    
    
    /**
     * Constructor to setup parameter
     * 
     * @param context
     *            context of the {@link DialogAppsAssign}
     * @param preset
     *            the Preset
     * @param rgList
     *            List of all ResourceGroups
     * @param psList
     *            List of Lists of all Privacy Settings (all ResourceGroups with their Privacy Settings)
     */
    public AdapterPrivacySettingsAssign(Context context, IPreset preset, ArrayList<IResourceGroup> rgList,
            ArrayList<ArrayList<IPrivacySetting>> psList) {
        this.context = context;
        this.rgList = rgList;
        this.psList = psList;
    }
    
    
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.psList.get(groupPosition).get(childPosition);
    }
    
    
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    
    
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
            ViewGroup parent) {
        
        // Get the Privacy Setting
        IPrivacySetting ps = (IPrivacySetting) getChild(groupPosition, childPosition);
        
        // Inflate the layout
        LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View entryView = infalInflater.inflate(R.layout.listitem_preset_assign_ps, null);
        
        // Set name and description of one Privacy Setting
        TextView name = (TextView) entryView.findViewById(R.id.TextView_Name_PS);
        name.setText(ps.getName());
        
        TextView descr = (TextView) entryView.findViewById(R.id.TextView_Description);
        descr.setText(ps.getDescription());
        
        return entryView;
    }
    
    
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.psList.get(groupPosition).size();
    }
    
    
    @Override
    public Object getGroup(int groupPosition) {
        return this.rgList.get(groupPosition);
    }
    
    
    @Override
    public int getGroupCount() {
        return this.rgList.size();
    }
    
    
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    
    
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        
        // Get the ResourceGroup
        IResourceGroup rg = (IResourceGroup) getGroup(groupPosition);
        
        // Inflate the layout
        LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View entryView = infalInflater.inflate(R.layout.listitem_preset_rg, null);
        
        // Set name and icon of the ResourceGrouop
        TextView name = (TextView) entryView.findViewById(R.id.TextView_RG_Name);
        name.setText(rg.getName());
        
        ImageView icon = (ImageView) entryView.findViewById(R.id.ImageView_RG_Icon);
        icon.setImageDrawable(rg.getIcon());
        
        return entryView;
    }
    
    
    @Override
    public boolean hasStableIds() {
        return true;
    }
    
    
    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }
    
    
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }
    
}
