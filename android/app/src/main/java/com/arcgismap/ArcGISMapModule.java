package com.arcgismap;

import android.util.Log;

import com.esri.android.map.MapView;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class ArcGISMapModule extends ReactContextBaseJavaModule {

    public static final String REACT_CLASS = "ArcGISMapModule";

    private MapView mapView;

    public ArcGISMapModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    @ReactMethod
    public void setLevel(float level) {
        Log.v(REACT_CLASS, "set level: " + level);
        // TODO: centerAndZoom is acting as if level was a factor even when the basemap is a TiledLayer
        // see https://developers.arcgis.com/android/api-reference/reference/com/esri/android/map/MapView.html#centerAndZoom(double,%20double,%20float)
        if (mapView != null && mapView.getCenter() != null) {
            mapView.centerAndZoom(
                    mapView.getCenter().getX(),
                    mapView.getCenter().getY(),
                    level
            );
        }
    }

    @ReactMethod
    public void setCenterWGS84(float x, float y) {
        Log.v(REACT_CLASS, "set center: " + x + ", " + y);

        if (mapView != null) {
            Point pointWgs = new Point(x, y);
            Point pointMap = (Point) GeometryEngine.project(
                    pointWgs,
                    SpatialReference.create(4326),
                    mapView.getSpatialReference()
            );
            mapView.centerAt(pointMap, true);
        }
    }

}
