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
package de.unistuttgart.ipvs.pmp.model.context.location;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

import android.R.attr;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.model.context.IContextView;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;
import de.unistuttgart.ipvs.pmp.util.location.PMPGeoPoint;

/**
 * View component for the {@link LocationContext}.
 * 
 * @author Tobias Kuhn
 *         
 */
public class LocationContextView extends LinearLayout implements IContextView {
    
    public static final String LATITUDE_EXTRA = "lat";
    public static final String LONGITUDE_EXTRA = "lon";
    public static final String NEGATE_EXTRA = "neg";
    
    class GeoPointList extends BaseAdapter {
        
        private final List<PMPGeoPoint> data;
        
        
        public GeoPointList() {
            this.data = new ArrayList<PMPGeoPoint>();
        }
        
        
        public void update(List<PMPGeoPoint> data) {
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }
        
        
        @Override
        public int getCount() {
            return this.data.size();
        }
        
        
        @Override
        public Object getItem(int position) {
            return this.data.get(position).toString();
        }
        
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        
        @SuppressWarnings("deprecation")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if ((convertView != null) && (convertView instanceof TextView)) {
                tv = (TextView) convertView;
            } else {
                tv = new TextView(parent.getContext());
            }
            tv.setTextAppearance(parent.getContext(), attr.textAppearance);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText(getItem(position).toString());
            
            return tv;
        }
        
        
        @Override
        public boolean hasStableIds() {
            return false;
        }
        
    }
    
    /**
     * Value currently in the view
     */
    protected LocationContextCondition value;
    
    /**
     * List of the coordinates
     */
    private ListView points;
    protected GeoPointList pointsList;
    private Button changeBtn;
    
    /**
     * Thread reading chances from the {@link LocationContextMapView}.
     */
    protected Thread readerThread = new Thread() {
        
        @Override
        public void run() {
            while (!isInterrupted()) {
                synchronized (LocationContextMapView.GEO_POINTS) {
                    if (LocationContextMapView.DIRTY_FLAG.get()) {
                        // load data back
                        LocationContextView.this.value.setNegated(LocationContextMapView.NEGATE);
                        LocationContextView.this.value.getPolygon().clear();
                        for (GeoPoint gp : LocationContextMapView.GEO_POINTS) {
                            LocationContextView.this.value.getPolygon()
                                    .add(new PMPGeoPoint(gp.getLatitudeE6() / 1E6, gp.getLongitudeE6() / 1E6));
                        }
                        // update view
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            
                            @Override
                            public void run() {
                                LocationContextView.this.pointsList.update(LocationContextView.this.value.getPolygon());
                            }
                        });
                        
                    }
                }
                
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                }
            }
        };
    };
    
    /**
     * Uncertainty
     */
    protected SeekBar uncertaintySeek;
    protected TextView uncertaintyText;
    
    /**
     * Hysteresis
     */
    protected SeekBar hysteresisSeek;
    protected TextView hysteresisText;
    
    
    public LocationContextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }
    
    
    public LocationContextView(Context context) {
        super(context);
        setup(context);
    }
    
    
    private void setup(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        
        this.value = getInitialCondition();
        
        inflate(context, R.layout.contexts_location_view, this);
        
        this.points = (ListView) findViewById(R.id.geoPointsList);
        this.pointsList = new GeoPointList();
        this.points.setAdapter(this.pointsList);
        this.pointsList.update(this.value.getPolygon());
        
        this.changeBtn = (Button) findViewById(R.id.changeCoordsBtn);
        
        this.uncertaintySeek = (SeekBar) findViewById(R.id.uncertaintySeekBar);
        this.uncertaintyText = (TextView) findViewById(R.id.uncertaintyTextView);
        
        this.hysteresisSeek = (SeekBar) findViewById(R.id.hysteresisSeekBar);
        this.hysteresisText = (TextView) findViewById(R.id.hysteresisTextView);
        
        addListeners();
        
        this.uncertaintySeek.setProgress(metersToSeekBarValue(1000.0));
        this.hysteresisSeek.setProgress(metersToSeekBarValue(100.0));
    }
    
    
    private void addListeners() {
        /*
         * start MapActivity
         */
        this.changeBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocationContextMapView.class);
                intent.putExtra(LATITUDE_EXTRA, LocationContextView.this.value.getPolygonLatitudeArray());
                intent.putExtra(LONGITUDE_EXTRA, LocationContextView.this.value.getPolygonLongitudeArray());
                intent.putExtra(NEGATE_EXTRA, LocationContextView.this.value.isNegated());
                GUITools.startIntent(intent);
                if (!LocationContextView.this.readerThread.isAlive()) {
                    LocationContextView.this.readerThread.start();
                }
            }
        });
        
        /*
         * hysteresis & uncertainty
         */
        OnSeekBarChangeListener seekBarListener = new OnSeekBarChangeListener() {
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            
            
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double meters = seekBarValueToMeters(progress);
                String display;
                if (meters < 1000) {
                    display = String.format("%.0f m", meters);
                } else if (meters < 10000) {
                    display = String.format("%.1f km", meters / 1000.0);
                } else {
                    display = String.format("%.0f km", meters / 1000.0);
                }
                
                if (seekBar.equals(LocationContextView.this.uncertaintySeek)) {
                    LocationContextView.this.uncertaintyText.setText(display);
                    LocationContextView.this.hysteresisSeek.setMax(progress);
                    
                } else if (seekBar.equals(LocationContextView.this.hysteresisSeek)) {
                    LocationContextView.this.hysteresisText.setText(display);
                    
                }
                
            }
        };
        this.uncertaintySeek.setOnSeekBarChangeListener(seekBarListener);
        this.hysteresisSeek.setOnSeekBarChangeListener(seekBarListener);
        seekBarListener.onProgressChanged(this.uncertaintySeek, metersToSeekBarValue(this.value.getUncertainty()),
                false);
        seekBarListener.onProgressChanged(this.hysteresisSeek, metersToSeekBarValue(this.value.getHysteresis()), false);
    }
    
    
    @Override
    public View asView() {
        return this;
    }
    
    
    @Override
    public String getViewCondition() {
        this.value.setUncertainty(seekBarValueToMeters(this.uncertaintySeek.getProgress()));
        this.value.setHysteresis(seekBarValueToMeters(this.hysteresisSeek.getProgress()));
        
        // polygon via MapView
        
        return this.value.toString();
    }
    
    
    @Override
    public void setViewCondition(String condition) throws InvalidConditionException {
        this.value = LocationContextCondition.parse(condition);
        
        this.uncertaintySeek.setProgress(metersToSeekBarValue(this.value.getUncertainty()));
        this.hysteresisSeek.setProgress(metersToSeekBarValue(this.value.getHysteresis()));
        
        this.pointsList.update(this.value.getPolygon());
        
        // polygon via MapView       
    }
    
    
    /**
     * Converts the {@link SeekBar} value in a reasonable meter amount.
     * 
     * @param progress
     * @return
     */
    protected double seekBarValueToMeters(int progress) {
        int pow = (progress / 90);
        double mantissa = 10.0 + (progress % 90);
        return mantissa * Math.pow(10.0, pow);
    }
    
    
    protected int metersToSeekBarValue(double meters) {
        int log = (int) (Math.floor(Math.log10(meters)) - 1);
        double mantissa = meters / Math.pow(10.0, log);
        int modulo = (int) Math.floor(mantissa - 10.0);
        return 90 * log + modulo;
    }
    
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        
        this.readerThread.interrupt();
    }
    
    
    private LocationContextCondition getInitialCondition() {
        List<PMPGeoPoint> initialList = new ArrayList<PMPGeoPoint>();
        initialList.add(new PMPGeoPoint(48.745161, 9.106774));
        
        return new LocationContextCondition(1000.0, 100.0, false, initialList);
    }
    
    
    @Override
    public String getDefaultCondition() {
        return getInitialCondition().toString();
    }
}
