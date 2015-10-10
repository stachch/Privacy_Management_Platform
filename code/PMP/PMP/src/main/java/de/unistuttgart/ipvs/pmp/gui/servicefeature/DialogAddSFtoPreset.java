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
package de.unistuttgart.ipvs.pmp.gui.servicefeature;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.preset.AdapterPresets;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;

public class DialogAddSFtoPreset extends Dialog {
    
    /**
     * 
     * @param context
     *            Context which is used to create the Dialog
     * @param dialog
     *            The dialog which invoked this.
     */
    public DialogAddSFtoPreset(Context context, final DialogServiceFeature dialog) {
        super(context);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.dialog_add_sf_to_preset);
        
        ListView lv = (ListView) findViewById(R.id.ListView_Presets);
        final List<IPreset> presets = ModelProxy.get().getPresets();
        
        if (presets.size() == 0) {
            ((TextView) findViewById(R.id.Presets_Text_View_No_Presets_Existing)).setVisibility(View.VISIBLE);
        } else {
            lv.setAdapter(new AdapterPresets(context, presets));
        }
        
        lv.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                /* Update the selected Preset */
                IPreset preset = presets.get(position);
                preset.getTransaction().start();
                IServiceFeature serviceFeature = dialog.getServiceFeature();
                preset.assignApp(serviceFeature.getApp());
                try {
                    preset.assignServiceFeature(serviceFeature);
                    preset.getTransaction().commit();
                } catch (PrivacySettingValueException e) {
                    preset.getTransaction().abort();
                    Log.e(DialogAddSFtoPreset.this, "Couldn't add Service Feature to Preset, PSVE", e);
                    GUITools.showToast(getContext(), getContext().getString(R.string.failure_invalid_ps_in_sf),
                            Toast.LENGTH_LONG);
                }
                
                /* Close the Dialog and close the underlying one as well */
                dismiss();
                dialog.refresh(); // Refresh before dismiss to update main list
                dialog.dismiss();
            }
        });
        
        addListener();
    }
    
    
    private void addListener() {
        ((Button) findViewById(R.id.Button_Cancel)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
