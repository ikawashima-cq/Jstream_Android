package jp.co.stream.jstresponse;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class GsonUtils {

    public static <T> T objectFromJson(String json, Class<T> clazz) {
        return GsonHolder.getGson().fromJson(json, clazz);
    }

    public static <T> List<T> objectsFromJson(String json, Class<T> clazz) {
        Type type = $Gson$Types.newParameterizedTypeWithOwner(null, ArrayList.class, clazz);
        return GsonHolder.getGson().fromJson(json, type);
    }
}