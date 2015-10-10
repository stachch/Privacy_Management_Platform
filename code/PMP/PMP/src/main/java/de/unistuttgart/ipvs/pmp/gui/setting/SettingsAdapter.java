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
package de.unistuttgart.ipvs.pmp.gui.setting;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * @author Marcus Vetter
 * 
 */
public class SettingsAdapter extends BaseAdapter {
    
    /**
     * The context
     */
    private Context context;
    
    /**
     * List of all Settings
     */
    private List<SettingAbstract<?>> settings;
    
    
    /**
     * Constructor
     * 
     * @param context
     *            context
     * @param settings
     *            list of Settings
     */
    public SettingsAdapter(Context context, List<SettingAbstract<?>> settings) {
        this.context = context;
        this.settings = settings;
    }
    
    
    @Override
    public int getCount() {
        return this.settings.size();
    }
    
    
    @Override
    public Object getItem(int position) {
        return this.settings.get(position);
    }
    
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    
    @Override
    public boolean hasStableIds() {
        return true;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View entryView = new SettingListItem(this.context, this.settings.get(position));
        return entryView;
    }
    
}
