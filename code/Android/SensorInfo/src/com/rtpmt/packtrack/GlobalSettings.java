package com.rtpmt.packtrack;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.telephony.TelephonyManager;
import android.view.MenuItem;

import com.example.sensorinfo.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class GlobalSettings extends PreferenceActivity {
	//final SensorCart listOfSensors = (SensorCart) getApplicationContext();
	public static String[] sensor_list_arr;
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//mac_id = getTruckIdAsMacAddress();
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}
		

		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences.
		/*PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_general);
		getPreferenceScreen().addPreference(fakeHeader);*/
		addPreferencesFromResource(R.xml.pref_general);

		sensor_list_arr = SensorCart.sensorIdList.toArray(new String[SensorCart.sensorIdList.size()]);
		
		ListPreference sensorList = (ListPreference) findPreference("sensor_list");
		sensorList.setEntries(sensor_list_arr);
		sensorList.setEntryValues(sensor_list_arr);
		//sensorList.setValue(sensor_list_arr[0]);
		
		// Add 'notifications' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_Temperature);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_temperature);
		
		// Add 'data and sync' preferences, and a corresponding header.
		fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_Humidity);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_humidity);

		fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_Vibration);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_vibration);
		
		fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_Shock);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_shock);
		
		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		//bindPreferenceSummaryToValue(findPreference("example_text"));
		bindPreferenceSummaryToValue(findPreference("set_truck_id"));
		bindPreferenceSummaryToValue(findPreference("sensor_list"));
		bindPreferenceSummaryToValue(findPreference("set_temperature_threshold"));
		bindPreferenceSummaryToValue(findPreference("before_threshold_temperature"));
		bindPreferenceSummaryToValue(findPreference("after_threshold_temperature"));
		bindPreferenceSummaryToValue(findPreference("set_humidity_threshold"));
		bindPreferenceSummaryToValue(findPreference("before_threshold_humidity"));
		bindPreferenceSummaryToValue(findPreference("after_threshold_humidity"));
		bindPreferenceSummaryToValue(findPreference("set_vibration_threshold"));
		bindPreferenceSummaryToValue(findPreference("before_threshold_vibration"));
		bindPreferenceSummaryToValue(findPreference("after_threshold_vibration"));
		bindPreferenceSummaryToValue(findPreference("set_shock_threshold"));
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);

			}
			else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
						""));
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);
			
			bindPreferenceSummaryToValue(findPreference("set_truck_id"));
			bindPreferenceSummaryToValue(findPreference("sensor_list"));
		}
	}

	/**
	 * This fragment shows notification preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class TemperaturePreferenceFragment extends
			PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_temperature);

			bindPreferenceSummaryToValue(findPreference("set_temperature_threshold"));
			bindPreferenceSummaryToValue(findPreference("before_threshold_temperature"));
			bindPreferenceSummaryToValue(findPreference("after_threshold_temperature"));
		}
	}

	
	
	/**
	 * This fragment shows data and sync preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class HumidityPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_humidity);

			bindPreferenceSummaryToValue(findPreference("set_humidity_threshold"));
			bindPreferenceSummaryToValue(findPreference("before_threshold_humidity"));
			bindPreferenceSummaryToValue(findPreference("after_threshold_humidity"));
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class VibrationPreferenceFragment extends
			PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_vibration);

			bindPreferenceSummaryToValue(findPreference("set_vibration_threshold"));
			bindPreferenceSummaryToValue(findPreference("before_threshold_vibration"));
			bindPreferenceSummaryToValue(findPreference("after_threshold_vibration"));
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class ShockPreferenceFragment extends
			PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_shock);

			bindPreferenceSummaryToValue(findPreference("set_shock_threshold"));
		}
	}
	
	public String getTruckIdAsMacAddress(){
		
		TelephonyManager tManager = (TelephonyManager)getSystemService(android.content.Context.TELEPHONY_SERVICE);
		String IMEI = tManager.getDeviceId();
		if (IMEI == null || IMEI .length() == 0)
			IMEI = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
		
		return IMEI;
	} 
}
