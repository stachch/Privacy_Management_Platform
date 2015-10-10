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
package de.unistuttgart.ipvs.pmp.service;

import java.util.concurrent.Semaphore;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.api.PMP;
import de.unistuttgart.ipvs.pmp.model.element.app.App;
import de.unistuttgart.ipvs.pmp.util.Restarter;

/**
 * 
 * <p>
 * External service for communication between PMP and {@link App}s. Do not directly connect to the service, use the
 * {@link PMP} API instead.
 * </p>
 * 
 * <p>
 * Additionally used for the contexts with {@link PMPServiceContextThread}. Semantics are:
 * </p>
 * <ul>
 * <li>If not yet started, it will start and launch one {@link PMPServiceContextThread}.</li>
 * <li>If started and an {@link PMPServiceContextThread} is present, the start will be ignored.</li>
 * <li>If started and an {@link PMPServiceContextThread} is not present, another one will be launched.</li>
 * <li>If the {@link PMPServiceContextThread} completes, it can schedule the next start unless it has already been
 * scheduled.</li>
 * </ul>
 * 
 * 
 * @author Jakob Jarosch
 */
public class PMPService extends Service {
    
    private static final long CONTEXT_SERVICE_INTERVAL = 5L * 60L * 1000L;
    
    private boolean scheduled;
    private PMPServiceContextThread thread;
    private Semaphore mutex;
    
    
    public PMPService() {
        this.scheduled = false;
        this.thread = new PMPServiceContextThread(this);
        this.mutex = new Semaphore(1);
    }
    
    
    @Override
    public IBinder onBind(Intent intent) {
        ServiceNotification.setBound(true);
        return new PMPServiceImplementation();
    }
    
    
    @Override
    public boolean onUnbind(Intent intent) {
        ServiceNotification.setBound(false);
        return super.onUnbind(intent);
    }
    
    
    @Override
    public void onDestroy() {
        ServiceNotification.setBound(false);
        ServiceNotification.setWorking(false);
        super.onDestroy();
    }
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            this.mutex.acquire();
            
            if (intent.getBooleanExtra(Restarter.RESTARTER_IDENTIFIER, false)) {
                this.scheduled = false;
            }
        } catch (InterruptedException e) {
            Log.e(this, "Interrupted while onStartCommand", e);
            this.scheduled = false;
            
        } finally {
            this.mutex.release();
        }
        
        ServiceNotification.setWorking(true);
        
        if (!this.thread.isAlive()) {
            this.thread = new PMPServiceContextThread(this);
            this.thread.start();
            return START_STICKY;
            
        } else {
            return START_NOT_STICKY;
        }
    }
    
    
    /**
     * Called by the {@link PMPServiceContextThread} once its done.
     * 
     * @param startId
     *            corresponding id
     * @param stop
     *            whether to stop the service or not.
     * @throws InterruptedException
     */
    public void contextsDone(boolean stop) {
        
        try {
            this.mutex.acquire();
            if (!stop && !this.scheduled) {
                Restarter.scheduleServiceRestart(this, CONTEXT_SERVICE_INTERVAL);
                this.scheduled = true;
            }
            
        } catch (InterruptedException e) {
            Log.e(this, "Interrupted while contextsDone", e);
            
        } finally {
            this.mutex.release();
        }
        
        ServiceNotification.setWorking(false);
        stopSelf();
        
    }
    
}
