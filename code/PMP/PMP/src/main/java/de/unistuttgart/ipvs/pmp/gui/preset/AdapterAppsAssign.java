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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;

/**
 * The {@link AdapterAppsAssign} is the list of Apps, which can be assigned in the {@link DialogAppsAssign}.
 * 
 * @author Marcus Vetter
 */
public class AdapterAppsAssign extends BaseAdapter {
    
    private Context context;
    private List<IApp> apps;
    
    private Map<IApp, Boolean> checkBoxMap = new HashMap<IApp, Boolean>();
    
    
    public AdapterAppsAssign(Context context, List<IApp> apps) {
        this.context = context;
        this.apps = apps;
        
        // Initialize the checkBoxMap
        for (int pos = 0; pos < getCount(); pos++) {
            this.checkBoxMap.put((IApp) getItem(pos), false);
        }
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
        ViewAppsAssign entryView = new ViewAppsAssign(this.context, app, this);
        
        return entryView;
    }
    
    
    public Map<IApp, Boolean> getCheckBoxMap() {
        return this.checkBoxMap;
    }
    
}
