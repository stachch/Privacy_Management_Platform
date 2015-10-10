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
package de.unistuttgart.ipvs.pmp.model.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * A handler for tasks with long duration, displays a progress dialog in the meantime.
 * 
 * @author Tobias Kuhn
 *         
 */
public abstract class LongTaskProgressDialog<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    
    @SuppressWarnings("unchecked")
    public abstract Result run(Params... params);
    
    
    public void processResult(Result result) {
    }
    
    private ProgressDialog progressDialog;
    
    
    public LongTaskProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
    
    
    @Override
    protected void onPreExecute() {
        this.progressDialog.show();
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    protected Result doInBackground(Params... params) {
        return run(params);
    };
    
    
    @Override
    protected void onPostExecute(Result result) {
        this.progressDialog.dismiss();
        processResult(result);
    }
    
}
