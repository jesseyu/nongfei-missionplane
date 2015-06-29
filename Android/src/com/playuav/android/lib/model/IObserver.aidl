// IObserver.aidl
package com.playuav.android.lib.model;

import com.o3dr.services.android.lib.drone.connection.ConnectionResult;

/**
* Asynchronous notification on change of vehicle state is available by registering observers for
* attribute changes.
*/
oneway interface IObserver {

    /**
    * Notify observer that the named attribute has changed.
    * @param attributeEvent event describing the update. The supported events are listed in {@link com.o3dr.services.android.lib.drone.attribute.AttributeEvent}
    * @param attributeBundle bundle object from which additional event data can be retrieved.
    */
    void onAttributeUpdated(String attributeEvent, in Bundle eventExtras);

    /**
    * TODO: left now for backward compatibility. To be removed in next version.
    * Called when the connection attempt fails.
    * @param result Describe why the connection failed.
    */
    void onConnectionFailed(in ConnectionResult result);
}
