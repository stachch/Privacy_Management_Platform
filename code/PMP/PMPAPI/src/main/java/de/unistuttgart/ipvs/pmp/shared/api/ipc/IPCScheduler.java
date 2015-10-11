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
package de.unistuttgart.ipvs.pmp.shared.api.ipc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.api.handler.AbortIPCException;
import de.unistuttgart.ipvs.pmp.shared.api.ipc.command.IPCCommand;

/**
 * The API IPC Scheduler to queue IPC commands and execute them.
 * 
 * @author Tobias Kuhn
 *         
 */
public final class IPCScheduler extends Thread {
    
    /**
     * The queue that contains all the commands to be executed
     */
    private final BlockingQueue<IPCCommand> queue;
    
    /**
     * The context to use for the connection
     */
    private final Context context;
    
    /**
     * The connection to be used while executing commands.
     */
    protected IPCConnection connection;
    
    
    /**
     * Creates a new {@link IPCScheduler}.
     * 
     * @param context
     *            the context to use for the connections
     */
    public IPCScheduler(Context context) {
        super("IPC Scheduling");
        this.queue = new LinkedBlockingQueue<IPCCommand>();
        this.context = context;
        start();
    }
    
    
    /**
     * Queues a new {@link IPCCommand} to be executed.
     * 
     * @param command
     *            the IPC command to be executed at an arbitrary point in the future
     */
    public void queue(IPCCommand command) {
        if (!this.queue.offer(command)) {
            Log.e(this, "BlockingQueue ran out of capacity!");
        }
    }
    
    
    @Override
    public void run() {
        // must be initialized in a different thread
        this.connection = new IPCConnection(this.context);
        
        while (!isInterrupted()) {
            
            try {
                final IPCCommand command = this.queue.take();
                
                new Thread() {
                    
                    @Override
                    public void run() {
                        IBinder binder = null;
                        
                        try {
                            command.getHandler().onPrepare();
                        } catch (AbortIPCException aipce) {
                            return;
                        }
                        
                        synchronized (IPCScheduler.this.connection) {
                            IPCScheduler.this.connection.setDestinationService(command.getDestinationService());
                            
                            // handle timeout
                            if (command.getTimeout() < System.currentTimeMillis()) {
                                command.getHandler().onTimeout();
                                return;
                            }
                            
                            // try connecting
                            binder = IPCScheduler.this.connection.getBinder();
                        }
                        
                        if (binder != null) {
                            command.execute(binder);
                        } else {
                            command.getHandler().onBindingFailed();
                        }
                        
                        command.getHandler().onFinalize();
                        
                    };
                }.start();
                
            } catch (InterruptedException e) {
                continue;
            }
            
        }
    }
}
