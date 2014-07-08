package de.robv.android.xposed.mods.playstorefix;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        // Display the fragment as the main content.
        if (savedInstanceState == null)
			getFragmentManager().beginTransaction().replace(android.R.id.content,
	                new PrefsFragment()).commit();
	}

	public static class PrefsFragment extends PreferenceFragment
			implements SharedPreferences.OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// this is important because although the handler classes that read these settings
			// are in the same package, they are executed in the context of the hooked package
			getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
			addPreferencesFromResource(R.xml.preferences);
			
			SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();
			sharedPref.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(sharedPref, "density");
            onSharedPreferenceChanged(sharedPref, "screenLayout");
            onSharedPreferenceChanged(sharedPref, "calculateLayout");
        }

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (key.equals("density")) {
				EditTextPreference pref = (EditTextPreference) findPreference(key);
				String value = sharedPreferences.getString(key, "");
				if (value.isEmpty()) {
					value = "(unchanged)";
				} else if (!value.matches("\\d{2,3}")) {
					pref.setText("");
					Toast.makeText(getActivity(), "Invalid density value", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (isAdded())
					pref.setSummary(getString(R.string.pref_density_summary, value));
			}else if(key.equals("screenLayout")){
                ListPreference pref = (ListPreference) findPreference(key);
                String value = sharedPreferences.getString(key, "");
                pref.setSummary(getString(R.string.pref_layout_summary,
                        screenLayoutToString(tryParseInt(value))));
            }
		}
	}

    private static String screenLayoutToString(int layout){
        switch (layout) {
            case 1:
                return "SMALL";
            case 2:
                return "NORMAL";
            case 3:
                return "LARGE";
            case 4:
                return "XLARGE";
            default:
                return "UNDEFINED";
        }
    }

    private static int tryParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }
}
