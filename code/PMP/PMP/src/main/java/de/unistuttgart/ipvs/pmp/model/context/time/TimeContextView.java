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
package de.unistuttgart.ipvs.pmp.model.context.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.model.context.IContextView;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;

/**
 * View component for the {@link TimeContext}.
 * 
 * @author Tobias Kuhn
 *         
 */
public class TimeContextView extends LinearLayout implements IContextView {
    
    private static TimeZone UTC = TimeZone.getTimeZone("GMT");
    
    /**
     * Value currently in the view
     */
    private TimeContextCondition value;
    
    /**
     * {@link TimePicker}s
     */
    protected TimePicker beginPicker, endPicker;
    
    /**
     * {@link CheckBox} for whole day
     */
    private CheckBox dayBox;
    
    /**
     * {@link RadioButton}s for UTC vs. fixed with phone
     */
    private RadioButton timeWithPhone, timeWithLocation;
    
    /**
     * The {@link Spinner} for the interval.
     */
    private Spinner intervalSpinner;
    
    /**
     * The layouts for the interval selection
     */
    protected LinearLayout weeklyLayout, monthlyLayout, yearlyLayout;
    
    /**
     * The buttons for the layouts
     */
    private List<CheckBox> weeklyCB, monthlyCB;
    private DatePicker anniversary;
    
    
    public TimeContextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }
    
    
    public TimeContextView(Context context) {
        super(context);
        setup(context);
    }
    
    
    private void setup(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        
        this.value = getInitialValue();
        
        inflate(context, R.layout.contexts_time_view, this);
        
        // set fields
        this.beginPicker = (TimePicker) findViewById(R.id.beginPicker);
        this.beginPicker.setIs24HourView(Boolean.TRUE);
        this.endPicker = (TimePicker) findViewById(R.id.endPicker);
        this.endPicker.setIs24HourView(Boolean.TRUE);
        this.dayBox = (CheckBox) findViewById(R.id.dayBox);
        
        this.timeWithPhone = (RadioButton) findViewById(R.id.kindWithPhone);
        this.timeWithLocation = (RadioButton) findViewById(R.id.kindWithLocation);
        
        this.intervalSpinner = (Spinner) findViewById(R.id.interval);
        this.weeklyLayout = (LinearLayout) findViewById(R.id.weeklyLayout);
        this.monthlyLayout = (LinearLayout) findViewById(R.id.monthlyLayout);
        this.yearlyLayout = (LinearLayout) findViewById(R.id.yearlyLayout);
        
        this.weeklyCB = new ArrayList<CheckBox>();
        this.monthlyCB = new ArrayList<CheckBox>();
        
        this.weeklyCB.add((CheckBox) findViewById(R.id.cb_mon));
        this.weeklyCB.add((CheckBox) findViewById(R.id.cb_tue));
        this.weeklyCB.add((CheckBox) findViewById(R.id.cb_wed));
        this.weeklyCB.add((CheckBox) findViewById(R.id.cb_thu));
        this.weeklyCB.add((CheckBox) findViewById(R.id.cb_fri));
        this.weeklyCB.add((CheckBox) findViewById(R.id.cb_sat));
        this.weeklyCB.add((CheckBox) findViewById(R.id.cb_sun));
        
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_01));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_02));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_03));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_04));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_05));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_06));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_07));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_08));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_09));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_10));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_11));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_12));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_13));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_14));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_15));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_16));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_17));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_18));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_19));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_20));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_21));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_22));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_23));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_24));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_25));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_26));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_27));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_28));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_29));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_30));
        this.monthlyCB.add((CheckBox) findViewById(R.id.cb_31));
        
        this.anniversary = (DatePicker) findViewById(R.id.anniversary);
        
        addListeners();
    }
    
    
    private void addListeners() {
        // make "daily" grey out the "begin" and "end", set to 00:00 thru 23:59
        this.dayBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @SuppressWarnings("deprecation")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TimeContextView.this.beginPicker.setEnabled(!isChecked);
                TimeContextView.this.endPicker.setEnabled(!isChecked);
                
                if (isChecked) {
                    TimeContextView.this.beginPicker.setCurrentHour(0);
                    TimeContextView.this.beginPicker.setCurrentMinute(0);
                    //TimeContextView.this.beginPicker.setCurrentSecond(0);
                    
                    TimeContextView.this.endPicker.setCurrentHour(23);
                    TimeContextView.this.endPicker.setCurrentMinute(59);
                    //TimeContextView.this.endPicker.setCurrentSecond(59);
                }
            }
        });
        
        // show the correct layout for the interval
        this.intervalSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onNothingSelected(parent);
                switch (position) {
                    case 1:
                        TimeContextView.this.weeklyLayout.setVisibility(VISIBLE);
                        break;
                    case 2:
                        TimeContextView.this.monthlyLayout.setVisibility(VISIBLE);
                        break;
                    case 3:
                        TimeContextView.this.yearlyLayout.setVisibility(VISIBLE);
                        break;
                }
            }
            
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TimeContextView.this.weeklyLayout.setVisibility(GONE);
                TimeContextView.this.monthlyLayout.setVisibility(GONE);
                TimeContextView.this.yearlyLayout.setVisibility(GONE);
                
            }
        });
    }
    
    
    @Override
    public View asView() {
        return this;
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public String getViewCondition() {
        this.value.setUTC(this.timeWithLocation.isChecked());
        
        this.value.getBegin().setHour(this.beginPicker.getCurrentHour());
        this.value.getBegin().setMinute(this.beginPicker.getCurrentMinute());
        this.value.getBegin().setSecond(0);
        
        this.value.getEnd().setHour(this.endPicker.getCurrentHour());
        this.value.getEnd().setMinute(this.endPicker.getCurrentMinute());
        this.value.getEnd().setSecond(0);
        
        if (this.value.isUTC()) {
            // convert to UTC
            this.value.getBegin().convertTimeZone(TimeZone.getDefault(), UTC);
            this.value.getEnd().convertTimeZone(TimeZone.getDefault(), UTC);
        }
        
        this.value.getDays().clear();
        switch (this.intervalSpinner.getSelectedItemPosition()) {
            case 0:
                this.value.setInterval(TimeContextIntervalType.REPEAT_DAILY);
                break;
                
            case 1:
                this.value.setInterval(TimeContextIntervalType.REPEAT_WEEKLY);
                for (int i = 0; i < this.monthlyCB.size(); i++) {
                    if (this.monthlyCB.get(i).isChecked()) {
                        switch (i) {
                            case 0:
                                this.value.getDays().add(Calendar.MONDAY);
                                break;
                            case 1:
                                this.value.getDays().add(Calendar.TUESDAY);
                                break;
                            case 2:
                                this.value.getDays().add(Calendar.WEDNESDAY);
                                break;
                            case 3:
                                this.value.getDays().add(Calendar.THURSDAY);
                                break;
                            case 4:
                                this.value.getDays().add(Calendar.FRIDAY);
                                break;
                            case 5:
                                this.value.getDays().add(Calendar.SATURDAY);
                                break;
                            case 6:
                                this.value.getDays().add(Calendar.SUNDAY);
                                break;
                        }
                    }
                }
                break;
                
            case 2:
                this.value.setInterval(TimeContextIntervalType.REPEAT_MONTHLY);
                for (int i = 0; i < this.monthlyCB.size(); i++) {
                    if (this.monthlyCB.get(i).isChecked()) {
                        this.value.getDays().add(i + 1);
                    }
                }
                break;
                
            case 3:
                this.value.setInterval(TimeContextIntervalType.REPEAT_YEARLY);
                this.value.getDays().add(this.anniversary.getMonth());
                this.value.getDays().add(this.anniversary.getDayOfMonth());
                break;
        }
        
        return this.value.toString();
    }
    
    
    /**
     * Unchecks all the checkboxes in the list.
     * 
     * @param list
     */
    private void unsetAll(List<CheckBox> list) {
        for (CheckBox cb : list) {
            cb.setChecked(false);
        }
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public void setViewCondition(String condition) throws InvalidConditionException {
        this.value = TimeContextCondition.parse(condition);
        
        TimeContextTime begin = this.value.getBegin();
        TimeContextTime end = this.value.getEnd();
        if (this.value.isUTC()) {
            begin.convertTimeZone(UTC, TimeZone.getDefault());
            end.convertTimeZone(UTC, TimeZone.getDefault());
        }
        // set field values
        this.beginPicker.setCurrentHour(begin.getHour());
        this.beginPicker.setCurrentMinute(begin.getMinute());
        //this.beginPicker.setCurrentSecond(begin.getSecond());
        
        this.endPicker.setCurrentHour(end.getHour());
        this.endPicker.setCurrentMinute(end.getMinute());
        //this.endPicker.setCurrentSecond(end.getSecond());
        
        this.dayBox.setChecked(this.value.representsWholeDay());
        
        this.timeWithPhone.setChecked(!this.value.isUTC());
        this.timeWithLocation.setChecked(this.value.isUTC());
        
        switch (this.value.getInterval()) {
            case REPEAT_DAILY:
                this.intervalSpinner.setSelection(0);
                break;
            case REPEAT_WEEKLY:
                this.intervalSpinner.setSelection(1);
                
                unsetAll(this.weeklyCB);
                for (Integer day : this.value.getDays()) {
                    switch (day) {
                        case Calendar.MONDAY:
                            this.weeklyCB.get(0).setChecked(true);
                            break;
                            
                        case Calendar.TUESDAY:
                            this.weeklyCB.get(1).setChecked(true);
                            break;
                            
                        case Calendar.WEDNESDAY:
                            this.weeklyCB.get(2).setChecked(true);
                            break;
                            
                        case Calendar.THURSDAY:
                            this.weeklyCB.get(3).setChecked(true);
                            break;
                            
                        case Calendar.FRIDAY:
                            this.weeklyCB.get(4).setChecked(true);
                            break;
                            
                        case Calendar.SATURDAY:
                            this.weeklyCB.get(5).setChecked(true);
                            break;
                            
                        case Calendar.SUNDAY:
                            this.weeklyCB.get(6).setChecked(true);
                            break;
                            
                    }
                }
                break;
            case REPEAT_MONTHLY:
                this.intervalSpinner.setSelection(2);
                
                unsetAll(this.monthlyCB);
                for (Integer day : this.value.getDays()) {
                    this.monthlyCB.get(day).setChecked(true);
                }
                break;
            case REPEAT_YEARLY:
                this.intervalSpinner.setSelection(3);
                this.anniversary.updateDate(Calendar.getInstance().get(Calendar.YEAR), this.value.getDays().get(0),
                        this.value.getDays().get(1));
                break;
        }
    }
    
    
    private TimeContextCondition getInitialValue() {
        return new TimeContextCondition(false, new TimeContextTime(), new TimeContextTime(),
                TimeContextIntervalType.REPEAT_DAILY, new ArrayList<Integer>());
    }
    
    
    @Override
    public String getDefaultCondition() {
        return getInitialValue().toString();
    }
}
