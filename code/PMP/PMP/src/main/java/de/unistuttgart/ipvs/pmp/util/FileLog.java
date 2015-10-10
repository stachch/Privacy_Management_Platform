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
package de.unistuttgart.ipvs.pmp.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import android.content.Context;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.gui.util.PMPPreferences;

/**
 * {@link FileHandler} to log certain events that shall actually be logged to a file.
 * 
 * @author Tobias Kuhn
 * 
 */
public class FileLog extends FileHandler implements IFileLog {
    
    private static class NullFileLog implements IFileLog {
        
        @Override
        public void log(Object origin, int granularity, Level level, String message, Object... params) {
            Log.w(this, "Cannot log to file: FileHandler threw IOException");
        }
        
        
        @Override
        public void logWithForward(Object origin, Throwable exception, int granularity, Level level, String message,
                Object... params) {
            String msg = String.format(message, params);
            int lvl = level.intValue();
            
            if (lvl >= Level.SEVERE.intValue()) {
                if (exception != null) {
                    Log.e(origin, msg, exception);
                } else {
                    Log.e(origin, msg);
                }
            } else if (lvl >= Level.WARNING.intValue()) {
                if (exception != null) {
                    Log.w(origin, msg, exception);
                } else {
                    Log.w(origin, msg);
                }
            } else if (lvl >= Level.INFO.intValue()) {
                if (exception != null) {
                    Log.i(origin, msg, exception);
                } else {
                    Log.i(origin, msg);
                }
            } else if (lvl >= Level.CONFIG.intValue()) {
                if (exception != null) {
                    Log.d(origin, msg, exception);
                } else {
                    Log.d(origin, msg);
                }
            } else {
                if (exception != null) {
                    Log.v(origin, msg, exception);
                } else {
                    Log.v(origin, msg);
                }
            }
            
            log(origin, granularity, level, message, params);
        }
        
    }
    
    /**
     * Installation, uninstallation of RGs;
     * Registration, unregistration of Apps
     */
    public static final int GRANULARITY_COMPONENT_CHANGES = 1 << 1;
    
    /**
     * Changes of SFs (Simple Mode);
     * Changes of Presets, PSs or Contexts (Expert Mode)
     */
    public static final int GRANULARITY_SETTING_CHANGES = 1 << 2;
    
    /**
     * ContextCondition changes;
     * PS changes caused by that
     */
    public static final int GRANULARITY_CONTEXT_CHANGES = 1 << 3;
    
    /**
     * SF requests by Apps;
     * PS values by RGs
     */
    public static final int GRANULARITY_SETTING_REQUESTS = 1 << 4;
    
    private static volatile IFileLog instance;
    
    private static final File LOG_FILE_DIR = PMPApplication.getContext().getDir("log", Context.MODE_PRIVATE);
    private static final String LOG_FILE_NAME = LOG_FILE_DIR.getAbsolutePath() + "/log.txt";
    
    private static final String TAG = "FileLog";
    
    
    private FileLog() throws IOException {
        super(LOG_FILE_NAME, true);
        setFormatter(new SimpleFormatter());
    }
    
    
    public static IFileLog get() {
        if (instance == null || instance instanceof NullFileLog) {
            try {
                instance = new FileLog();
            } catch (IOException e) {
                Log.e(TAG, "Failed creating FileHandler for " + LOG_FILE_NAME, e);
                instance = new NullFileLog();
            }
        }
        return instance;
    }
    
    
    @Override
    public void log(Object origin, int granularity, Level level, String message, Object... params) {
        if ((PMPPreferences.getInstance().getLoggingGranularity() & granularity) > 0) {
            LogRecord lr = new LogRecord(level, String.format(message, params));
            lr.setSourceClassName(origin.getClass().getSimpleName());
            lr.setSourceMethodName("");
            publish(lr);
        }
    }
    
    
    @Override
    public void logWithForward(Object origin, Throwable exception, int granularity, Level level, String message,
            Object... params) {
        String msg = String.format(message, params);
        int lvl = level.intValue();
        
        if (lvl >= Level.SEVERE.intValue()) {
            if (exception != null) {
                Log.e(origin, msg, exception);
            } else {
                Log.e(origin, msg);
            }
        } else if (lvl >= Level.WARNING.intValue()) {
            if (exception != null) {
                Log.w(origin, msg, exception);
            } else {
                Log.w(origin, msg);
            }
        } else if (lvl >= Level.INFO.intValue()) {
            if (exception != null) {
                Log.i(origin, msg, exception);
            } else {
                Log.i(origin, msg);
            }
        } else if (lvl >= Level.CONFIG.intValue()) {
            if (exception != null) {
                Log.d(origin, msg, exception);
            } else {
                Log.d(origin, msg);
            }
        } else {
            if (exception != null) {
                Log.v(origin, msg, exception);
            } else {
                Log.v(origin, msg);
            }
        }
        
        log(origin, granularity, level, message, params);
    }
}
