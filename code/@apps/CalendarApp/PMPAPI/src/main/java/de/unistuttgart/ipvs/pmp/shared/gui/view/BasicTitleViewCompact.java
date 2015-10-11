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
package de.unistuttgart.ipvs.pmp.shared.gui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import de.unistuttgart.ipvs.pmp.shared.R;
import de.unistuttgart.ipvs.pmp.shared.gui.AttributeSetUtil;

/**
 * Compact version of the Basic Title for layouts.
 * 
 * @author Jakob Jarosch
 */
public class BasicTitleViewCompact extends BasicTitleView {
    
    public BasicTitleViewCompact(Context context) {
        super(context);
    }
    
    
    public BasicTitleViewCompact(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        this.borderColor = new AttributeSetUtil(context, attrs).getColor(AttributeSetUtil.ViewBasicTitle_borderColor,
                Color.parseColor("#777777"));
    }
    
    
    @Override
    protected void createLayout() {
        /* load the xml-layout. */
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addView(layoutInflater.inflate(R.layout.view_basictitle_compact, null));
    }
}
