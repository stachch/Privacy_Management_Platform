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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.unistuttgart.ipvs.pmp.model.Model;

/**
 * {@link BroadcastReceiver} to remove apps once they are uninstalled by Android.
 * 
 * @author Tobias Kuhn
 * 
 */
public class UninstallReceiver extends android.content.BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentPackage = intent.getData().getSchemeSpecificPart();
        
        if (Model.getInstance().getApp(intentPackage) != null) {
            Model.getInstance().unregisterApp(intentPackage);
        }
    }
    
}
