package com.playuav.android.proxy.mission.item.fragments;

import com.playuav.android.R;
import com.playuav.android.widgets.spinnerWheel.CardWheelHorizontalView;
import com.playuav.android.widgets.spinnerWheel.adapters.NumericWheelAdapter;

import android.view.View;

import com.o3dr.services.android.lib.drone.mission.item.MissionItem;
import com.o3dr.services.android.lib.drone.mission.MissionItemType;
import com.o3dr.services.android.lib.drone.mission.item.command.ChangeSpeed;

public class MissionChangeSpeedFragment extends MissionDetailFragment implements
		CardWheelHorizontalView.OnCardWheelChangedListener {

	@Override
	protected int getResource() {
		return R.layout.fragment_editor_detail_change_speed;
	}

    @Override
    public void onApiConnected(){
        super.onApiConnected();

        final View view = getView();
        typeSpinner.setSelection(commandAdapter.getPosition(MissionItemType.CHANGE_SPEED));

        final NumericWheelAdapter adapter = new NumericWheelAdapter(getActivity()
                .getApplicationContext(), R.layout.wheel_text_centered, 1,
                20, "%d m/s");
        CardWheelHorizontalView cardAltitudePicker = (CardWheelHorizontalView) view.findViewById
                (R.id.picker1);
        cardAltitudePicker.setViewAdapter(adapter);
        cardAltitudePicker.addChangingListener(this);

        ChangeSpeed item = (ChangeSpeed) getMissionItems().get(0);
        cardAltitudePicker.setCurrentValue((int) item.getSpeed());
    }

	@Override
	public void onChanged(CardWheelHorizontalView wheel, int oldValue, int newValue) {
		switch (wheel.getId()) {
		case R.id.picker1:
            for(MissionItem missionItem : getMissionItems()) {
            	ChangeSpeed item = (ChangeSpeed) missionItem;
                item.setSpeed(newValue);
            }
			break;
		}
	}
}
