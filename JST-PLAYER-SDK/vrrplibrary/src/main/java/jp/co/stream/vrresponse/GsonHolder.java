package jp.co.stream.vrresponse;


import com.google.gson.Gson;

import androidx.annotation.NonNull;

final class GsonHolder {

    private GsonHolder() {
    }

    @NonNull
    public static Gson getGson() {
        return Holder.GSON;
    }

    public static final class Holder {
        private static final Gson GSON = new Gson();
    }

}