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
package de.unistuttgart.ipvs.pmp.gui.preset.conflict;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.model.conflicts.ConflictModel;
import de.unistuttgart.ipvs.pmp.model.conflicts.IProcessingCallback;

/**
 * The {@link ScanningProgressDialog} informs the user about the conflict scanning progress.
 * It can be started by using the {@link ScanningProgressDialog#start()} method. Dialog will automatically dismissed on
 * completion.
 * 
 * @author Jakob Jarosch
 */
public class ScanningProgressDialog extends Dialog {
    
    private ICallback callback;
    
    /**
     * The callback is invoked when calculation is finished.
     * 
     * @author Jakob Jarosch
     */
    public interface ICallback {
        
        public void finished();
    }
    
    
    /**
     * Creates a new instance of the {@link ScanningProgressDialog}.
     * 
     * @param context
     *            Context used for dialog creation.
     * @param callback
     *            {@link ICallback} which is invoked on completion.
     */
    public ScanningProgressDialog(Context context, ICallback callback) {
        super(context);
        
        this.callback = callback;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_conflicts_scanning);
        
        setCancelable(false);
    }
    
    
    /**
     * Starts the calculation process.
     */
    public void start() {
        show();
        
        ConflictModel.getInstance().calculateConflicts(new IProcessingCallback() {
            
            @Override
            public void stepMessage(String message) {
                setMessage(message);
            }
            
            
            @Override
            public void progressUpdate(int completed, int fullCount) {
                setProgress(completed, fullCount);
                
            }
            
            
            @Override
            public void finished() {
                ScanningProgressDialog.this.callback.finished();
                dismiss();
            }
            
        });
    }
    
    
    /**
     * Updates the dialog message.
     * 
     * @param message
     *            Message which should be displayed.
     */
    private void setMessage(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            
            @Override
            public void run() {
                ((TextView) findViewById(R.id.TextView_Description)).setText(message);
            }
        });
    }
    
    
    /**
     * Updates the progress.
     * 
     * @param completed
     *            Count of finished steps.
     * @param fullCount
     *            Count of all steps.
     */
    private void setProgress(final int completed, final int fullCount) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            
            @Override
            public void run() {
                ProgressBar progress = (ProgressBar) findViewById(R.id.ProgressBar);
                progress.setProgress(completed);
                progress.setMax(fullCount);
            }
        });
    }
}
