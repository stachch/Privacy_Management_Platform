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
package de.unistuttgart.ipvs.pmp.apps.calendarapp.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Represents one date object that is stored
 * 
 * @author Thorsten Berberich
 * 
 */
public class Appointment implements Serializable {
    
    /**
     * Generated serial
     */
    private static final long serialVersionUID = -154070252634681032L;
    
    /**
     * The description of the date
     */
    private String descrpition;
    
    /**
     * Date as string representation
     */
    private Date date;
    
    /**
     * Unique id to identify the date
     */
    private Integer id;
    
    /**
     * Name of this appointment
     */
    private String name;
    
    /**
     * Severity of the appointment
     */
    private Severity severity;
    
    
    /**
     * Constructor to create a new date object
     * 
     * @param descrpition
     *            Description of the date
     * @param date
     *            date as String
     */
    public Appointment(int id, String name, String descrpition, Date date, Severity severity) {
        this.name = name;
        this.descrpition = descrpition;
        this.date = (Date) date.clone();
        this.id = id;
        this.severity = severity;
    }
    
    
    /**
     * Getter for the description
     * 
     * @return the description of the date
     */
    public String getDescrpition() {
        return this.descrpition;
    }
    
    
    /**
     * Sets the description
     * 
     * @param description
     *            to set
     */
    public void setDescription(String description) {
        this.descrpition = description;
    }
    
    
    /**
     * Returns the date
     * 
     * @return the date as string
     */
    public Date getDate() {
        return (Date) this.date.clone();
    }
    
    
    /**
     * Sets the given date
     * 
     * @param date
     *            to set
     */
    public void setDate(Date date) {
        this.date = (Date) date.clone();
    }
    
    
    /**
     * Returns the id of the date
     * 
     * @return the id
     */
    public Integer getId() {
        return this.id;
    }
    
    
    /**
     * String representation of the stored date
     * 
     * @return string representation
     */
    public String getDateString() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(this.date);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        return dateFormat.format(cal.getTime());
    }
    
    
    /**
     * Returns the name of this appointment
     * 
     * @return
     */
    public String getName() {
        return this.name;
    }
    
    
    /**
     * Sets the name of this appointment
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
    /**
     * Returns the {@link Severity} of the appointment
     * 
     * @return {@link Severity}
     */
    public Severity getSeverity() {
        return this.severity;
    }
    
    
    /**
     * Sets the {@link Severity} of the appointment
     * 
     * @param severity
     *            to set
     */
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
}
