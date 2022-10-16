package com.arcgismap;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnPanListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.Polygon;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ArcGISMapManager extends SimpleViewManager<MapView> {

    public static final String REACT_CLASS = "ArcGISMap";

    private static final String DEFAULT_LAYER = "https://services.gisqatar.org.qa/server/rest/services/Vector/Qatar_StreetMap_Hybrid_E/MapServer";

    private MapView mapView;
    private ArcGISMapModule arcGISMapModule;
    //PropertyChangeSupport extentChangeListener = new PropertyChangeSupport(this);

    public String getName() {
        return REACT_CLASS;
    }

    // The MapView implementation has a reference to an independent module that will be used
    //   in JavaScript to call functions on the MapView without needing to invalidate the full component
    // It doesn't seems that there is a convenient to share a state between JavaScript and native
    //   i.e.: JS can perform an action by changing the state of the ArcGISMap and the native
    //     component could update the state on JS side if user has interacted directly with the component
    //   it seems this would have been very convenient for some MapView attributes like center and scale
    //   Most of the API will probably be ok to implement using ArcGISMapModule but this seems
    //     like a big limitation for editing layers
    // https://facebook.github.io/react-native/docs/native-components-ios.html
    // https://facebook.github.io/react-native/docs/native-components-android.html
    // https://facebook.github.io/react-native/docs/native-modules-android.html
    // https://facebook.github.io/react-native/docs/communication-ios.html
    public ArcGISMapManager(ArcGISMapModule arcGISMapModule) {
        this.arcGISMapModule = arcGISMapModule;
    }

    @Override
    protected MapView createViewInstance(ThemedReactContext reactContext) {
        Log.v(REACT_CLASS, "createViewInstance");

        mapView = new MapView(reactContext);

        /*
        // TODO: attempt to listen to map change globally
        //  there is a mention of center property at https://developers.arcgis.com/android/api-reference/reference/com/esri/android/map/event/OnStatusChangedListener.html
        //  another way to go seems to listen for touch but that probably won't be fired when chaging center programmatically
        //  https://developers.arcgis.com/android/api-reference/reference/com/esri/android/map/MapOnTouchListener.html
        mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onStatusChanged(Object source, STATUS status) {
                switch (status) {
                    case INITIALIZED:
                        extentChangeListener.addPropertyChangeListener("center", new PropertyChangeListener() {
                            @Override
                            public void propertyChange(PropertyChangeEvent event) {
                                if (event.getOldValue() != event.getNewValue()) {
                                    Log.v(REACT_CLASS, "extent change");
                                    emitExtentChange();
                                }
                            }
                        });
                        break;
                }
            }
        });
        */

        mapView.setAllowRotationByPinch(true);
        mapView.enableWrapAround(true);

        // Give the module a reference to MapView
        arcGISMapModule.setMapView(mapView);

        return mapView;
    }

    @ReactProp(name = "layers")
    public void setLayers(MapView view, @Nullable ReadableArray layers) {
        Log.v(REACT_CLASS, "set layers");

        if (layers == null || layers.size() < 1) {
            Log.v(REACT_CLASS, "set layers: adding default layer");
            mapView.addLayer(new ArcGISTiledMapServiceLayer(DEFAULT_LAYER));
        } else {
            mapView.removeAll();
            for (int i = 0; i < layers.size(); i++) {
                ReadableMap layer = layers.getMap(i);
                String type = layer.getString("type");
                String url = layer.getString("url");

                if (!url.equals("")) {
                    if (type.equals("ArcGISTiledMapServiceLayer")) {
                        Log.v(REACT_CLASS, "set layers: adding ArcGISTiledMapServiceLayer:" + url);
                        mapView.addLayer(new ArcGISTiledMapServiceLayer(url));
                    } else if (type.equals("ArcGISFeatureLayer")) {
                        Log.v(REACT_CLASS, "set layers: adding ArcGISFeatureLayer:" + url);
                        mapView.addLayer(new ArcGISFeatureLayer(url, ArcGISFeatureLayer.MODE.SNAPSHOT));
                    } else {
                        Log.v(REACT_CLASS, "set layers: unrecognized layer: " + type);
                    }
                } else {
                    Log.v(REACT_CLASS, "set layers: invalid url:" + url);
                }
            }
        }
    }

    private void emitExtentChange() {
        Log.v(REACT_CLASS, "emit extent change");

        WritableMap event = Arguments.createMap();
        event.putDouble("x", mapView.getCenter().getX());
        event.putDouble("y", mapView.getCenter().getY());
        event.putDouble("scale", mapView.getScale());

        ReactContext reactContext = (ReactContext) mapView.getContext();
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("onExtentChange", event);
    }

    @ReactProp(name = "onExtentChange", defaultBoolean = true)
    public void onExtentChange(MapView view, Boolean value) {
        mapView.setOnZoomListener(new OnZoomListener() {
            @Override
            public void preAction(float pivotX, float pivotY, double factor) {
            }

            @Override
            public void postAction(float pivotX, float pivotY, double factor) {
                emitExtentChange();
            }
        });

        mapView.setOnPanListener(new OnPanListener() {
            @Override
            public void prePointerMove(float v, float v1, float v2, float v3) {
                emitExtentChange();
            }

            @Override
            public void postPointerMove(float v, float v1, float v2, float v3) {

            }

            @Override
            public void prePointerUp(float v, float v1, float v2, float v3) {

            }

            @Override
            public void postPointerUp(float v, float v1, float v2, float v3) {

            }
        });
    }
}
