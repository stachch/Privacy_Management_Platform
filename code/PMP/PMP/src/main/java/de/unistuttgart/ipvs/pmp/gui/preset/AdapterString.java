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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;

public class AdapterString extends BaseAdapter {
    
    private List<String> items;
    private Context context;
    private Map<String, Boolean> checkedMap = new HashMap<String, Boolean>();
    
    
    public AdapterString(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }
    
    
    @Override
    public int getCount() {
        return this.items.size();
    }
    
    
    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }
    
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.checkedMap.put(this.items.get(position), false);
        
        return new CheckableListItem(this.context, this, this.items.get(position));
    }
    
    
    public void setChecked(String item, boolean checked) {
        this.checkedMap.put(item, checked);
    }
    
    
    public boolean isChecked(int position) {
        return this.checkedMap.get(this.items.get(position));
    }
}

class CheckableListItem extends LinearLayout {
    
    private AdapterString adapter;
    private String item;
    
    
    @SuppressWarnings("deprecation")
    public CheckableListItem(Context context, AdapterString adapter, String item) {
        super(context);
        
        this.adapter = adapter;
        this.item = item;
        
        setLayoutParams(new AbsListView.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        
        LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View entryView = infalInflater.inflate(R.layout.listitem_checkable_string, null);
        entryView.setLayoutParams(new AbsListView.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(entryView);
        
        refresh();
        addListener();
    }
    
    
    private void refresh() {
        ((TextView) findViewById(R.id.TextView_Name)).setText(this.item);
    }
    
    
    private void addListener() {
        setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ((CheckBox) findViewById(R.id.CheckBox_State))
                        .setChecked(!((CheckBox) findViewById(R.id.CheckBox_State)).isChecked());
            }
        });
        
        ((CheckBox) findViewById(R.id.CheckBox_State))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        CheckableListItem.this.adapter.setChecked(CheckableListItem.this.item, buttonView.isChecked());
                    }
                });
    }
}
