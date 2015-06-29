package com.playuav.android.widgets.checklist;

import android.content.Context;

import com.o3dr.android.client.Drone;
import com.o3dr.services.android.lib.drone.property.Battery;
import com.o3dr.services.android.lib.drone.property.Gps;
import com.o3dr.services.android.lib.drone.property.State;

import com.playuav.android.DroidPlannerApp;

public class CheckListSysLink {
    private Context context;
	private Drone drone;

	public CheckListSysLink(Context context, Drone drone) {
        this.context = context;
		this.drone = drone;
	}

	public void getSystemData(CheckListItem mListItem, String mSysTag) {
		if (mSysTag == null)
			return;

		Battery batt = drone.getBattery();
		if (batt != null) {
			if (mSysTag.equalsIgnoreCase("SYS_BATTREM_LVL")) {
				mListItem.setSys_value(batt.getBatteryRemain());
			} else if (mSysTag.equalsIgnoreCase("SYS_BATTVOL_LVL")) {
				mListItem.setSys_value(batt.getBatteryVoltage());
			} else if (mSysTag.equalsIgnoreCase("SYS_BATTCUR_LVL")) {
				mListItem.setSys_value(batt.getBatteryCurrent());
			}
		}

		Gps gps = drone.getGps();
		if (gps != null) {
			if (mSysTag.equalsIgnoreCase("SYS_GPS3D_LVL")) {
				mListItem.setSys_value(gps.getSatellitesCount());
			}
		}

		State state = drone.getState();
		if (state != null) {
			if (mSysTag.equalsIgnoreCase("SYS_ARM_STATE")) {
				mListItem.setSys_activated(state.isArmed());
			} else if (mSysTag.equalsIgnoreCase("SYS_FAILSAFE_STATE")) {
				mListItem.setSys_activated(state.isWarning());
			}
		}

		if (mSysTag.equalsIgnoreCase("SYS_CONNECTION_STATE")) {
			mListItem.setSys_activated(drone.isConnected());
		}
	}

	public void setSystemData(CheckListItem checkListItem) {

		if (checkListItem.getSys_tag() == null)
			return;

		if (checkListItem.getSys_tag().equalsIgnoreCase("SYS_CONNECTION_STATE")) {
			doSysConnect(checkListItem);

		} else if (checkListItem.getSys_tag().equalsIgnoreCase("SYS_ARM_STATE")) {
			doSysArm(checkListItem);

		}
	}

	private void doSysArm(CheckListItem checkListItem) {
		if (drone.isConnected()) {
			if (checkListItem.isSys_activated() && !drone.getState().isArmed()) {
				drone.arm(true);
			} else {
				drone.arm(false);
			}
		}
	}

	private void doSysConnect(CheckListItem checkListItem) {
		boolean activated = checkListItem.isSys_activated();
		boolean connected = drone.isConnected();
		if (activated != connected) {
			if (connected)
				DroidPlannerApp.disconnectFromDrone(context);
			else
				DroidPlannerApp.connectToDrone(context);
		}
	}

}
