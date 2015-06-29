package com.playuav.android.proxy.mission.item.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.o3dr.android.client.Drone;
import com.o3dr.services.android.lib.drone.mission.MissionItemType;
import com.o3dr.services.android.lib.drone.mission.item.complex.CameraDetail;
import com.o3dr.services.android.lib.drone.mission.item.complex.Survey;
import com.o3dr.services.android.lib.drone.mission.item.complex.SurveyDetail;
import com.o3dr.services.android.lib.drone.property.CameraProxy;

import com.playuav.android.R;
import com.playuav.android.R.id;
import com.playuav.android.proxy.mission.MissionProxy;
import com.playuav.android.proxy.mission.item.adapters.CamerasAdapter;
import com.playuav.android.utils.unit.UnitManager;
import com.playuav.android.utils.unit.UnitProvider;
import com.playuav.android.widgets.spinnerWheel.CardWheelHorizontalView;
import com.playuav.android.widgets.spinnerWheel.adapters.NumericWheelAdapter;
import com.playuav.android.widgets.spinners.SpinnerSelfSelect;

import java.util.Collections;
import java.util.List;

public class MissionSurveyFragment extends MissionDetailFragment implements
		CardWheelHorizontalView.OnCardWheelChangedListener,
		SpinnerSelfSelect.OnSpinnerItemSelectedListener{

	private static final String TAG = MissionSurveyFragment.class.getSimpleName();

    private static final IntentFilter eventFilter = new IntentFilter(MissionProxy
            .ACTION_MISSION_PROXY_UPDATE);

    private final BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(MissionProxy.ACTION_MISSION_PROXY_UPDATE.equals(action)){
                updateViews();
            }
        }
    };

	private CardWheelHorizontalView mOverlapPicker;
	private CardWheelHorizontalView mAnglePicker;
	private CardWheelHorizontalView mAltitudePicker;
	private CardWheelHorizontalView mSidelapPicker;

	public TextView distanceBetweenLinesTextView;
	public TextView areaTextView;
	public TextView distanceTextView;
	public TextView footprintTextView;
	public TextView groundResolutionTextView;
	public TextView numberOfPicturesView;
	public TextView numberOfStripsView;
	public TextView lengthView;
	private CamerasAdapter cameraAdapter;
    private SpinnerSelfSelect cameraSpinner;

	@Override
	protected int getResource() {
		return R.layout.fragment_editor_detail_survey;
	}

	@Override
	protected List<Survey> getMissionItems() {
		return (List<Survey>) super.getMissionItems();
	}

	@Override
	public void onApiConnected() {
		super.onApiConnected();

        final View view = getView();
        final Context context = getActivity().getApplicationContext();

        CameraProxy camera = getDrone().getCamera();
        List<CameraDetail> cameraDetails = camera == null
                ? Collections.<CameraDetail>emptyList()
                :  camera.getAvailableCameraInfos();
        cameraAdapter = new CamerasAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, cameraDetails);

        cameraSpinner = (SpinnerSelfSelect) view.findViewById(id.cameraFileSpinner);
        cameraSpinner.setAdapter(cameraAdapter);
		cameraSpinner.setOnSpinnerItemSelectedListener(this);

		mAnglePicker = (CardWheelHorizontalView) view.findViewById(id.anglePicker);
		mAnglePicker.setViewAdapter(new NumericWheelAdapter(context, R.layout.wheel_text_centered,
				0, 180, "%dº"));

        mOverlapPicker = (CardWheelHorizontalView) view.findViewById(id.overlapPicker);
        mOverlapPicker.setViewAdapter(new NumericWheelAdapter(context,
                R.layout.wheel_text_centered, 0, 99, "%d %%"));

        mSidelapPicker = (CardWheelHorizontalView) view.findViewById(R.id.sidelapPicker);
        mSidelapPicker.setViewAdapter(new NumericWheelAdapter(context,
                R.layout.wheel_text_centered, 0, 99, "%d %%"));

        mAltitudePicker = (CardWheelHorizontalView) view.findViewById(R.id.altitudePicker);
        mAltitudePicker.setViewAdapter(new NumericWheelAdapter(context,
                R.layout.wheel_text_centered, 0, 200, "%d m"));

		areaTextView = (TextView) view.findViewById(id.areaTextView);
		distanceBetweenLinesTextView = (TextView) view
				.findViewById(id.distanceBetweenLinesTextView);
		footprintTextView = (TextView) view.findViewById(id.footprintTextView);
		groundResolutionTextView = (TextView) view.findViewById(id.groundResolutionTextView);
		distanceTextView = (TextView) view.findViewById(id.distanceTextView);
		numberOfPicturesView = (TextView) view.findViewById(id.numberOfPicturesTextView);
		numberOfStripsView = (TextView) view.findViewById(id.numberOfStripsTextView);
		lengthView = (TextView) view.findViewById(id.lengthTextView);

        updateCamera();
		updateViews();
		
        mAnglePicker.addChangingListener(this);
        mOverlapPicker.addChangingListener(this);
        mSidelapPicker.addChangingListener(this);
        mAltitudePicker.addChangingListener(this);

        typeSpinner.setSelection(commandAdapter.getPosition(MissionItemType.SURVEY));

		getBroadcastManager().registerReceiver(eventReceiver, eventFilter);
	}

	@Override
	public void onApiDisconnected() {
		super.onApiDisconnected();
		getBroadcastManager().unregisterReceiver(eventReceiver);
	}

	@Override
	public void onSpinnerItemSelected(Spinner spinner, int position) {
		if (spinner.getId() == id.cameraFileSpinner) {
			CameraDetail cameraInfo = cameraAdapter.getItem(position);
			for (Survey survey : getMissionItems()) {
				survey.getSurveyDetail().setCameraDetail(cameraInfo);
			}

			onChanged(mAnglePicker, 0, 0);
		}
	}
	
	@Override
	public void onChanged(CardWheelHorizontalView cardWheel, int oldValue, int newValue) {
		switch (cardWheel.getId()) {
		case R.id.anglePicker:
		case R.id.altitudePicker:
		case R.id.overlapPicker:
		case R.id.sidelapPicker:
            Drone drone = getDrone();
			try {
				for (Survey survey : getMissionItems()) {
                    SurveyDetail surveyDetail = survey.getSurveyDetail();
                    surveyDetail.setAltitude(mAltitudePicker.getCurrentValue());
                    surveyDetail.setAngle(mAnglePicker.getCurrentValue());
                    surveyDetail.setOverlap(mOverlapPicker.getCurrentValue());
                    surveyDetail.setSidelap(mSidelapPicker.getCurrentValue());

                    drone.buildComplexMissionItem(survey);
                    checkIfValid(survey);
				}
			} catch (Exception e) {
				Log.e(TAG, "Error while building the survey.", e);
			}

            getMissionProxy().notifyMissionUpdate();
			break;
		}
	}

    private void checkIfValid(Survey survey) {
        if(mAltitudePicker == null)
            return;

        if(survey.isValid())
            mAltitudePicker.setBackgroundResource(R.drawable.bg_cell_white);
        else
            mAltitudePicker.setBackgroundColor(Color.RED);
    }

    private void updateViews() {
        if(isDetached())
            return;

		updateTextViews();
		updateSeekBars();
	}

    private void updateCamera() {
        List<Survey> surveyList = getMissionItems();
        if (!surveyList.isEmpty()) {
            Survey survey = surveyList.get(0);
            final int cameraSelection = cameraAdapter.getPosition(survey.getSurveyDetail()
                    .getCameraDetail());
            cameraSpinner.setSelection(Math.max(cameraSelection, 0));
        }
    }

    private void updateSeekBars() {
		List<Survey> surveyList = getMissionItems();
		if (!surveyList.isEmpty()) {
            Survey survey = surveyList.get(0);
			SurveyDetail surveyDetail = survey.getSurveyDetail();
            if(surveyDetail != null) {
                mAnglePicker.setCurrentValue((int) surveyDetail.getAngle());
                mOverlapPicker.setCurrentValue((int) surveyDetail.getOverlap());
                mSidelapPicker.setCurrentValue((int) surveyDetail.getSidelap());
                mAltitudePicker.setCurrentValue((int) surveyDetail.getAltitude());
            }

            checkIfValid(survey);
		}
	}
	
	private void updateTextViews() {
		boolean setDefault = true;
		List<Survey> surveyList = getMissionItems();
		if (!surveyList.isEmpty()) {
            Survey survey = surveyList.get(0);
			SurveyDetail surveyDetail = survey.getSurveyDetail();
			try {
                UnitProvider unitProvider = UnitManager.getUnitProvider();

				footprintTextView.setText(String.format("%s: %s x %s",
                        getString(R.string.footprint),
                        unitProvider.distanceToString(surveyDetail.getLateralFootPrint()),
                        unitProvider.distanceToString(surveyDetail
                                .getLongitudinalFootPrint())));

				groundResolutionTextView.setText(String.format("%s: %s /px",
                        getString(R.string.ground_resolution),
                        unitProvider.areaToString(surveyDetail.getGroundResolution())));

				distanceTextView.setText(String.format("%s: %s",
                        getString(R.string.distance_between_pictures),
                        unitProvider.distanceToString(surveyDetail
                                .getLongitudinalPictureDistance())));

				distanceBetweenLinesTextView.setText(String.format("%s: %s",
                        getString(R.string.distance_between_lines),
                        unitProvider.distanceToString(surveyDetail
                                .getLateralPictureDistance())));

				areaTextView.setText(String.format("%s: %s", getString(R.string.area),
                        unitProvider.areaToString(survey.getPolygonArea())));

				lengthView.setText(String.format("%s: %s", getString(R.string.mission_length),
                        unitProvider.distanceToString(survey.getGridLength())));

				numberOfPicturesView.setText(String.format("%s: %d", getString(R.string.pictures),
                        survey.getCameraCount()));

				numberOfStripsView.setText(String.format("%s: %d", getString(R.string.number_of_strips),
                        survey.getNumberOfLines()));

				setDefault = false;
			} catch (Exception e) {
				setDefault = true;
			}
		}

		if (setDefault) {
			footprintTextView.setText(getString(R.string.footprint) + ": ???");
			groundResolutionTextView.setText(getString(R.string.ground_resolution) + ": ???");
			distanceTextView.setText(getString(R.string.distance_between_pictures) + ": ???");
			distanceBetweenLinesTextView.setText(getString(R.string.distance_between_lines)
					+ ": ???");
			areaTextView.setText(getString(R.string.area) + ": ???");
			lengthView.setText(getString(R.string.mission_length) + ": ???");
			numberOfPicturesView.setText(getString(R.string.pictures) + "???");
			numberOfStripsView.setText(getString(R.string.number_of_strips) + "???");
		}
	}

}
