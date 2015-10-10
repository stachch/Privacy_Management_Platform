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

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.PresetSetTools;
import de.unistuttgart.ipvs.pmp.gui.util.PresetSetTools.ICallbackImport;
import de.unistuttgart.ipvs.pmp.gui.util.PresetSetTools.ICallbackUpload;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.xml.InvalidPresetSetException;
import de.unistuttgart.ipvs.pmp.model.xml.XMLInterface;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetSet;

/**
 * The {@link DialogPresetsImportExport} provides a view for selecting the Presets which should be imported or exported.
 * 
 * @author Jakob Jarosch
 */
public class DialogPresetsImportExport extends Dialog {
    
    /**
     * The {@link ICallbackImport} which is invoked on dialog completion.
     */
    private ICallbackImport callback;
    
    /**
     * Boolean is set to true if the {@link Dialog} was created for an import.
     */
    boolean isExport;
    
    /**
     * The List of {@link IPreset}s which are available for export.
     */
    private List<IPreset> presets;
    
    /**
     * The List of {@link IPreset} which are available for import.
     */
    private IPresetSet presetsNew;
    
    /**
     * The ListView which holds a checkable list of all {@link IPreset}s.
     */
    private ListView listView;
    
    private AdapterString listViewAdapter;
    
    
    /**
     * Creating a new {@link DialogPresetsImportExport} for an export.
     * 
     * @param context
     *            {@link Context} which is required for {@link Dialog} creation.
     */
    public DialogPresetsImportExport(Context context) {
        super(context);
        
        this.isExport = true;
        this.presets = ModelProxy.get().getPresets();
        
        initializeDialog();
    }
    
    
    /**
     * Creating a new {@link DialogPresetsImportExport} for an import.
     * 
     * @param context
     * @param presets
     * @param callback
     */
    public DialogPresetsImportExport(Context context, IPresetSet presets, ICallbackImport callback) {
        super(context);
        
        this.isExport = false;
        this.presetsNew = presets;
        this.callback = callback;
        
        initializeDialog();
    }
    
    
    /**
     * Initializes the dialog.
     */
    private void initializeDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_presets_import_export);
        
        this.listView = (ListView) findViewById(R.id.ListView_Presets);
        
        refresh();
        addListener();
    }
    
    
    /**
     * Updates the UI.
     */
    private void refresh() {
        /*
         * Set the correct label for confirm button and display the checkbox if required.
         */
        Button confirmButton = (Button) findViewById(R.id.Button_Confirm);
        CheckBox importCheckbox = (CheckBox) findViewById(R.id.CheckBox_ImportOverride);
        if (this.isExport) {
            confirmButton.setText(getContext().getString(R.string.export_presets));
            importCheckbox.setVisibility(View.GONE);
        } else {
            confirmButton.setText(getContext().getString(R.string.import_presets));
            importCheckbox.setVisibility(View.VISIBLE);
        }
        
        /*
         * Initialize the ListView with the names of the presets.
         */
        List<String> items = new ArrayList<String>();
        if (this.isExport) {
            for (IPreset preset : this.presets) {
                items.add(preset.getName());
            }
        } else {
            for (de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPreset preset : this.presetsNew.getPresets()) {
                items.add(preset.getName());
            }
        }
        
        /*
         * Configure the ListView.
         */
        this.listView.setItemsCanFocus(false);
        this.listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        this.listViewAdapter = new AdapterString(getContext(), items);
        this.listView.setAdapter(this.listViewAdapter);
    }
    
    
    private boolean importPresets(boolean override) {
        /* 
         * First of all we have to remove all presets from the set the user don't like
         */
        List<de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPreset> newPresets = this.presetsNew.getPresets();
        int listSize = this.presetsNew.getPresets().size();
        for (int i = listSize - 1; i >= 0; i--) {
            if (!this.listViewAdapter.isChecked(i)) {
                newPresets.remove(i);
            }
        }
        
        /*
         * Imports the presets into the PMP model.
         */
        try {
            XMLInterface.instance.importPresets(newPresets, override);
            return true;
        } catch (InvalidPresetSetException e) {
            return false;
        }
    }
    
    
    /**
     * Exports all checked {@link IPreset}s into a {@link IPresetSet}.
     * 
     * @return Returns a generated {@link IPresetSet}.
     */
    private IPresetSet exportPresets() {
        List<IPreset> exportPresets = new ArrayList<IPreset>(this.presets.size());
        exportPresets.addAll(this.presets);
        
        int listSize = exportPresets.size();
        for (int i = listSize - 1; i >= 0; i--) {
            if (!this.listViewAdapter.isChecked(i)) {
                exportPresets.remove(i);
            }
        }
        
        return XMLInterface.instance.exportPresets(exportPresets);
    }
    
    
    /**
     * Adds the listener to all clickable elements.
     */
    private void addListener() {
        /*
         * Confirm Button on click listener.
         */
        ((Button) findViewById(R.id.Button_Confirm)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (DialogPresetsImportExport.this.isExport) {
                    /* in export mode */
                    IPresetSet presetSet = exportPresets();
                    PresetSetTools.uploadPresetSet(getContext(), presetSet, new ICallbackUpload() {
                        
                        @Override
                        public void ended(final String id) {
                            /* Invoked on upload completion */
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                
                                @Override
                                public void run() {
                                    if (id != null) {
                                        new DialogPresetsImportExportId(getContext(), id).show();
                                        dismiss();
                                    } else {
                                        Toast.makeText(getContext(),
                                                getContext().getString(R.string.presets_export_upload_failed),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    /* in import mode */
                    boolean override = ((CheckBox) findViewById(R.id.CheckBox_ImportOverride)).isChecked();
                    boolean importSuccess = importPresets(override);
                    if (importSuccess) {
                        Toast.makeText(getContext(), getContext().getString(R.string.presets_import_succeed),
                                Toast.LENGTH_LONG).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), getContext().getString(R.string.presets_import_failed),
                                Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                    
                    if (DialogPresetsImportExport.this.callback != null) {
                        DialogPresetsImportExport.this.callback.ended(importSuccess);
                    }
                }
            }
            
        });
        
        /*
         * Cancel button on click listener.
         */
        ((Button) findViewById(R.id.Button_Cancel)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
                if (DialogPresetsImportExport.this.callback != null) {
                    DialogPresetsImportExport.this.callback.ended(false);
                }
            }
        });
    }
}
