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

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

/**
 * An abstract implementation of a on click listener which can act as {@link OnClickListener} and as a
 * {@link OnLongClickListener} at the same time.
 * 
 * @author Jakob Jarosch
 */
public abstract class OnShortLongClickListener implements OnClickListener, OnLongClickListener {
    
    /**
     * Auto registers the {@link OnShortLongClickListener} as the {@link OnClickListener} and
     * {@link OnLongClickListener} of the given {@link View}.
     * 
     * @param view
     */
    public void autoRegister(View view) {
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }
    
    
    @Override
    public final boolean onLongClick(View v) {
        onClick(v);
        
        return true;
    }
    
}
