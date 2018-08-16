package com.example.cmahajan.flickrgallery.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.cmahajan.flickrgallery.models.DataHolder;
import com.example.cmahajan.flickrgallery.models.FlickerItem;

import java.util.ArrayList;
import java.util.Map;

public class PersistanceManager {
    
    private SharedPreferences sharedPreferences;

    public PersistanceManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public DataHolder fetchItems() {
        DataHolder dataHolder = new DataHolder();
        dataHolder.setFlickerItems(new ArrayList<FlickerItem>());

        Map<String, String> map = (Map<String, String>) sharedPreferences.getAll();
        for (String key : map.keySet()) {
            dataHolder.getFlickerItems().add(new FlickerItem(key, map.get(key)));
        }

        return dataHolder;

    }

    public void storeItems(String id, String url) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(id, url);
        editor.apply();
    }
}
