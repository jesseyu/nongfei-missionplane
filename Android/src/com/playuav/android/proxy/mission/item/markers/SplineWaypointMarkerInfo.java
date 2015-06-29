package com.playuav.android.proxy.mission.item.markers;

import com.playuav.android.R;
import com.playuav.android.proxy.mission.item.MissionItemProxy;

/**
 * This class implements the marker info for a spline waypoint mission item.
 * TODO: update this marker info's icons.
 */
public class SplineWaypointMarkerInfo extends MissionItemMarkerInfo {

	protected SplineWaypointMarkerInfo(MissionItemProxy origin) {
		super(origin);
	}

	@Override
	protected int getSelectedIconResource() {
		return R.drawable.ic_spline_wp_map_selected;
	}

	@Override
	protected int getIconResource() {
		return R.drawable.ic_spline_wp_map;
	}
}
