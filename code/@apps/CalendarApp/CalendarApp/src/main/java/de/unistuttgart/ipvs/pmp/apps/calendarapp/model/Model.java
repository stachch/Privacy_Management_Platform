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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import android.content.Intent;
import android.os.Handler;
import android.widget.ArrayAdapter;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.R;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.activities.CalendarAppActivity;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.activities.ImportActivity;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.adapter.AppointmentArrayAdapter;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.adapter.SeparatedListAdapter;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.sqlConnector.SqlConnector;
import de.unistuttgart.ipvs.pmp.resourcegroups.filesystem.resources.FileDetails;
import de.unistuttgart.ipvs.pmp.shared.Log;

public class Model {
    
    /**
     * Instance of this class
     */
    private static volatile Model instance;
    
    /**
     * Stores for every existing day a list of {@link Appointment}s
     */
    private HashMap<Long, ArrayList<Appointment>> dayAppointments = new HashMap<Long, ArrayList<Appointment>>();
    
    /**
     * {@link HashMap} for storing the adapters of one day
     */
    private HashMap<Long, AppointmentArrayAdapter> adapters = new HashMap<Long, AppointmentArrayAdapter>();
    
    /**
     * Holds all files for importing
     */
    private List<FileDetails> fileList = new ArrayList<FileDetails>();
    
    /**
     * The context of the app
     */
    private CalendarAppActivity appContext;
    
    /**
     * Handler of the {@link ImportActivity}
     */
    private Handler importHandler;
    
    /**
     * Handler of the {@link CalendarAppActivity}
     */
    private Handler handler;
    
    /**
     * The context of the import activity
     */
    private ImportActivity importContext;
    
    /**
     * Array adapter of the list to refresh it
     */
    private SeparatedListAdapter arrayAdapter;
    
    /**
     * Array adapter of the import file list to refresh it
     */
    private ArrayAdapter<FileDetails> importArrayAdapter;
    
    /**
     * Highest id of {@link Appointment}s
     */
    private int highestId = 0;
    
    
    /**
     * Private constructor because of singleton
     */
    private Model() {
    }
    
    
    /**
     * Returns the stored instance of the class or creates a new one if there is none
     * 
     * @return instance of this class
     */
    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        
        return instance;
    }
    
    
    /**
     * Called from {@link SqlConnector#loadAppointments()}
     */
    public void loadAppointments(ArrayList<Appointment> appList) {
        
        this.dayAppointments.clear();
        this.adapters.clear();
        
        for (Appointment app : appList) {
            addAppointment(app);
        }
        
        if (this.arrayAdapter != null) {
            this.arrayAdapter.notifyDataSetChanged();
        }
        
        // Update the visibility of the "no appointments avaiable" textview
        getContext().updateNoAvaiableAppointmentsTextView();
    }
    
    
    /**
     * Sets the array adapter that the model can refresh the list when something is changed
     * 
     * @param adapter
     *            the ArrayAdapter of the list with dates
     */
    public void setArrayAdapter(SeparatedListAdapter adapter) {
        this.arrayAdapter = adapter;
    }
    
    
    /**
     * Sets the array adapter that the model can refresh the list when something is changed
     * 
     * @param adapter
     *            the ArrayAdapter of the list with dates
     */
    public SeparatedListAdapter getArrayAdapter() {
        return this.arrayAdapter;
    }
    
    
    /**
     * Sets the context of the app
     * 
     * @param context
     *            context of the app
     */
    public void setContext(CalendarAppActivity context) {
        this.appContext = context;
    }
    
    
    /**
     * Returns the context of the app
     * 
     * @return app context
     */
    public CalendarAppActivity getContext() {
        return this.appContext;
    }
    
    
    /**
     * Called from {@link SqlConnector#storeNewDate(String, String)}. Adds the appointment to the {@link Appointment}
     * list of the day or creates the list if it doesn't exist
     * 
     * @param Appointment
     *            appointment to store
     */
    public void addAppointment(final Appointment appointment) {
        new Thread() {
            
            @Override
            public void run() {
                Model.this.handler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        Long key = creatKey(appointment.getDate());
                        if (Model.this.dayAppointments.containsKey(key)) {
                            Model.this.dayAppointments.get(key).add(appointment);
                        } else {
                            ArrayList<Appointment> appointmentList = new ArrayList<Appointment>();
                            appointmentList.add(appointment);
                            Model.this.dayAppointments.put(key, appointmentList);
                            AppointmentArrayAdapter adapter = new AppointmentArrayAdapter(Model.this.appContext,
                                    R.layout.list_item, appointmentList);
                            Model.this.adapters.put(key, adapter);
                            Model.this.arrayAdapter.addSection(key, adapter);
                        }
                        
                        Model.this.arrayAdapter.notifyDataSetChanged();
                        
                        // Update the visibility of the "no appointments available" textview
                        getContext().updateNoAvaiableAppointmentsTextView();
                    }
                });
            }
        }.start();
        
    }
    
    
    /**
     * Changes the appointment. Called from {@link SqlConnector#changeAppointment(int, String, String)}
     * 
     * @param id
     *            unique id of the date
     * @param date
     *            {@link Date}
     * @param description
     *            Description of the date
     */
    public void changeAppointment(final int id, final Date date, final Date oldDate, final String name,
            final String description, final Severity severity) {
            
        new Thread() {
            
            @Override
            public void run() {
                Model.this.handler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        Long key = creatKey(oldDate);
                        
                        Appointment toDel = null;
                        if (Model.this.dayAppointments.containsKey(key)) {
                            ArrayList<Appointment> appList = Model.this.dayAppointments.get(key);
                            for (Appointment appointment : appList) {
                                
                                // The date remains the same
                                if (appointment.getId() == id && oldDate.equals(date)) {
                                    appointment.setDate(date);
                                    appointment.setName(name);
                                    appointment.setDescription(description);
                                    appointment.setSeverity(severity);
                                    Model.this.adapters.get(key).notifyDataSetChanged();
                                    break;
                                    
                                    // The date changes
                                } else if (appointment.getId() == id) {
                                    // Store appointment to delete later
                                    toDel = appointment;
                                    break;
                                }
                            }
                            
                            // Delete appointment if necessary if the date has changed
                            if (toDel != null) {
                                deleteAppointment(toDel);
                                
                                // Add new appointment
                                addAppointment(new Appointment(id, name, description, date, severity));
                            }
                            
                            Model.this.arrayAdapter.notifyDataSetChanged();
                        } else {
                            Log.e(this, "List of this day not found");
                        }
                    }
                });
            }
        }.start();
    }
    
    
    /**
     * Returns the whole list of appointments
     * 
     * @return all appointments at all days
     */
    public ArrayList<Appointment> getAppointmentList() {
        ArrayList<Appointment> appointmentList = new ArrayList<Appointment>();
        for (Entry<Long, ArrayList<Appointment>> entry : this.dayAppointments.entrySet()) {
            appointmentList.addAll(entry.getValue());
        }
        return appointmentList;
    }
    
    
    public Boolean isModelEmpty() {
        return this.dayAppointments.isEmpty();
    }
    
    
    /**
     * Clears the local stored list of dates but not the dates stored at the database
     */
    public void clearLocalList() {
        new Thread() {
            
            @Override
            public void run() {
                Model.this.handler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        Model.this.dayAppointments.clear();
                        Model.this.adapters.clear();
                        Model.this.arrayAdapter.reset();
                        
                        Model.this.arrayAdapter.notifyDataSetChanged();
                        Model.this.appContext.updateNoAvaiableAppointmentsTextView();
                    }
                });
            }
        }.start();
    }
    
    
    /**
     * Clears the local stored list of dates but not the dates stored at the database
     */
    public void clearLocalListWithoutTextViewUpdate() {
        new Thread() {
            
            @Override
            public void run() {
                Model.this.handler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        Model.this.dayAppointments.clear();
                        Model.this.adapters.clear();
                        Model.this.arrayAdapter.reset();
                        
                        Model.this.arrayAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();
    }
    
    
    /**
     * Get the file list for importing
     * 
     * @return file list for importing
     */
    public List<FileDetails> getFileList() {
        return this.fileList;
    }
    
    
    /**
     * Get a file for a given file name
     * 
     * @param fileName
     *            given file name
     * @return file for file name, null if file name does not exist
     */
    public FileDetails getFileForName(String fileName) {
        for (FileDetails file : this.fileList) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        // Update the visibility of the "no files avaiable" textview
        if (getImportContext() != null) {
            getImportContext().updateNoAvaiableFilesTextView();
        }
        return null;
    }
    
    
    /**
     * Remove a file from the list for importing
     * 
     * @param file
     *            to remove
     */
    public void removeFileFromList(final FileDetails file) {
        new Thread() {
            
            @Override
            public void run() {
                Model.this.importHandler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        Model.this.fileList.remove(file);
                        if (Model.this.importArrayAdapter != null) {
                            Model.this.importArrayAdapter.notifyDataSetChanged();
                        }
                        
                        // Update the visibility of the "no files available" textview
                        if (getImportContext() != null) {
                            getImportContext().updateNoAvaiableFilesTextView();
                        }
                    }
                });
            }
        }.start();
    }
    
    
    public void addImportHandler(Handler handler) {
        this.importHandler = handler;
    }
    
    
    /**
     * Add a file to the list for importing
     * 
     * @param file
     *            to add
     */
    public void addFileToList(final FileDetails file) {
        new Thread() {
            
            @Override
            public void run() {
                Model.this.importHandler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        Model.this.fileList.add(file);
                        if (Model.this.importArrayAdapter != null) {
                            Model.this.importArrayAdapter.notifyDataSetChanged();
                        }
                        
                        // Update the visibility of the "no files available" textview
                        if (getImportContext() != null) {
                            getImportContext().updateNoAvaiableFilesTextView();
                        }
                    }
                });
            }
        }.start();
    }
    
    
    /**
     * Adds the {@link FileDetails} to the list but without a handler
     * 
     * @param file
     *            to add
     */
    public void addFileToListExport(FileDetails file) {
        this.fileList.add(file);
        if (this.importArrayAdapter != null) {
            this.importArrayAdapter.notifyDataSetChanged();
        }
        
        // Update the visibility of the "no files available" textview
        if (getImportContext() != null) {
            getImportContext().updateNoAvaiableFilesTextView();
        }
    }
    
    
    /**
     * Clear the file list of the model
     */
    public void clearFileList() {
        this.fileList.clear();
        if (this.importArrayAdapter != null) {
            this.importArrayAdapter.notifyDataSetChanged();
        }
        
        // Update the visibility of the "no files avaiable" textview
        if (getImportContext() != null) {
            getImportContext().updateNoAvaiableFilesTextView();
        }
    }
    
    
    /**
     * Check, if a file name already exists
     * 
     * @param filenameToCheck
     *            filename to check
     * @return flag
     */
    public boolean isFileNameExisting(String filenameToCheck) {
        for (FileDetails file : this.fileList) {
            if (file.getName().toLowerCase(Locale.getDefault())
                    .equals(filenameToCheck.toLowerCase(Locale.getDefault()))) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Get the array adapter for importing files
     * 
     * @return array adapter
     */
    public ArrayAdapter<FileDetails> getImportArrayAdapter() {
        return this.importArrayAdapter;
    }
    
    
    /**
     * Set the array adapter for importing files
     * 
     * @param importArrayAdapter
     *            array adapter for importing files
     */
    public void setImportArrayAdapter(ArrayAdapter<FileDetails> importArrayAdapter) {
        this.importArrayAdapter = importArrayAdapter;
    }
    
    
    /**
     * Get the context of the import activity
     * 
     * @return context of the import activity
     */
    public ImportActivity getImportContext() {
        return this.importContext;
    }
    
    
    /**
     * Set the context of the import activity
     * 
     * @param importContext
     *            context of the import activity
     */
    public void setImportContext(ImportActivity importContext) {
        this.importContext = importContext;
    }
    
    
    /**
     * Creates a key for getting an {@link ArrayList} of {@link Appointment}s.
     * 
     * @return string representation
     */
    private Long creatKey(Date date) {
        return date.getTime();
    }
    
    
    /**
     * Deletes a appointment out of the day list
     * 
     * @param appointment
     *            appointment to delete
     */
    public void deleteAppointment(final Appointment appointment) {
        new Thread() {
            
            @Override
            public void run() {
                Model.this.handler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        ArrayList<Long> toDelete = new ArrayList<Long>();
                        
                        // Search the correct list of this day
                        for (Entry<Long, ArrayList<Appointment>> dayList : Model.this.dayAppointments.entrySet()) {
                            if (dayList.getValue().contains(appointment)) {
                                
                                // Delete the entry out of this day list
                                dayList.getValue().remove(appointment);
                                
                                /*
                                 * If this daylist is empty then remove the header and the adapter,
                                 * remember what to delete out of the list of days
                                 */
                                if (dayList.getValue().isEmpty()) {
                                    Model.this.arrayAdapter.removeEmptyHeadersAndSections();
                                    Model.this.adapters.remove(dayList.getKey());
                                    toDelete.add(dayList.getKey());
                                }
                            }
                        }
                        
                        // Delete the day lists out of the whole list
                        for (Long del : toDelete) {
                            Model.this.dayAppointments.remove(del);
                        }
                        Model.this.arrayAdapter.notifyDataSetChanged();
                        Model.this.appContext.updateNoAvaiableAppointmentsTextView();
                    }
                });
            }
        }.start();
    }
    
    
    /**
     * Delete all appointments from the database and model
     */
    public void deleteAllAppointments() {
        this.dayAppointments.clear();
        if (this.arrayAdapter != null) {
            this.arrayAdapter.reset();
            this.arrayAdapter.notifyDataSetChanged();
        }
        this.adapters.clear();
        if (this.appContext != null) {
            this.appContext.updateNoAvaiableAppointmentsTextView();
        }
        SqlConnector connector = new SqlConnector();
        connector.deleteAllApointments();
    }
    
    
    /**
     * Gets the current highest id
     * 
     * @return highestId
     */
    public int getHighestId() {
        return this.highestId;
    }
    
    
    /**
     * Sets the current highest id
     * 
     * @param highestId
     *            highest id to set
     */
    public void setHighestId(int highestId) {
        this.highestId = highestId;
    }
    
    
    /**
     * Gets a new highest id
     * 
     * @return highestId++
     */
    public int getNewHighestId() {
        this.highestId++;
        return this.highestId;
    }
    
    
    /**
     * Adds a handler to update the {@link ArrayAdapter}
     * 
     * @param handler
     *            handler of the {@link CalendarAppActivity}
     */
    public void addHandler(Handler handler) {
        this.handler = handler;
    }
    
    
    /**
     * Scrolls to the actual date
     */
    public void scrollToActualDate() {
        new Thread() {
            
            @Override
            public void run() {
                Model.this.handler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        Model.this.appContext.getListView()
                                .setSelection(Model.this.arrayAdapter.getActualAppointmentPosition());
                    }
                });
            }
        }.start();
    }
    
    
    public void invokeBroadcast() {
        Intent broadcast = new Intent();
        broadcast.setAction("PMP_APP.CALENDAR_MODIFIED");
        getContext().sendBroadcast(broadcast);
    }
}
