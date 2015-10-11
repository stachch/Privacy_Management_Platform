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
package de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.R;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.fsConnector.FileSystemConnector;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.fsConnector.FileSystemListActionType;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.util.UiManager;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.model.Model;
import de.unistuttgart.ipvs.pmp.resourcegroups.filesystem.resources.FileDetails;
import de.unistuttgart.ipvs.pmp.shared.api.PMP;

/**
 * This is the (list-)activity for the import. It shows all available files for importing.
 * 
 * @author Marcus Vetter
 *         
 */
public class ImportActivity extends ListActivity {
    
    /**
     * The arrayAdapter of the list
     */
    private ArrayAdapter<FileDetails> importArrayAdapter;
    
    
    /**
     * Called, when the activity is created
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Model.getInstance().addImportHandler(new Handler());
        setContentView(R.layout.import_list_layout);
        
        setTitle(R.string.import_appointments);
        
        // Store the context
        Model.getInstance().setImportContext(this);
        UiManager.getInstance().setImportActivity(this);
        
        /*
         * Fill the list of files for importing.
         * It is also used to check for exporting, if a file already exists.
         */
        new FileSystemConnector().prepare(FileSystemListActionType.NONE);
        
        // Array adapter that is needed to show the list of dates
        this.importArrayAdapter = new ArrayAdapter<FileDetails>(this, R.layout.import_list_item,
                Model.getInstance().getFileList());
        Model.getInstance().setImportArrayAdapter(this.importArrayAdapter);
        setListAdapter(this.importArrayAdapter);
        new FileSystemConnector().listFilesImport();
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        
        /*
         * Listener for clicking one item. Opens a new dialog where the user can
         * change the date.
         */
        listView.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final FileDetails file = Model.getInstance().getFileList().get(position);
                Model.getInstance().clearLocalList();
                new FileSystemConnector().importAppointments(file.getName());
            }
        });
        
        /*
         * Listener for long clicking on one item. Opens a context menu where
         * the user can delete a file
         */
        listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(getString(R.string.menu));
                menu.add(0, 0, 0, R.string.delete);
            }
        });
        
        // Update the visibility of the "no files avaiable" textview
        updateNoAvaiableFilesTextView();
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Update the visibility of the "no files avaiable" textview
        updateNoAvaiableFilesTextView();
    };
    
    
    @Override
    public boolean onContextItemSelected(MenuItem aItem) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();
        final FileDetails clicked = Model.getInstance().getFileList().get(menuInfo.position);
        /*
         * Called when the user presses sth. in the menu that appears while long clicking
         */
        if (aItem.getItemId() == 0) {
            if (PMP.get().isServiceFeatureEnabled("export")) {
                new FileSystemConnector().deleteFile(clicked);
            } else {
                String[] req = new String[1];
                req[0] = "export";
                UiManager.getInstance().showServiceFeatureInsufficientDialog(req);
            }
            return true;
        }
        return false;
    }
    
    
    /**
     * Update the visibility of the "no files available" textview
     */
    public void updateNoAvaiableFilesTextView() {
        // add text view "no appointments available", if the list is empty
        TextView tv = (TextView) findViewById(R.id.no_files_avaiable);
        if (Model.getInstance().getFileList().size() > 0) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
        }
    }
    
}
