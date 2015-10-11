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
package de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.R;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.fsConnector.FileSystemConnector;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.model.Model;
import de.unistuttgart.ipvs.pmp.shared.Log;

/**
 * This is the dialog for exporting. You can enter a file name for exporting.
 * 
 * @author Marcus Vetter
 *         
 */
public class ExportDialog extends Dialog {
    
    /**
     * The file name input
     */
    private TextView fileTextView;
    
    /**
     * The confirm button
     */
    private Button confirm;
    
    
    /**
     * Necessary constructor
     * 
     * @param context
     *            the context
     */
    public ExportDialog(Context context) {
        super(context);
        new FileSystemConnector().listFilesExport();
    }
    
    
    /**
     * Called when the dialog is first created. Gets all elements of the gui
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.export_dialog);
        
        this.setTitle(R.string.export_appointments);
        
        this.fileTextView = (TextView) findViewById(R.id.export_file_name_input);
        
        this.confirm = (Button) findViewById(R.id.ExportConfirmButton);
        
        this.confirm.setOnClickListener(new ConfirmListener());
        
        /*
         * Needed to fill the width of the screen
         */
        getWindow().setLayout(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                
    }
    
    /**
     * Listener class only needed for the confirm button
     * 
     */
    protected class ConfirmListener implements android.view.View.OnClickListener {
        
        /**
         * Called when the confirm button is pressed. Export the appointment list.
         */
        @Override
        public void onClick(View v) {
            
            final String fileName = ExportDialog.this.fileTextView.getText().toString();
            if (fileName.length() != 0) {
                if (!Model.getInstance().isFileNameExisting(fileName)) {
                    Log.d(this, "Exporting...");
                    new FileSystemConnector().exportAppointments(Model.getInstance().getAppointmentList(), fileName);
                } else {
                    Log.d(this, "Filename already exists!");
                    
                    // Show the confirm dialog for overwriting the file
                    new AlertDialog.Builder(Model.getInstance().getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.export_override_question)
                            .setMessage(R.string.export_override_attention)
                            .setPositiveButton(R.string.export_override_conf, new DialogInterface.OnClickListener() {
                                
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Override the file
                                    Log.d(this, "Exporting... Filename: " + fileName);
                                    new FileSystemConnector()
                                            .exportAppointments(Model.getInstance().getAppointmentList(), fileName);
                                }
                                
                            })
                            .setNegativeButton(R.string.export_override_cancel, new DialogInterface.OnClickListener() {
                                
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Model.getInstance().getContext(), R.string.export_toast_cancel,
                                            Toast.LENGTH_SHORT).show();
                                    Log.d(this, "Exporting canceled.");
                                }
                            }).show();
                            
                }
                
                dismiss();
            } else {
                Toast.makeText(Model.getInstance().getContext(), R.string.export_error_filename, Toast.LENGTH_SHORT)
                        .show();
            }
        }
        
    }
    
}
