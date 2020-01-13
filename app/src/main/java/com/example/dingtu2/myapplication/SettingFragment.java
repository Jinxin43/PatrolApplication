package com.example.dingtu2.myapplication;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;

import com.DingTu.Base.PubVar;
import com.example.dingtu2.myapplication.utils.UpdateManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment {


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_main);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
//        if ("select_linkage".equals(preference.getKey())) {
//            CheckBoxPreference checkBox = (CheckBoxPreference) findPreference("select_linkage");
//            ListPreference editBox = (ListPreference) findPreference("select_city");
//            editBox.setEnabled(checkBox.isChecked());
//        }
        if ("check_update".equals(preference.getKey())) {
            UpdateManager um = new UpdateManager(PubVar.m_DoEvent.m_Context);
            um.checkUpdate();
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(PubVar.m_DoEvent.m_Context);
        PubVar.AutoPan = prefs.getBoolean("gps_center", true);

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}
