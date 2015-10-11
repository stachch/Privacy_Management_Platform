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
package de.unistuttgart.ipvs.pmp.apps.calendarapp.sqlConnector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.R;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.activities.CalendarAppActivity;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.activities.ImportActivity;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.util.UiManager;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.model.Appointment;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.model.Model;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.model.Severity;
import de.unistuttgart.ipvs.pmp.resourcegroups.database.IDatabaseConnection;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.api.PMP;
import de.unistuttgart.ipvs.pmp.shared.api.PMPResourceIdentifier;
import de.unistuttgart.ipvs.pmp.shared.api.handler.PMPRequestResourceHandler;

public class SqlConnector {
    
    /**
     * Identifier of the needed resource group
     */
    private static final String RG_IDENTIFIER = "de.unistuttgart.ipvs.pmp.resourcegroups.database";
    
    /**
     * Resource identifier
     */
    private static final String R_IDENTIFIER = "databaseResource";
    
    /**
     * {@link Context} of the {@link CalendarAppActivity}
     */
    private CalendarAppActivity appContext = Model.getInstance().getContext();
    
    /**
     * Identifier to get the resource
     */
    private static final PMPResourceIdentifier PMP_IDENTIFIER = PMPResourceIdentifier.make(RG_IDENTIFIER, R_IDENTIFIER);
    
    /*
     * Constants for the database table
     */
    private static final String DB_TABLE_NAME = "appointments";
    private static final String ID = "ID";
    private static final String NAME = "Name";
    private static final String DESC = "Description";
    private static final String DATE = "Date";
    private static final String SEVERITY = "Severity";
    
    
    /**
     * Loads the dates stored appointments in the SQL database. This method calls
     * {@link Model#loadAppointments(ArrayList)} to store the dates in the model.
     * 
     */
    public void loadAppointments() {
        PMP.get().getResource(PMP_IDENTIFIER, new PMPRequestResourceHandler() {
            
            @Override
            public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    
                    if (createTable(idc)) {
                        // Getting the number of the rows
                        long rowCount;
                        try {
                            rowCount = idc.query(SqlConnector.DB_TABLE_NAME, null, null, null, null, null,
                                    SqlConnector.DATE);
                            // Getting the rows 
                            for (int itr = 0; itr < rowCount; itr++) {
                                String[] columns = idc.getRowAt(itr);
                                
                                // Storing everything from this appointment
                                int id = Integer.valueOf(columns[0]);
                                String name = columns[1];
                                String desc = columns[2];
                                Severity severity = Severity.valueOf(columns[3]);
                                Date date = new Date(Long.valueOf(columns[4]));
                                
                                // Storing in the model
                                Model.getInstance().addAppointment(new Appointment(id, name, desc, date, severity));
                                Log.v(this,
                                        "Loading appointment: ID: " + String.valueOf(id) + " date: " + columns[2]
                                                + " name: " + name + " description: " + columns[1] + " severity "
                                                + severity.toString());
                                                
                                if (id > Model.getInstance().getHighestId()) {
                                    Model.getInstance().setHighestId(id);
                                }
                            }
                            Model.getInstance().scrollToActualDate();
                        } catch (RemoteException e) {
                            showToast(SqlConnector.this.appContext.getString(R.string.err_load));
                            Log.e(this, "Remote Exception", e);
                        } finally {
                            try {
                                idc.close();
                            } catch (RemoteException e) {
                                Log.e(this, "RemoteException", e);
                            }
                            
                            Model.getInstance().invokeBroadcast();
                        }
                    }
                }
            }
        });
    }
    
    
    /**
     * Stores the appointment in the database AND in the model
     * 
     * @param date
     *            date of the appointment
     * @param name
     *            name of the appointment
     * @param description
     *            description of the appointment
     * @param severity
     *            {@link Severity} of the appointment
     */
    public void storeNewAppointment(final Date date, final String name, final String description,
            final Severity severity) {
            
        if (description.equals("") && name.equals("")) {
            Toast.makeText(this.appContext, R.string.appointment_not_added, Toast.LENGTH_SHORT).show();
            return;
        }
        
        PMP.get().getResource(PMP_IDENTIFIER, new PMPRequestResourceHandler() {
            
            @Override
            public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    
                    if (createTable(idc)) {
                        try {
                            
                            // The values to add
                            Map<String, String> values = new HashMap<String, String>();
                            int id = Model.getInstance().getNewHighestId();
                            
                            values.put(SqlConnector.ID, String.valueOf(id));
                            values.put(SqlConnector.NAME, name);
                            values.put(SqlConnector.DESC, description);
                            values.put(SqlConnector.DATE, String.valueOf(date.getTime()));
                            values.put(SqlConnector.SEVERITY, severity.toString());
                            
                            long result = idc.insert(SqlConnector.DB_TABLE_NAME, null, values);
                            Log.v(this, "Return value of insert: " + result);
                            if (result != -1) {
                                //idc.query(SqlConnector.DB_TABLE_NAME, null, null, null, null, null, SqlConnector.DATE);
                                
                                Log.v(this, "Storing new appointment: id: " + String.valueOf(id) + " date: " + date
                                        + " description: " + description);
                                Model.getInstance()
                                        .addAppointment(new Appointment(id, name, description, date, severity));
                            } else {
                                showToast(SqlConnector.this.appContext.getString(R.string.err_store));
                                Log.e(this, "Appointment not stored");
                            }
                        } catch (RemoteException e) {
                            showToast(SqlConnector.this.appContext.getString(R.string.err_store));
                            Log.e(this, "Remote Exception", e);
                        } finally {
                            try {
                                idc.close();
                            } catch (RemoteException e) {
                                Log.e(this, "RemoteException", e);
                            }
                            
                            Model.getInstance().invokeBroadcast();
                        }
                    }
                }
            }
        });
        
    }
    
    
    /**
     * Stores the appointment ONLY in the database and NOT in the {@link Model}
     * 
     * @param date
     *            date of the appointment
     * @param name
     *            name of the appointment
     * @param description
     *            description of the appointment
     * @param severity
     *            {@link Severity} of the appointment
     */
    public void storeNewAppointmentWithoutModel(final Date date, final String name, final String description,
            final Severity severity) {
            
        if (description.equals("") && name.equals("")) {
            Toast.makeText(this.appContext, R.string.appointment_not_added, Toast.LENGTH_SHORT).show();
            return;
        }
        
        PMP.get().getResource(PMP_IDENTIFIER, new PMPRequestResourceHandler() {
            
            @Override
            public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    
                    if (createTable(idc)) {
                        try {
                            // The values to add
                            Map<String, String> values = new HashMap<String, String>();
                            
                            int id = Model.getInstance().getNewHighestId();
                            
                            values.put(SqlConnector.ID, String.valueOf(id));
                            values.put(SqlConnector.NAME, name);
                            values.put(SqlConnector.DESC, description);
                            values.put(SqlConnector.DATE, String.valueOf(date.getTime()));
                            values.put(SqlConnector.SEVERITY, severity.toString());
                            
                            long result = idc.insert(SqlConnector.DB_TABLE_NAME, null, values);
                            Log.v(this, "Return value of insert: " + result);
                            if (result != -1) {
                                Log.v(this, "Storing new appointment: id: " + String.valueOf(id) + " date: " + date
                                        + " description: " + description);
                            } else {
                                showToast(SqlConnector.this.appContext.getString(R.string.err_store));
                                Log.e(this, "Appointment not stored");
                            }
                        } catch (RemoteException e) {
                            showToast(SqlConnector.this.appContext.getString(R.string.err_store));
                            Log.e(this, "Remote Exception", e);
                        } finally {
                            try {
                                idc.close();
                            } catch (RemoteException e) {
                                Log.e(this, "RemoteException", e);
                            }
                            
                            Model.getInstance().invokeBroadcast();
                        }
                    }
                }
            }
        });
        
    }
    
    
    /**
     * Delete the appointment out of the SQL database with the given id and then calls {@link Model#deleteDateByID(int)}
     * 
     * @param id
     *            id of the appointment to delete
     */
    public void deleteAppointment(final Appointment appointment) {
        PMP.get().getResource(PMP_IDENTIFIER, new PMPRequestResourceHandler() {
            
            @Override
            public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    
                    if (createTable(idc)) {
                        try {
                            Log.v(this, "Trying to delete appointment with id: " + appointment.getId() + " name: "
                                    + appointment.getName() + " Description: " + appointment.getDescrpition());
                                    
                            String[] args = new String[1];
                            args[0] = String.valueOf(appointment.getId());
                            
                            /*
                             * Delete the date out of the database
                             */
                            if (idc.delete(SqlConnector.DB_TABLE_NAME, SqlConnector.ID + " = ?", args) == 1) {
                                Log.v(this, "Deleting date: id: " + String.valueOf(appointment.getId()));
                                Model.getInstance().deleteAppointment(appointment);
                            } else {
                                showToast(SqlConnector.this.appContext.getString(R.string.err_del));
                            }
                        } catch (RemoteException e) {
                            showToast(SqlConnector.this.appContext.getString(R.string.err_del));
                            Log.e(this, "Remote Exception", e);
                        } finally {
                            try {
                                idc.close();
                            } catch (RemoteException e) {
                                Log.e(this, "RemoteException", e);
                            }
                            
                            Model.getInstance().invokeBroadcast();
                        }
                    }
                }
            }
        });
    }
    
    
    public void deleteAllApointments() {
        PMP.get().getResource(PMP_IDENTIFIER, new PMPRequestResourceHandler() {
            
            @Override
            public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    
                    try {
                        if (idc.isTableExisted(SqlConnector.DB_TABLE_NAME)) {
                            if (idc.deleteTable(SqlConnector.DB_TABLE_NAME)) {
                                Log.d(this, "Table deleted");
                            } else {
                                Log.e(this, "Could not delete table");
                            }
                        }
                    } catch (RemoteException e) {
                        showToast(SqlConnector.this.appContext.getString(R.string.err_del));
                        Log.e(this, "RemoteException", e);
                    } finally {
                        try {
                            idc.close();
                        } catch (RemoteException e) {
                            Log.e(this, "RemoteException", e);
                        }
                        
                        Model.getInstance().invokeBroadcast();
                    }
                }
            }
        });
    }
    
    
    /**
     * Changes the appointment at the SQL database and then calls {@link Model#changeAppointment(int, String, String)}
     * 
     * @param id
     *            the id of the appointment to change
     * @param date
     *            the date that has changed
     * @param description
     *            the description that has changed
     */
    public void changeAppointment(final Integer id, final Date date, final Date oldDate, final String name,
            final String description, final Severity severity) {
        if (description.equals("") && name.equals("")) {
            Toast.makeText(this.appContext, R.string.appointment_not_changed, Toast.LENGTH_SHORT).show();
            return;
        }
        
        PMP.get().getResource(PMP_IDENTIFIER, new PMPRequestResourceHandler() {
            
            @Override
            public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    
                    if (createTable(idc)) {
                        try {
                            Map<String, String> values = new HashMap<String, String>();
                            
                            values.put(SqlConnector.NAME, name);
                            values.put(SqlConnector.DESC, description);
                            values.put(SqlConnector.DATE, String.valueOf(date.getTime()));
                            values.put(SqlConnector.SEVERITY, severity.toString());
                            /*
                             * Change the date in the database and only if one row
                             * was changed change, then change it in the model
                             */
                            if (idc.update(SqlConnector.DB_TABLE_NAME, values,
                                    SqlConnector.ID + " = " + String.valueOf(id), null) == 1) {
                                Model.getInstance().changeAppointment(id, date, oldDate, name, description, severity);
                                Log.v(this,
                                        "Changing date with id " + String.valueOf(id) + " to: name: " + name + " date: "
                                                + date + " description: " + description + " severity: "
                                                + severity.toString());
                            } else {
                                showToast(SqlConnector.this.appContext.getString(R.string.err_change));
                            }
                        } catch (RemoteException e) {
                            showToast(SqlConnector.this.appContext.getString(R.string.err_change));
                            Log.e(this, "Remote Exception", e);
                        } finally {
                            try {
                                idc.close();
                            } catch (RemoteException e) {
                                Log.e(this, "RemoteException", e);
                            }
                            
                            Model.getInstance().invokeBroadcast();
                        }
                    }
                }
            }
        });
    }
    
    
    /**
     * Deletes the table, creates a new table, stores all appointments in the given list and closes the
     * {@link ImportActivity}
     * 
     * @param appList
     *            {@link ArrayList} with {@link Appointment}s to store
     */
    public void storeAppointmentListInEmptyList(final ArrayList<Appointment> appList) {
        PMP.get().getResource(PMP_IDENTIFIER, new PMPRequestResourceHandler() {
            
            @Override
            public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    try {
                        
                        // Create a new table
                        if (createTable(idc)) {
                            
                            // Store all appointments
                            for (Appointment app : appList) {
                                // The values to add
                                Map<String, String> values = new HashMap<String, String>();
                                
                                int id = Model.getInstance().getNewHighestId();
                                
                                values.put(SqlConnector.ID, String.valueOf(id));
                                values.put(SqlConnector.NAME, app.getName());
                                values.put(SqlConnector.DESC, app.getDescrpition());
                                values.put(SqlConnector.DATE, String.valueOf(app.getDate().getTime()));
                                values.put(SqlConnector.SEVERITY, app.getSeverity().toString());
                                
                                long result = idc.insert(SqlConnector.DB_TABLE_NAME, null, values);
                                Log.v(this, "Return value of insert: " + result);
                                if (result != -1) {
                                    idc.query(SqlConnector.DB_TABLE_NAME, null, null, null, null, null,
                                            SqlConnector.DATE);
                                            
                                    Log.v(this, "Storing new appointment: id: " + String.valueOf(id) + " date: "
                                            + app.getDate() + " description: " + app.getDescrpition());
                                            
                                } else {
                                    showToast(SqlConnector.this.appContext.getString(R.string.err_store));
                                    Log.e(this, "Appointment not stored");
                                }
                            }
                        }
                    } catch (RemoteException e) {
                        showToast(SqlConnector.this.appContext.getString(R.string.err_del));
                        Log.e(this, "Remote Exception", e);
                    } finally {
                        UiManager.getInstance().getImportActivity().finish();
                        try {
                            idc.close();
                        } catch (RemoteException e) {
                            Log.e(this, "RemoteException", e);
                        }
                        
                        Model.getInstance().invokeBroadcast();
                    }
                }
            }
        });
    }
    
    
    /**
     * Checks if the table exists, if not then the table will be created
     * 
     * @param idc
     *            {@link IDatabaseConnection} to create the table
     */
    private Boolean createTable(IDatabaseConnection idc) {
        try {
            idc.open(DB_TABLE_NAME);
            
            if (!idc.isTableExisted(SqlConnector.DB_TABLE_NAME)) {
                
                // Columns of the table
                Map<String, String> columns = new HashMap<String, String>();
                columns.put(SqlConnector.ID, "INTEGER");
                columns.put(SqlConnector.NAME, "TEXT");
                columns.put(SqlConnector.DESC, "TEXT");
                columns.put(SqlConnector.DATE, "TEXT");
                columns.put(SqlConnector.SEVERITY, "TEXT");
                
                // Creates the table
                Log.v(this, "Creating table");
                
                // Create the table
                if (idc.createTable(SqlConnector.DB_TABLE_NAME, columns, null)) {
                    Log.v(this, "Table created. Name: " + SqlConnector.DB_TABLE_NAME);
                    return true;
                } else {
                    Log.e(this, "Couldn't create table");
                    showToast(this.appContext.getString(R.string.err_create));
                    return false;
                }
            } else {
                Log.v(this, "Table already exists");
                return true;
            }
        } catch (RemoteException e) {
            Log.e(this, "RemoteException", e);
        }
        return false;
    }
    
    
    /**
     * Shows a toast. Called from inside a {@link Thread}
     * 
     * @param message
     *            to show
     */
    private void showToast(final String message) {
        new Thread() {
            
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(Model.getInstance().getContext(), message, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
    }
}
