package com.arcgismap;

import android.util.Log;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArcGISMapPackage implements ReactPackage {

    private ArcGISMapModule arcGISMapModule;

    @Override
    public List<NativeModule> createNativeModules(
            ReactApplicationContext reactContext) {
        Log.v("ArcGISMapPackage", "createNativeModules");
        List<NativeModule> modules = new ArrayList<>();
        modules.add(arcGISMapModule);
        return modules;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(
            ReactApplicationContext reactContext) {
        Log.v("ArcGISMapPackage", "createViewManagers");

        // Create the module now as createViewManagers is called before createNativeModules
        arcGISMapModule = new ArcGISMapModule(reactContext);

        return Arrays.<ViewManager>asList(
                new ArcGISMapManager(arcGISMapModule)
        );
    }
}
