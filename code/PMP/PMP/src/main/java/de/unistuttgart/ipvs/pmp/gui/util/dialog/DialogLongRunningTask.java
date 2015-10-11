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
package de.unistuttgart.ipvs.pmp.gui.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.shared.gui.view.BasicTitleView;

public class DialogLongRunningTask extends Dialog {
    
    private Runnable runnable;
    
    
    public DialogLongRunningTask(Context context, Runnable runnable) {
        super(context);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_long_running_task);
        
        setCancelable(false);
        
        this.runnable = runnable;
    }
    
    
    public DialogLongRunningTask setTitle(String title) {
        ((BasicTitleView) findViewById(R.id.Title)).setTitle(title);
        return this;
    }
    
    
    public DialogLongRunningTask setMessage(String message) {
        ((TextView) findViewById(R.id.TextView_Description)).setText(message);
        return this;
    }
    
    
    public void start() {
        show();
        new Thread() {
            
            @Override
            public void run() {
                DialogLongRunningTask.this.runnable.run();
                
                taskCompleted();
            };
        }.start();
    }
    
    
    private void taskCompleted() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            
            @Override
            public void run() {
                
                dismiss();
            }
        });
    }
}
