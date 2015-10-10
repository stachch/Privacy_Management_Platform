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

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.view.BasicTitleView;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetSet;

/**
 * Dialog for displaying or requesting the ID of a {@link IPresetSet}.
 * 
 * @author Jakob Jarosch
 */
public class DialogPresetsImportExportId extends Dialog {
    
    /**
     * {@link ICallback} which is invoked on {@link Dialog} completion (when a input was requested).
     * 
     * @author Jakob Jarosch
     */
    public interface ICallback {
        
        /**
         * Method is called on {@link Dialog} dismiss.
         * 
         * @param id
         *            The identifier which was typed in.
         */
        public void ended(String id);
    }
    
    /**
     * Callback which should be invoked on {@link Dialog} dismiss.
     */
    private ICallback callback = null;
    
    /**
     * The id which should be initially displayed in the {@link EditText} box.
     */
    private String id = null;
    
    
    /**
     * Creates a new {@link DialogPresetsImportExportId} for input.
     * 
     * @param context
     *            {@link Context} required for {@link Dialog} creation.
     * @param callback
     *            {@link ICallback} which is invoked on {@link Dialog} dismiss.
     * @param id
     *            Id which should be filled into the field. Can be null.
     */
    public DialogPresetsImportExportId(Context context, ICallback callback, String id) {
        super(context);
        
        this.callback = callback;
        this.id = id;
        
        initializeDialog();
    }
    
    
    /**
     * Creates a new {@link DialogPresetsImportExportId} for displaying an id.
     * 
     * @param context
     *            {@link Context} required for {@link Dialog} creation.
     * @param id
     *            Unique identifier which should be displayed.
     */
    public DialogPresetsImportExportId(Context context, String id) {
        super(context);
        
        this.id = id;
        
        initializeDialog();
    }
    
    
    /**
     * Initializes the {@link Dialog}.
     */
    private void initializeDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_presets_import_export_id);
        
        refresh();
        addListener();
    }
    
    
    /**
     * Updates the UI.
     */
    private void refresh() {
        EditText editText = (EditText) findViewById(R.id.EditText_ID);
        TextView descriptionTv = (TextView) findViewById(R.id.TextView_Description);
        TextView idTv = (TextView) findViewById(R.id.TextView_ID);
        BasicTitleView title = (BasicTitleView) findViewById(R.id.Title);
        Button confirmButton = (Button) findViewById(R.id.Button_NextStep);
        Button emailButton = (Button) findViewById(R.id.Button_Email);
        Button cancelButton = (Button) findViewById(R.id.Button_Cancel);
        Button closeButton = (Button) findViewById(R.id.Button_Close);
        
        if (this.callback == null) {
            /* Just show the id (there was an upload before). */
            editText.setVisibility(View.GONE);
            descriptionTv.setVisibility(View.VISIBLE);
            idTv.setVisibility(View.VISIBLE);
            idTv.setText(this.id);
            
            title.setTitle(getContext().getString(R.string.presets_export_title));
            
            emailButton.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            
        } else {
            /* There is a import in process, let the user enter a id. */
            descriptionTv.setVisibility(View.GONE);
            idTv.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            
            if (this.id != null) {
                editText.setText(this.id);
            }
            
            title.setTitle(getContext().getString(R.string.presets_import_title));
            
            emailButton.setVisibility(View.GONE);
            closeButton.setVisibility(View.GONE);
            confirmButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            
        }
    }
    
    
    /**
     * Adds listener to all clickable UI elements.
     */
    private void addListener() {
        /*
         * Confirm button on click listener.
         */
        ((Button) findViewById(R.id.Button_NextStep)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String id = ((EditText) findViewById(R.id.EditText_ID)).getText().toString();
                DialogPresetsImportExportId.this.callback.ended(id);
                dismiss();
            }
        });
        
        /*
         * Email button on click listener.
         */
        ((Button) findViewById(R.id.Button_Email)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                sendEmail();
                dismiss();
            }
        });
        
        /*
         * Cancel button on click listener.
         */
        ((Button) findViewById(R.id.Button_Cancel)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        /*
         * Close button on click listener.
         */
        ((Button) findViewById(R.id.Button_Close)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        /*
         * TextView listener reacts on a long touch and copies the id to the clipboard.
         */
        ((TextView) findViewById(R.id.TextView_ID)).setOnLongClickListener(new View.OnLongClickListener() {
            
            @SuppressWarnings("deprecation")
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(DialogPresetsImportExportId.this.id);
                
                Toast.makeText(getContext(), getContext().getString(R.string.presets_export_copy_success),
                        Toast.LENGTH_SHORT).show();
                        
                return true;
            }
        });
    }
    
    
    /**
     * Creates a send email {@link Intent} with a message including the preset set id.
     */
    private void sendEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        emailIntent.setType("plain/text");
        
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[0]);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                getContext().getString(R.string.presets_export_email_subject));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                getContext().getString(R.string.presets_export_email_body, this.id));
                
        Intent startIntent = Intent.createChooser(emailIntent, getContext().getText(R.string.choose_email_app));
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(startIntent);
    }
}
