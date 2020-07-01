package jp.co.stream.jstresponse;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.databinding.ObservableBoolean;


/**
 * 端末に保存する設定情報
 */
class AppSetting {

    private static Integer readIntFromString(JSONObject object, String key) {
        try {
            String str = object.getString(key);
            return Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * BooleanかIntで保存されたフラグ値を読み込む
     *
     * @param object
     * @param key
     * @return 読み取ったフラグ
     */
    private static Boolean readBoolOrIntFlag(JSONObject object, String key) {
        try {
            Boolean value = object.getBoolean(key);
            return value;
        } catch (JSONException e) {
            try {
                Integer value = object.getInt(key);
                return (value == 1);
            } catch (JSONException exp) {
                exp.printStackTrace();
            }
        }
        return null;
    }

    private static Boolean readBoolean(JSONObject object, String key) {
        return readBoolean(object, key, false);
    }

    private static Integer readInt(JSONObject object, String key) {
        try {
            Integer value = object.getInt(key);
            return value;
        } catch (JSONException e) {
        }
        return null;
    }

    private static Boolean readBoolean(JSONObject object, String key, Boolean defaultValue) {
        try {
            Boolean value = object.getBoolean(key);
            return value;
        } catch (JSONException e) {
        }
        return defaultValue;
    }

    private static String readString(JSONObject object, String key) {
        try {
            String value = object.getString(key);
            return value;
        } catch (JSONException e) {
        }
        return null;
    }

    private static JSONObject readJSONObject(JSONObject object, String key) {
        try {
            JSONObject value = object.getJSONObject(key);
            return value;
        } catch (JSONException e) {
        }
        return null;
    }

    private static JSONArray readJSONOArray(JSONObject object, String key) {
        try {
            JSONArray value = object.getJSONArray(key);
            return value;
        } catch (JSONException e) {
        }
        return null;
    }

}
