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
package de.unistuttgart.ipvs.pmp.gui.preset;

import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;

/**
 * Dialog for adding a Preset
 * 
 * @author Marcus Vetter
 */
public class DialogPresetEdit extends Dialog {
    
    /**
     * The ICallback is invoked on dialog dismiss.
     * 
     * @author Jakob Jarosch
     */
    public interface ICallback {
        
        /**
         * Method is invoked when a preset should be opened.
         * 
         * @param preset
         *            The {@link IPreset} which should be opened.
         */
        public void openPreset(IPreset preset);
        
        
        /**
         * Method is invoked when the UI has to be refreshed.
         */
        public void refresh();
    }
    
    /**
     * The TextView with the name
     */
    protected TextView name;
    
    /**
     * The TextView with the description
     */
    protected TextView desc;
    
    /**
     * The button to confirm the dialog
     */
    private Button confirm;
    
    /**
     * The button to cancel the dialog
     */
    private Button cancel;
    
    /**
     * The Callback instance
     */
    protected ICallback callback;
    
    /**
     * The Preset, if this dialog is used to modify the name and description of a Preset
     */
    protected IPreset preset;
    
    /**
     * The default name of the Preset
     */
    protected String defaultName = "";
    
    
    /**
     * * Necessary constructor
     * 
     * @param context
     *            the context
     * 
     * @param preset
     *            if it's null, an empty dialog will be created (add a new Preset); if it's not null, a dialog with
     *            prefilled text areas will be created (edit a Preset)
     */
    public DialogPresetEdit(Context context, IPreset preset, ICallback callback) {
        super(context);
        this.callback = callback;
        this.preset = preset;
    }
    
    
    /**
     * Called when the dialog is first created. Gets all elements of the gui
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.dialog_preset_add);
        
        if (this.preset == null) {
            ((LinearLayout) findViewById(R.id.Title_add_preset)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.Title_edit_preset)).setVisibility(View.GONE);
        } else {
            ((LinearLayout) findViewById(R.id.Title_add_preset)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.Title_edit_preset)).setVisibility(View.VISIBLE);
        }
        
        this.name = (TextView) findViewById(R.id.presets_dialog_name_textfield);
        this.desc = (TextView) findViewById(R.id.presets_dialog_description_textfield);
        this.confirm = (Button) findViewById(R.id.presets_dialog_confirm);
        this.cancel = (Button) findViewById(R.id.presets_dialog_cancel);
        
        // Fill text fields, if it's an edit of a Preset
        if (this.preset != null) {
            this.name.setText(this.preset.getName());
            this.desc.setText(this.preset.getDescription());
        } else {
            // Fill the name field with a default name, if you want to add a Preset
            int number = 0;
            LOOP: while (this.defaultName.equals("")) {
                number++;
                for (IPreset preset : ModelProxy.get().getPresets()) {
                    if (preset.getName().equals("Preset_" + number)) {
                        continue LOOP;
                    }
                }
                this.defaultName = "Preset_" + number;
            }
            this.name.setText(this.defaultName);
        }
        
        // Add listener and watcher
        this.name.setOnFocusChangeListener(new FocusListenerNameField());
        this.name.setOnClickListener(new ClickListenerNameField());
        this.name.addTextChangedListener(new TextWatcherNameField());
        this.confirm.setOnClickListener(new ConfirmListener());
        this.cancel.setOnClickListener(new CancelListener());
    }
    
    /**
     * Listener class needed for the name field
     */
    private class FocusListenerNameField implements android.view.View.OnFocusChangeListener {
        
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            TextView nameField = DialogPresetEdit.this.name;
            String nameText = String.valueOf(nameField.getText());
            String defaultName = DialogPresetEdit.this.defaultName;
            
            /* 
             * Clear the name text field, if the text field equals the default name and has the focus,
             *  else set the default name
             */
            if (hasFocus && nameText.equals(defaultName)) {
                nameField.setText("");
            } else if (!hasFocus && nameText.equals("")) {
                nameField.setText(defaultName);
            }
        }
    }
    
    /**
     * Listener class needed for the name field
     */
    private class ClickListenerNameField implements android.view.View.OnClickListener {
        
        @Override
        public void onClick(View v) {
            TextView nameField = DialogPresetEdit.this.name;
            String nameText = String.valueOf(nameField.getText());
            String defaultName = DialogPresetEdit.this.defaultName;
            
            // Clear the name text field, if the text is equal to the default name
            if (nameText.equals(defaultName)) {
                nameField.setText("");
            }
            
        }
        
    }
    
    /**
     * Text watcher class needed for the name field
     */
    private class TextWatcherNameField implements android.text.TextWatcher {
        
        @Override
        public void afterTextChanged(Editable s) {
            TextView nameField = DialogPresetEdit.this.name;
            String nameText = String.valueOf(nameField.getText());
            
            for (IPreset preset : ModelProxy.get().getPresets()) {
                if (preset.getName().toLowerCase(Locale.getDefault()).equals(nameText.toLowerCase(Locale.getDefault()))) {
                    nameField.setError("Another Preset is already called " + nameText);
                }
                
            }
        }
        
        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // no need
        }
        
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // no need
        }
        
    }
    
    /**
     * Listener class needed for the confirm button
     */
    private class ConfirmListener implements android.view.View.OnClickListener {
        
        @Override
        public void onClick(View v) {
            TextView nameField = DialogPresetEdit.this.name;
            
            String name = "null";
            if (DialogPresetEdit.this.name != null) {
                name = DialogPresetEdit.this.name.getText().toString();
            }
            String descr = "null";
            if (DialogPresetEdit.this.desc != null) {
                descr = DialogPresetEdit.this.desc.getText().toString();
            }
            
            if (name.length() == 0) {
                // no name set
                Toast.makeText(getContext(), R.string.please_enter_a_name, Toast.LENGTH_SHORT).show();
                return;
            } else if (nameField.getError() != null) {
                Toast.makeText(getContext(), R.string.please_choose_another_name, Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (DialogPresetEdit.this.preset == null) {
                // Add a new Preset
                IPreset createdPreset = ModelProxy.get().addUserPreset(name, descr);
                
                // Open Preset
                if (DialogPresetEdit.this.callback != null) {
                    DialogPresetEdit.this.callback.openPreset(createdPreset);
                }
            } else {
                // Edit the Preset
                DialogPresetEdit.this.preset.setName(name);
                DialogPresetEdit.this.preset.setDescription(descr);
                
                // Update the Presets
                if (DialogPresetEdit.this.callback != null) {
                    DialogPresetEdit.this.callback.refresh();
                }
            }
            
            // Dismiss
            dismiss();
            
        }
    }
    
    /**
     * Listener class needed for the cancel button
     */
    private class CancelListener implements android.view.View.OnClickListener {
        
        @Override
        public void onClick(View v) {
            // Dismiss
            dismiss();
        }
        
    }
    
}
