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

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.R;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.model.Appointment;

/**
 * Implements the {@link ArrayAdapter} to show the custom entries
 * 
 * @author Thorsten Berberich
 * 
 */
public class AppointmentArrayAdapter extends ArrayAdapter<Appointment> {
    
    /**
     * Appointment list
     */
    private ArrayList<Appointment> items;
    
    /**
     * Context of the app
     */
    private Context context;
    
    
    /**
     * Constructor
     * 
     * @param context
     *            context of the app
     * @param textViewResourceId
     *            ID of the row xml element
     * @param items
     *            appointment items
     */
    public AppointmentArrayAdapter(Context context, int textViewResourceId, ArrayList<Appointment> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.items = items;
    }
    
    
    @Override
    public int getCount() {
        return this.items.size();
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }
        
        Appointment appointm = this.items.get(position);
        if (appointm != null) {
            TextView textTop = (TextView) view.findViewById(R.id.toptext);
            TextView textBottom = (TextView) view.findViewById(R.id.bottomtext);
            View severity = view.findViewById(R.id.severity);
            
            // Set the color of the severity
            switch (appointm.getSeverity()) {
                case HIGH:
                    severity.setBackgroundColor(Color.RED);
                    break;
                case MIDDLE:
                    severity.setBackgroundResource(R.color.orange);
                    break;
                case LOW:
                    severity.setBackgroundResource(R.color.green);
                    break;
            }
            
            if (textTop != null) {
                textTop.setText(appointm.getName());
                textTop.setTypeface(null, Typeface.BOLD);
            }
            if (textBottom != null) {
                textBottom.setText(appointm.getDescrpition());
            }
        }
        return view;
    }
}
