/*
 * Copyright 2012 pmp-android development team
 * Project: CalendarApp
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
package de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.adapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.R;

/**
 * Creates the view for the header of a section
 * 
 * @author Thorsten Berberich
 * 
 */
public class HeaderAdapter extends BaseAdapter {
    
    /**
     * Context of the app
     */
    private Context context;
    
    /**
     * Stores the {@link Date#getTime()} for every header of a section
     */
    ArrayList<Long> headers = new ArrayList<Long>();
    
    
    /**
     * Creates a new header
     * 
     * @param context
     *            context of the app
     */
    public HeaderAdapter(Context context) {
        this.context = context;
    }
    
    
    @Override
    public int getCount() {
        return this.headers.size();
    }
    
    
    @Override
    public Object getItem(int position) {
        return this.headers.get(position);
    }
    
    
    @Override
    public long getItemId(int arg0) {
        return 0;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_header, null);
        }
        
        Long dateTime = this.headers.get(position);
        
        if (dateTime != null) {
            TextView header = (TextView) view.findViewById(R.id.list_header_title);
            Date date = new Date(dateTime);
            header.setText(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(date));
        }
        return view;
    }
    
    
    /**
     * Adds a section
     * 
     * @param section
     *            {@link Date#getTime()}
     */
    public void add(Long section) {
        if (!this.headers.contains(section)) {
            this.headers.add(section);
        }
    }
    
    
    /**
     * Sorts the headers
     */
    public void sort() {
        Collections.sort(this.headers);
    }
    
    
    /**
     * Removes the given header
     * 
     * @param key
     *            {@link Date#getTime()} of the header
     *            key of the header
     */
    public void remove(Long key) {
        this.headers.remove(key);
    }
    
    
    /**
     * Clears everything
     */
    public void clear() {
        this.headers.clear();
    }
    
}
