/*
 * Copyright 2012 pmp-android development team
 * Project: PMP-API
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
package de.unistuttgart.ipvs.pmp.api.handler._default;

import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.app.Dialog;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.api.gui.servicefeature.ServiceFeatureDialog;
import de.unistuttgart.ipvs.pmp.api.handler.AbortIPCException;
import de.unistuttgart.ipvs.pmp.api.handler.PMPRequestServiceFeaturesHandler;

/**
 * The {@link PMPDefaultRequestSFHandler} is used to display a {@link Dialog} for requesting additional Service
 * Features. The
 * user is able to decide between changing Service Features and aborting the action. Aborting means the request will be
 * killed and no request for new Service Features will be sent.
 * 
 * @author Jakob Jarosch
 */
public class PMPDefaultRequestSFHandler extends PMPRequestServiceFeaturesHandler {
    
    /**
     * The {@link Activity} which initiated the registration.
     */
    protected Activity activity;
    
    /**
     * Boolean which is set to true when the request should be killed.
     */
    private volatile boolean killServiceFeatureRequest = false;
    
    /**
     * Semaphore which locks the handler until the user made a decision in the {@link Dialog}.
     */
    private Semaphore semaphore = new Semaphore(0);
    
    
    public PMPDefaultRequestSFHandler(Activity activity) {
        this.activity = activity;
    }
    
    
    @Override
    public void onPrepare() throws AbortIPCException {
        super.onPrepare();
        
        /* Dispatch the dialog creation on the activity UI-thread. */
        this.activity.runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                new ServiceFeatureDialog(PMPDefaultRequestSFHandler.this.activity, PMPDefaultRequestSFHandler.this)
                        .show();
            }
        });
        
        /* Wait until the dialog unblocks the handler thread. */
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            Log.e(this, "Interrupted the ServiceFeatureHandler", e);
        }
        
        /* Abort the IPCRequest if requested by the user. */
        if (this.killServiceFeatureRequest) {
            throw new AbortIPCException();
        }
    }
    
    
    /**
     * Kills the current Service Features request. No request will be sent to PMP.
     * Has to be called before {@link PMPDefaultRequestSFHandler#unblockHandler()} is called.
     */
    public void killServiceFeatureRequest() {
        this.killServiceFeatureRequest = true;
    }
    
    
    /**
     * Unblocks the Handler and executes the request if not killed.
     */
    public void unblockHandler() {
        this.semaphore.release();
    }
    
}
