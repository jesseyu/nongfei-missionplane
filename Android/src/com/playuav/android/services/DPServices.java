package com.playuav.android.services;

import android.os.RemoteException;
import android.util.Log;

import com.playuav.android.BuildConfig;
import com.playuav.android.lib.model.IApiListener;
import com.playuav.android.lib.model.IDroidPlannerServices;
import com.playuav.android.lib.model.IDroneApi;

import java.lang.ref.WeakReference;

/**
 * Created by fhuya on 11/3/14.
 */
final class DPServices extends IDroidPlannerServices.Stub {

    private final static String TAG = DPServices.class.getSimpleName();

    private final WeakReference<DroidPlannerService> serviceRef;

    DPServices(DroidPlannerService service) {
        serviceRef = new WeakReference<DroidPlannerService>(service);
    }

    private DroidPlannerService getService() {
        final DroidPlannerService service = serviceRef.get();
        if (service == null)
            throw new IllegalStateException("Lost reference to parent service.");

        return service;
    }

    @Override
    public boolean ping() throws RemoteException {
        return true;
    }

    @Override
    public int getApiVersionCode() throws RemoteException {
        return BuildConfig.VERSION_CODE;
    }

    @Override
    public IDroneApi acquireDroneApi(String appId) throws RemoteException {
        return getService().acquireDroidPlannerApi(appId);
    }

    @Override
    public IDroneApi registerDroneApi(IApiListener listener, String appId) throws RemoteException {
        return getService().registerDroneApi(listener, appId);
    }

    @Override
    public void releaseDroneApi(IDroneApi dpApi) throws RemoteException {
        Log.d(TAG, "Releasing acquired drone api handle.");
        if(dpApi instanceof DroneApi) {
            getService().releaseDroneApi((DroneApi) dpApi);
        }
    }
}
