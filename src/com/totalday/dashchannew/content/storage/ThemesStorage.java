package com.totalday.dashchannew.content.storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chan.util.StringUtils;

public class ThemesStorage extends StorageManager.JsonOrgStorage<List<JSONObject>> {
	private static final String KEY_DATA = "data";

	private static final ThemesStorage INSTANCE = new ThemesStorage();
	private final HashMap<String, JSONObject> themes = new HashMap<>();

	private ThemesStorage() {
		super("themes", 1000, 10000);
		startRead();
	}

	public static ThemesStorage getInstance() {
		return INSTANCE;
	}

	@Override
	public List<JSONObject> onClone() {
		return new ArrayList<>(themes.values());
	}

	@Override
	public void onDeserialize(JSONObject jsonObject) {
		JSONArray jsonArray = jsonObject.optJSONArray(KEY_DATA);
		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.optJSONObject(i);
				if (jsonObject != null) {
					String name = jsonObject.optString("name");
					if (!StringUtils.isEmpty(name)) {
						themes.put(name, jsonObject);
					}
				}
			}
		}
	}

	@Override
	public JSONObject onSerialize(List<JSONObject> themes) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (JSONObject jsonObject : themes) {
			jsonArray.put(jsonObject);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(KEY_DATA, jsonArray);
		return jsonObject;
	}

	public HashMap<String, JSONObject> getItems() {
		return themes;
	}
}
