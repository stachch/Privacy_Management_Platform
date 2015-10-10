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
package de.unistuttgart.ipvs.pmp.gui.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.preset.DialogPresetsImportExport;
import de.unistuttgart.ipvs.pmp.gui.preset.DialogPresetsImportExportId;
import de.unistuttgart.ipvs.pmp.gui.preset.DialogPresetsImportExportId.ICallback;
import de.unistuttgart.ipvs.pmp.gui.util.dialog.DialogLongRunningTask;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.server.ServerProvider;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetSet;

/**
 * The {@link PresetSetTools} provide some functionality for export and import of {@link IPreset}s.
 * 
 * @author Jakob Jarosch
 */
public class PresetSetTools {
    
    /**
     * The {@link ICallbackImport} is invoked on completion of a import process.
     * 
     * @author Jakob Jarosch
     */
    public interface ICallbackImport {
        
        /**
         * The method in called when the import process is finished.
         * 
         * @param success
         *            Indicates whether the import was successful or not.
         */
        public void ended(boolean success);
    }
    
    /**
     * The {@link ICallbackDownload} is invoked on completion of a {@link IPresetSet} download.
     * 
     * @author Jakob Jarosch
     */
    public interface ICallbackDownload {
        
        /**
         * The method is called directly after completion of the download.
         * 
         * @param presetSet
         *            An {@link IPresetSet} is returned, or null if no {@link IPresetSet} could be fetched.
         */
        public void ended(IPresetSet presetSet);
    }
    
    /**
     * The {@link ICallbackUpload} is invoked on completion of a {@link IPresetSet} upload.
     * 
     * @author Jakob Jarosch
     */
    public interface ICallbackUpload {
        
        /**
         * The method is called directly after completion of the upload.
         * 
         * @param id
         *            An unique identifier for the {@link IPresetSet} is returned, or null if upload has failed.
         */
        public void ended(String id);
    }
    
    
    /**
     * Start the import process for a {@link IPresetSet}. The import process is based on interaction with the user.
     * Must be called from the main thread!
     * 
     * @param context
     *            {@link Context} required to create {@link Dialog}s for user interaction.
     * @param callback
     *            {@link ICallbackImport} is invoked after completion of the import.
     * @param id
     *            The id which should be filled into the field. Can be null.
     */
    public static void importPresets(final Context context, final ICallbackImport callback, String id) {
        new DialogPresetsImportExportId(context, new ICallback() {
            
            @Override
            public void ended(String id) {
                PresetSetTools.downloadPresetSet(context, id, new ICallbackDownload() {
                    
                    @Override
                    public void ended(final IPresetSet presetSet) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            
                            @Override
                            public void run() {
                                if (presetSet == null) {
                                    Toast.makeText(context, context.getString(R.string.presets_import_download_failed),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    new DialogPresetsImportExport(context, presetSet, callback).show();
                                }
                            }
                        });
                    }
                });
            }
        }, id).show();
    }
    
    
    /**
     * Start the export process for some {@link IPreset}s. The export process is based on interaction with the user.
     * Must be called from the main thread!
     * 
     * @param context
     *            {@link Context} required to create {@link Dialog}s for user interaction.
     */
    public static void exportPresets(Context context) {
        new DialogPresetsImportExport(context).show();
    }
    
    
    /**
     * Starts the download of a {@link IPresetSet} from the JPMPPS server.
     * 
     * @param context
     *            {@link Context} required to display the in Process dialog.
     * @param id
     *            Unique identifier of the {@link IPresetSet} which should be downloaded.
     * @param callback
     *            {@link ICallbackDownload} which will be invoked on completion.
     */
    public static void downloadPresetSet(Context context, final String id, final ICallbackDownload callback) {
        Runnable executor = new Runnable() {
            
            @Override
            public void run() {
                IPresetSet presetSet = ServerProvider.getInstance().loadPresetSet(id);
                
                callback.ended(presetSet);
            }
        };
        
        DialogLongRunningTask dlrt = new DialogLongRunningTask(context, executor);
        dlrt.setTitle(context.getString(R.string.presets_import_download_title));
        dlrt.setMessage(context.getString(R.string.presets_import_download_message));
        dlrt.start();
    }
    
    
    /**
     * Starts the upload of a {@link IPresetSet} to the JPMPPS server.
     * 
     * @param context
     *            {@link Context} required to display the in Process dialog.
     * @param presetSet
     *            {@link IPresetSet} which should be uploaded on the server.
     * @param callback
     *            {@link ICallbackUpload} which will be invoked on completion.
     */
    public static void uploadPresetSet(Context context, final IPresetSet presetSet, final ICallbackUpload callback) {
        Runnable executor = new Runnable() {
            
            @Override
            public void run() {
                String id = ServerProvider.getInstance().storePresetSet(presetSet);
                
                callback.ended(id);
            }
        };
        
        DialogLongRunningTask dlrt = new DialogLongRunningTask(context, executor);
        dlrt.setTitle(context.getString(R.string.presets_export_upload_title));
        dlrt.setMessage(context.getString(R.string.presets_export_upload_message));
        dlrt.start();
    }
}
