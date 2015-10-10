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
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.R;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewConfiguration;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.util.MapsAPIKeyAsset;

public class LocationContextMapView extends MapActivity {
    
    /*
     * A lot of stuff because Android fails at providing an interface to get some return values.
     */
    
    /**
     * Flag to indicate that GEO_POINTS has changed. May only be changed within synchronized(GEO_POINTS).
     */
    public static final AtomicBoolean DIRTY_FLAG = new AtomicBoolean(false);
    
    /**
     * The actual data. May only be changed within synchronized(GEO_POINTS).
     */
    public static final List<GeoPoint> GEO_POINTS = new ArrayList<GeoPoint>();
    public static boolean NEGATE = false;
    
    protected static final int CONTEXT_MENU_REMOVE_BUTTON_ID = 1;
    protected static final int CONTEXT_MENU_MOVE_BUTTON_ID = 2;
    protected static final int CONTEXT_MENU_ADD_BUTTON_ID = 3;
    protected static final int CONTEXT_MENU_CLEAR_BUTTON_ID = 4;
    protected static final int CONTEXT_MENU_NEGATE_BUTTON_ID = 5;
    
    public static final float MAX_MOVE_SIZE_FOR_LONG_TAP = 20f;
    
    class PointOverlays extends ItemizedOverlay<OverlayItem> {
        
        /**
         * The point that's currently selected.
         */
        private int selected = -1;
        
        /**
         * The point that is currently moved.
         */
        protected int movingPoint = -1;
        
        
        public PointOverlays(Drawable defaultMarker) {
            super(boundCenterBottom(defaultMarker));
        }
        
        
        public void updateOverlays() {
            populate();
            LocationContextMapView.this.map.invalidate();
        }
        
        
        @Override
        protected OverlayItem createItem(int i) {
            return new OverlayItem(GEO_POINTS.get(i), "", "");
        }
        
        
        @Override
        public int size() {
            return GEO_POINTS.size();
        }
        
        
        @Override
        public GeoPoint getCenter() {
            long sumLat = 0, sumLon = 0;
            
            for (int i = 0; i < size(); i++) {
                sumLat += GEO_POINTS.get(i).getLatitudeE6();
                sumLon += GEO_POINTS.get(i).getLongitudeE6();
            }
            
            return new GeoPoint((int) (sumLat / size()), (int) (sumLon / size()));
        }
        
        
        @Override
        protected boolean onTap(int index) {
            boolean result = super.onTap(index);
            
            this.selected = index;
            openContextMenu(LocationContextMapView.this.map);
            
            return result;
        }
        
        
        public int getSelected() {
            return this.selected;
        }
        
        
        public void resetSelection() {
            this.selected = -1;
            
        }
        
        
        @Override
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
            
            // prepare paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStrokeCap(Cap.ROUND);
            paint.setStrokeJoin(Join.ROUND);
            
            // prepare path
            Path path = new Path();
            
            Point p0 = new Point(), p = new Point();
            mapView.getProjection().toPixels(GEO_POINTS.get(0), p0);
            path.moveTo(p0.x, p0.y);
            
            for (int i = 1; i < GEO_POINTS.size(); i++) {
                mapView.getProjection().toPixels(GEO_POINTS.get(i), p);
                path.lineTo(p.x, p.y);
            }
            path.lineTo(p0.x, p0.y);
            
            // fill
            paint.setColor(0xaafff06d);
            paint.setStyle(Style.FILL);
            
            if (NEGATE) {
                canvas.save();
                canvas.clipPath(path, Op.DIFFERENCE);
                
                canvas.drawRect(mapView.getLeft(), mapView.getTop(), mapView.getRight(), mapView.getBottom(), paint);
                canvas.restore();
            } else {
                canvas.drawPath(path, paint);
            }
            
            // lines
            paint.setColor(0x80ffd83c);
            paint.setStrokeWidth(5f);
            paint.setStyle(Style.STROKE);
            
            canvas.drawPath(path, paint);
            
            return super.draw(canvas, mapView, shadow, when);
        }
        
        
        public void startMovingSelected() {
            this.movingPoint = this.selected;
        }
        
        
        public int getAndStopMoving() {
            int result = this.movingPoint;
            this.movingPoint = -1;
            return result;
        }
    }
    
    /**
     * Mighty Google {@link MapView}.
     */
    protected MapView map;
    private MapController mapController;
    
    /**
     * The overlay of the individual points.
     */
    protected PointOverlays points;
    
    /**
     * The point where the context menu was opened, if this.points.getSelected() < 0.
     */
    protected GeoPoint contextPoint;
    
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle icicle) {
        
        super.onCreate(icicle);
        
        String mapKey = MapsAPIKeyAsset.getKey(this);
        if (mapKey == null) {
            // if not existing, I'm taking you down with me!
            throw new IllegalAccessError();
        }
        
        // prepare map, hacked so long click opens the context menu
        // (onLongClickHandler does not work with MapViews)
        this.map = new MapView(this, mapKey) {
            
            private long lastClickDown = 0L;
            private float lastClickX = 0f;
            private float lastClickY = 0f;
            private boolean moving = false;
            
            private final float MAX_MOVE_SIZE_FOR_LONG_TAP_SQR = MAX_MOVE_SIZE_FOR_LONG_TAP
                    * MAX_MOVE_SIZE_FOR_LONG_TAP;
                    
                    
            @Override
            public boolean onTouchEvent(MotionEvent ev) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        this.lastClickDown = ev.getEventTime();
                        this.lastClickX = ev.getX();
                        this.lastClickY = ev.getY();
                        this.moving = false;
                        break;
                        
                    case MotionEvent.ACTION_MOVE:
                        float distX = this.lastClickX - ev.getX();
                        float distY = this.lastClickY - ev.getY();
                        float distClickSqr = distX * distX + distY * distY;
                        
                        this.moving |= (distClickSqr > this.MAX_MOVE_SIZE_FOR_LONG_TAP_SQR);
                        break;
                        
                    case MotionEvent.ACTION_UP:
                        if (!this.moving
                                && this.lastClickDown + ViewConfiguration.getLongPressTimeout() < ev.getEventTime()) {
                            LocationContextMapView.this.contextPoint = getProjection().fromPixels((int) ev.getX(),
                                    (int) ev.getY());
                                    
                            // LongClick
                            
                            int movingPoint = LocationContextMapView.this.points.getAndStopMoving();
                            if (movingPoint >= 0) {
                                
                                // move the point
                                synchronized (GEO_POINTS) {
                                    DIRTY_FLAG.set(true);
                                    GEO_POINTS.set(movingPoint, LocationContextMapView.this.contextPoint);
                                    LocationContextMapView.this.points.updateOverlays();
                                }
                                
                            } else {
                                // show the menu
                                LocationContextMapView.this.openContextMenu(LocationContextMapView.this.map);
                            }
                            return true;
                        }
                        break;
                        
                }
                
                return super.onTouchEvent(ev);
            }
            
        };
        this.map.setClickable(true);
        this.map.setBuiltInZoomControls(true);
        
        this.mapController = this.map.getController();
        
        // prepare overlay
        this.points = new PointOverlays(getResources().getDrawable(R.drawable.star_on));
        
        // read in intent lat/lon
        synchronized (DIRTY_FLAG) {
            DIRTY_FLAG.set(false);
            double[] latitudes = getIntent().getDoubleArrayExtra(LocationContextView.LATITUDE_EXTRA);
            double[] longitudes = getIntent().getDoubleArrayExtra(LocationContextView.LONGITUDE_EXTRA);
            
            GEO_POINTS.clear();
            for (int i = 0; i < latitudes.length; i++) {
                GEO_POINTS.add(new GeoPoint((int) (1E6 * latitudes[i]), (int) (1E6 * longitudes[i])));
            }
            
            NEGATE = getIntent().getBooleanExtra(LocationContextView.NEGATE_EXTRA, false);
            
        }
        
        // add the overlays
        this.points.updateOverlays();
        this.map.getOverlays().add(this.points);
        
        // animate to where we want to be
        this.mapController.animateTo(this.points.getCenter());
        this.mapController.zoomToSpan(this.points.getLatSpanE6(), this.points.getLonSpanE6());
        
        setContentView(this.map);
        registerForContextMenu(this.map);
        
        addListeners();
    }
    
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    
    private void addListeners() {
        
        this.map.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(de.unistuttgart.ipvs.pmp.R.string.contexts_location_menu_header);
                
                // add
                menu.add(Menu.NONE, CONTEXT_MENU_ADD_BUTTON_ID, 1,
                        de.unistuttgart.ipvs.pmp.R.string.contexts_location_add)
                        .setEnabled(LocationContextMapView.this.points.getSelected() < 0);
                        
                // move
                menu.add(Menu.NONE, CONTEXT_MENU_MOVE_BUTTON_ID, 2,
                        de.unistuttgart.ipvs.pmp.R.string.contexts_location_move)
                        .setEnabled(LocationContextMapView.this.points.getSelected() >= 0);
                        
                // remove
                menu.add(Menu.NONE, CONTEXT_MENU_REMOVE_BUTTON_ID, 3,
                        de.unistuttgart.ipvs.pmp.R.string.contexts_location_remove)
                        .setEnabled((GEO_POINTS.size() > 1) && (LocationContextMapView.this.points.getSelected() >= 0));
                        
                // clear
                menu.add(Menu.NONE, CONTEXT_MENU_CLEAR_BUTTON_ID, 4,
                        de.unistuttgart.ipvs.pmp.R.string.contexts_location_clear);
                        
                // negate
                menu.add(Menu.NONE, CONTEXT_MENU_NEGATE_BUTTON_ID, 5,
                        de.unistuttgart.ipvs.pmp.R.string.contexts_location_negate);
            }
        });
        
    }
    
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (!item.isEnabled()) {
            return true;
        }
        
        switch (item.getItemId()) {
            // remove
            case CONTEXT_MENU_REMOVE_BUTTON_ID:
                synchronized (GEO_POINTS) {
                    if ((GEO_POINTS.size() > 1) && (this.points.getSelected() >= 0)) {
                        DIRTY_FLAG.set(true);
                        GEO_POINTS.remove(this.points.getSelected());
                        this.points.updateOverlays();
                    }
                }
                break;
                
            // add
            case CONTEXT_MENU_ADD_BUTTON_ID:
                synchronized (GEO_POINTS) {
                    DIRTY_FLAG.set(true);
                    GEO_POINTS.add(this.contextPoint);
                    this.points.updateOverlays();
                }
                break;
                
            // move
            case CONTEXT_MENU_MOVE_BUTTON_ID:
                Toast.makeText(this, getText(de.unistuttgart.ipvs.pmp.R.string.contexts_location_moving),
                        Toast.LENGTH_LONG).show();
                this.points.startMovingSelected();
                break;
                
            // clear
            case CONTEXT_MENU_CLEAR_BUTTON_ID:
                synchronized (GEO_POINTS) {
                    // 1 point has to remain...
                    if (GEO_POINTS.size() > 1) {
                        DIRTY_FLAG.set(true);
                        while (GEO_POINTS.size() > 1) {
                            GEO_POINTS.remove(GEO_POINTS.size() - 1);
                        }
                        this.points.updateOverlays();
                    }
                }
                break;
                
            // negate
            case CONTEXT_MENU_NEGATE_BUTTON_ID:
                synchronized (GEO_POINTS) {
                    DIRTY_FLAG.set(true);
                    NEGATE = !NEGATE;
                }
                this.map.invalidate();
                break;
                
        }
        this.points.resetSelection();
        return super.onContextItemSelected(item);
    }
    
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // fake ContextMenu on menu button 
        this.contextPoint = this.map.getMapCenter();
        openContextMenu(this.map);
        return super.onPrepareOptionsMenu(menu);
    }
    
}
