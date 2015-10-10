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
package de.unistuttgart.ipvs.pmp.gui.servicefeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import de.unistuttgart.ipvs.pmp.gui.app.ActivityApps;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;

/**
 * The {@link AdapterServiceFeatures} is the list of Apps in the {@link ActivityApps}.
 * 
 * @author Jakob Jarosch
 */
public class AdapterServiceFeatures extends BaseAdapter {
    
    /**
     * {@link Context} which is used to create the Views of each App.
     */
    private Activity activity;
    
    /**
     * List of all Service Features which should be displayed.
     */
    private List<IServiceFeature> serviceFeatures;
    
    private Map<IServiceFeature, ListItemServiceFeature> serviceFeatureViews = new HashMap<IServiceFeature, ListItemServiceFeature>();
    
    
    public AdapterServiceFeatures(Activity activity, List<IServiceFeature> serviceFeatures) {
        this.activity = activity;
        this.serviceFeatures = serviceFeatures;
    }
    
    
    @Override
    public int getCount() {
        return this.serviceFeatures.size();
    }
    
    
    @Override
    public Object getItem(int position) {
        return this.serviceFeatures.get(position);
    }
    
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IServiceFeature serviceFeature = this.serviceFeatures.get(position);
        
        ListItemServiceFeature entryView;
        entryView = new ListItemServiceFeature(this.activity, serviceFeature, this);
        this.serviceFeatureViews.put(serviceFeature, entryView);
        
        return entryView;
    }
    
    
    /**
     * Refreshes all listed views.
     */
    public void refreshAllViews() {
        for (Entry<IServiceFeature, ListItemServiceFeature> view : this.serviceFeatureViews.entrySet()) {
            view.getValue().refresh(false);
        }
    }
}
