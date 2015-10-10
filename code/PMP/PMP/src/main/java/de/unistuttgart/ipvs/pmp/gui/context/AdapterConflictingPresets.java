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
package de.unistuttgart.ipvs.pmp.gui.context;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;

public class AdapterConflictingPresets extends BaseAdapter {
    
    private IContextAnnotation contextAnnotation;
    private List<IPreset> presets;
    private Context context;
    
    
    public AdapterConflictingPresets(Context context, IContextAnnotation contextAnnotation, List<IPreset> presets) {
        this.contextAnnotation = contextAnnotation;
        this.presets = presets;
        this.context = context;
    }
    
    
    @Override
    public int getCount() {
        return this.presets.size();
    }
    
    
    @Override
    public Object getItem(int arg0) {
        return this.presets.get(arg0);
    }
    
    
    @Override
    public long getItemId(int arg0) {
        return arg0;
    }
    
    
    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        return new ViewConflictingPreset(this.context, this.contextAnnotation, this.presets.get(arg0));
    }
}
