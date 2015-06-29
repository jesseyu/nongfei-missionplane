package com.playuav.android.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.playuav.android.maps.providers.DPMapProvider;
import com.playuav.android.utils.prefs.DroidPlannerPrefs;

import java.util.Locale;

/**
 * Contains application related functions.
 */
public class Utils {

	/**
	 * Returns the map provider selected by the user.
	 * 
	 * @param context
	 *            application context
	 * @return selected map provider
	 */
	public static DPMapProvider getMapProvider(Context context) {
		DroidPlannerPrefs prefs = new DroidPlannerPrefs(context);
		final String mapProviderName = prefs.getMapProviderName();

		return mapProviderName == null ? DPMapProvider.DEFAULT_MAP_PROVIDER : DPMapProvider
				.getMapProvider(mapProviderName);
	}

	/**
	 * Used to update the user interface language.
	 * 
	 * @param context
	 *            Application context
	 */
	public static void updateUILanguage(Context context) {
		DroidPlannerPrefs prefs = new DroidPlannerPrefs(context);
		if (prefs.isEnglishDefaultLanguage()) {
			Configuration config = new Configuration();
			config.locale = Locale.ENGLISH;

			final Resources res = context.getResources();
			res.updateConfiguration(config, res.getDisplayMetrics());
		}
	}
}
