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

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import de.unistuttgart.ipvs.pmp.gui.preset.ActivityPresets;

/**
 * Activity which handles a link click matched to the hostname defined in the AndroidManifest.xml.
 * 
 * @author Jakob Jarosch
 */
public class PresetSetImportActivity extends Activity {
    
    private static final int INTENT_RESULT_ID = 12345;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // TODO get the id from intent...
        Uri data = getIntent().getData();
        List<String> params = data.getPathSegments();
        String id = params.get(0); // "presetSetId"
        
        Intent intent = new Intent(this, ActivityPresets.class);
        intent.putExtra(GUIConstants.ACTIVITY_ACTION, GUIConstants.DOWNLOAD_PRESET_SET);
        intent.putExtra(GUIConstants.PRESET_SET_ID, id);
        startActivityForResult(intent, INTENT_RESULT_ID);
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == INTENT_RESULT_ID) {
            finish();
        }
    }
}
