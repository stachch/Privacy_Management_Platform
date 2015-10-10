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
package de.unistuttgart.ipvs.pmp.gui.app;

import java.util.Collections;
import java.util.Comparator;
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
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;

/**
 * The {@link AdapterApps} is the list of Apps in the {@link ActivityApps}.
 * 
 * @author Jakob Jarosch
 */
public class AdapterApps extends BaseAdapter {
    
    /**
     * {@link Context} which is used to create the Views of each App.
     */
    private Context context;
    
    /**
     * List of all apps which should be displayed.
     */
    private List<IApp> apps;
    
    
    public AdapterApps(Context context, List<IApp> apps) {
        this.context = context;
        this.apps = apps;
        
        /* Sort the Apps alphabetically */
        Collections.sort(this.apps, new Comparator<IApp>() {
            
            @Override
            public int compare(IApp lhs, IApp rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
            
        });
    }
    
    
    @Override
    public int getCount() {
        return this.apps.size();
    }
    
    
    @Override
    public Object getItem(int position) {
        return this.apps.get(position);
    }
    
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IApp app = this.apps.get(position);
        
        /* load the layout from the xml file */
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout entryView = (LinearLayout) inflater.inflate(R.layout.listitem_apps, null);
        
        /* Set icon, name, description of the requested App */
        ImageView icon = (ImageView) entryView.findViewById(R.id.ImageView_Icon);
        icon.setImageDrawable(app.getIcon());
        
        TextView name = (TextView) entryView.findViewById(R.id.TextView_Name);
        name.setText(app.getName());
        
        TextView description = (TextView) entryView.findViewById(R.id.TextView_Description);
        description.setText(app.getDescription());
        
        return entryView;
    }
}
