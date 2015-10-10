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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.privacysetting.DialogPrivacySettingEdit;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.gui.util.view.AlwaysClickableButton;
import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.context.IContextView;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;

/**
 * The {@link DialogContextChange} provides an interface for changing a {@link IContextAnnotation}.
 * 
 * @author Jakob Jarosch
 */
public class DialogContextChange extends Dialog {
    
    /**
     * Interface which is called on successful change.
     * 
     * @author Jakob Jarosch
     */
    public interface ICallback {
        
        /**
         * Method is invoked on successful change.
         */
        public void callback();
    }
    
    /**
     * The {@link IPreset} which contains the {@link IPrivacySetting}.
     */
    private IPreset preset;
    
    /**
     * The {@link IPrivacySetting} which contains the {@link IContextAnnotation}.
     */
    private IPrivacySetting privacySetting;
    
    /**
     * The {@link IContextAnnotation} which is represented with this dialog.
     * When the variable is null a new {@link IContextAnnotation} is being created.
     */
    private IContextAnnotation contextAnnotation;
    
    /**
     * The context condition value which should be applied.
     */
    private String contextCondition;
    
    /**
     * The privacy setting value which overrides the original one when the context is active.
     */
    private String overrideValue;
    
    /**
     * The context which is used for the {@link IContextAnnotation}.
     */
    private IContext usedContext;
    
    /**
     * The view which provides an UI for changing the configuration.
     */
    private IContextView usedView;
    
    /**
     * The {@link ICallback} which is invoked on dismiss.
     */
    private ICallback callback;
    
    
    /**
     * Creates a new {@link DialogContextChange}.
     * 
     * @param context
     *            {@link Context} which is required for dialog creation.
     * @param preset
     *            {@link IPreset} which contains the {@link IPrivacySetting}.
     * @param privacySetting
     *            {@link IPrivacySetting} which contains the {@link IContextAnnotation}.
     * @param contextAnnotation
     *            {@link IContextAnnotation} which should be represented by this dialog. Can be null, then a new
     *            {@link IContextAnnotation} is being created.
     * @param callback
     *            {@link ICallback} which is invoked on dialog dismiss. Can be null if not required.
     */
    public DialogContextChange(Context context, IPreset preset, IPrivacySetting privacySetting,
            IContextAnnotation contextAnnotation, ICallback callback) {
        super(context);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_context_edit);
        
        this.preset = preset;
        this.privacySetting = privacySetting;
        this.contextAnnotation = contextAnnotation;
        this.callback = callback;
        
        /*
         * When context annotation is null create a context type selection dialog.
         */
        if (contextAnnotation == null) {
            showContextTypeSelectionDialog();
        } else {
            this.contextCondition = contextAnnotation.getContextCondition();
            this.overrideValue = contextAnnotation.getOverridePrivacySettingValue();
            this.usedContext = contextAnnotation.getContext();
            showDialog();
        }
        
        refresh();
        addListener();
    }
    
    
    /**
     * Show command does not do anything, dialog is being directly shown after creation.
     */
    @Override
    public void show() {
        /* Ignore the show command... */
    }
    
    
    /**
     * Method wraps the {@link Dialog#show()} functionality.
     */
    private void showDialog() {
        super.show();
    }
    
    
    /**
     * Creates a {@link Dialog} for selecting the type of the {@link IContextAnnotation}.
     */
    private void showContextTypeSelectionDialog() {
        /*
         * Load the name of all different context types.
         */
        final List<IContext> contexts = ModelProxy.get().getContexts();
        String[] contextStrings = new String[contexts.size()];
        
        for (int i = 0; i < contexts.size(); i++) {
            contextStrings[i] = contexts.get(i).getName();
        }
        
        /*
         * Create a new simple dialog for selecting the context type.
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.context_select_context_type));
        builder.setItems(contextStrings, new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DialogContextChange.this.usedContext = contexts.get(which);
                refresh();
                showDialog();
            }
        });
        /*
         * React on cancel the selection dialog; dismiss all dialogs.
         */
        builder.setOnCancelListener(new OnCancelListener() {
            
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                dismiss();
            }
        });
        builder.create().show();
    }
    
    
    /**
     * Updates all UI elements.
     */
    private void refresh() {
        /*
         * Only make context annotation deletable when context annotation is not newly created.
         */
        ((Button) findViewById(R.id.Button_Delete)).setEnabled((this.contextAnnotation != null));
        
        /*
         * Only display context when the type is already selected.
         */
        if (this.usedContext != null) {
            if (this.usedView == null) {
                this.usedView = this.usedContext.getView(getContext());
            }
            
            /*
             * Try to update the context view with the contextCondition.
             */
            try {
                if (this.contextCondition != null) {
                    this.usedView.setViewCondition(this.contextCondition);
                } else {
                    this.usedView.setViewCondition(this.usedView.getDefaultCondition());
                }
            } catch (InvalidConditionException e) {
                Log.e(this, "The condition which should be assigned seems to be an invalid one.", e);
            }
            
            /* Add the view */
            ((LinearLayout) findViewById(R.id.LinearLayout_Context)).removeAllViews();
            ((LinearLayout) findViewById(R.id.LinearLayout_Context)).addView(this.usedView.asView());
        }
    }
    
    
    /**
     * Add listener to all clickable UI elements.
     */
    private void addListener() {
        /*
         * React on a change privacy button click.
         */
        ((Button) findViewById(R.id.Button_ChangePS)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new DialogPrivacySettingEdit(getContext(), DialogContextChange.this.privacySetting,
                        DialogContextChange.this.overrideValue, new DialogPrivacySettingEdit.ICallback() {
                            
                            @Override
                            public void result(boolean changed, String newValue) {
                                if (changed) {
                                    DialogContextChange.this.overrideValue = newValue;
                                    DialogContextChange.this.contextCondition = DialogContextChange.this.usedView
                                            .getViewCondition();
                                    refresh();
                                }
                            }
                        }).show();
            }
        });
        
        /*
         * React on a save button click.
         */
        ((AlwaysClickableButton) findViewById(R.id.Button_Save)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                /*
                 * When the override value is null the context can't be created, so inform the user.
                 */
                if (DialogContextChange.this.overrideValue == null) {
                    Toast.makeText(getContext(),
                            "Please first configure the Privacy Setting over the 'Change value' button.",
                            Toast.LENGTH_LONG).show();
                } else {
                    /* Try to create the context annotation. */
                    DialogContextChange.this.contextCondition = DialogContextChange.this.usedView.getViewCondition();
                    
                    try {
                        if (DialogContextChange.this.contextAnnotation != null) {
                            DialogContextChange.this.contextAnnotation
                                    .setContextCondition(DialogContextChange.this.contextCondition);
                            DialogContextChange.this.contextAnnotation
                                    .setOverridePrivacySettingValue(DialogContextChange.this.overrideValue);
                        } else {
                            DialogContextChange.this.preset.assignContextAnnotation(
                                    DialogContextChange.this.privacySetting, DialogContextChange.this.usedContext,
                                    DialogContextChange.this.contextCondition, DialogContextChange.this.overrideValue);
                        }
                    } catch (InvalidConditionException e) {
                        Log.e(DialogContextChange.this, "Couldn't set new value for ContextAnnotaion, ICE", e);
                        GUITools.showToast(getContext(),
                                getContext().getString(R.string.failure_invalid_context_value), Toast.LENGTH_LONG);
                    } catch (PrivacySettingValueException e) {
                        Log.e(DialogContextChange.this, "Couldn't set new value for PrivacySetting, PSVE", e);
                        GUITools.showToast(getContext(), getContext().getString(R.string.failure_invalid_ps_value),
                                Toast.LENGTH_LONG);
                    }
                    
                    if (DialogContextChange.this.callback != null) {
                        DialogContextChange.this.callback.callback();
                    }
                    
                    dismiss();
                }
            }
        });
        
        /*
         * React on a delete button click.
         */
        ((Button) findViewById(R.id.Button_Delete)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DialogContextChange.this.preset.removeContextAnnotation(DialogContextChange.this.privacySetting,
                        DialogContextChange.this.contextAnnotation);
                dismiss();
            }
        });
        
        /*
         * React on a cancel button click.
         */
        ((Button) findViewById(R.id.Button_Cancel)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    
    
    @Override
    public void dismiss() {
        super.dismiss();
        
        /* Remove the context view to make it reusable. */
        ((LinearLayout) findViewById(R.id.LinearLayout_Context)).removeAllViews();
        
        /* Inform the callback. */
        if (this.callback != null) {
            this.callback.callback();
        }
    }
}
