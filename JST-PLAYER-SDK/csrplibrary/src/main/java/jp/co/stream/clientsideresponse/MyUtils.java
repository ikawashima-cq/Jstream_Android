package jp.co.stream.clientsideresponse;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.Display;
import android.view.Window;

import com.google.common.base.Strings;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

class MyUtils {

	public static String InputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        } 
        br.close();
        return sb.toString();
    }
	
	public static String getUUID(Activity activity, boolean save) {
		String uuid;
		if (save) {

			importAIRUUID(activity);

			SharedPreferences preferences = activity.getSharedPreferences("jstream_player", Activity.MODE_PRIVATE);
			uuid = preferences.getString("uuid", null);
			if (uuid == null) {
				uuid = UUID.randomUUID().toString();
				Editor editor = preferences.edit();
				editor.putString("uuid", uuid);
				editor.commit();
			}
		} else {
			uuid = UUID.randomUUID().toString();
		}
		return uuid;
	}

	private static void importAIRUUID(Activity activity) {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String airUUID = preferences.getString("uuid", null);
		if (airUUID != null) {
			try {
				JSONObject uuidJson = new JSONObject(airUUID);
				String uuid = uuidJson.getString("uuid");
                if (!Strings.isNullOrEmpty(uuid)) {
					uuid = UUID.randomUUID().toString();
					Editor editor = preferences.edit();
					editor.putString("uuid", uuid);
					editor.commit();
				}
			} catch (Exception e) {
			}
			preferences.edit().remove("uuid").apply();
		}
	}

	public static String urlEncode(String value) {
    	String encodedResult = "";
    	try {
    		if(value != null)
			{
				encodedResult = URLEncoder.encode(value, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
		}
    	return encodedResult;
    }

	public static String urlDecode(String value) {
		String decodedResult = "";
		try {
			if(value != null)
			{
				decodedResult = URLDecoder.decode(value, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
		}
		return decodedResult;
	}


	public static boolean isNumber(String value) {
    	try {
    		Integer.parseInt(value);
    		return true;
    	} catch (NumberFormatException e) {
    		return false;
    	}
    }
	
    public static String getVersionCodeString(Context context) {
        int versionCode = getVersionCode(context);
   		int segment1 = versionCode / 1000000;
   		int segment2 = versionCode % 1000000 / 1000;
   		int segment3 = versionCode % 1000;
   		return new StringBuilder().append(segment1).append(".").append(segment2).append(".").append(segment3).toString();
    }
	
    public static int getVersionCode(Context context) {
        PackageManager pm =  context.getPackageManager();
        int versionCode = 0;
        try {
            PackageInfo packageInfo = pm.getPackageInfo( context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch(Exception e) {
        }
        return versionCode;
    }

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        String versionName = "";
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch(Exception e) {
        }
        return versionName;
    }

	public static boolean canUseExoPlayer() {
		return Build.VERSION.SDK_INT >= 16;
	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}

	public static Point getScreenSize(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		if (Build.VERSION.SDK_INT >= 19) {
		 	//ナビゲーションバーを表示しない場合
			display.getRealSize(size);
		} else {
			display.getSize(size);
		}
		return size;
	}

	public static <T> List<T> trimListSize(List<T> list, int size, T defaultValue) {
		List newList = new ArrayList<>();
		for (int n = 0; n < size; n ++) {
			if (n < list.size()) {
				newList.add(list.get(n));
			} else {
				newList.add(defaultValue);
			}
		}
		return newList;
	}

	public static int convertPxToDp(Context context, int px){
		float scale = context.getResources().getDisplayMetrics().density;
		return (int)((px / scale) + 0.5f);
	}

	public static int convertDpToPx(Context context, int dp){
		float scale = context.getResources().getDisplayMetrics().density;
		return (int)((dp * scale) + 0.5f);
	}

	public static int getStatusBarHeight(Activity activity){
		final Rect rect = new Rect();
		Window window = activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rect);
		return rect.top;
	}

	public static String encryptAES128ECB(String plainText, String keyString) {
		String encodedString = "";
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
			SecretKeySpec secretKeySpec = new SecretKeySpec(keyString.getBytes(), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			byte[] encode = cipher.doFinal(plainText.getBytes());
			encodedString = Base64.encodeToString(encode, Base64.NO_WRAP);
		} catch (Exception e) {
		}
		return encodedString;
	}

	public static String dencryptAES128ECB(String encriptedText, String keyString) {
		String dencodedString = "";
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
			SecretKeySpec secretKeySpec = new SecretKeySpec(keyString.getBytes(), "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] data = Base64.decode(encriptedText, Base64.NO_WRAP);
			byte[] decode = cipher.doFinal(data);
			dencodedString = new String(decode);
		} catch (Exception e) {
		}
		return dencodedString;
	}

	public static String getCurrentDateTime(String format){
		DateFormat dataFormat = new SimpleDateFormat(format);
		Date date = new Date(System.currentTimeMillis());
		return dataFormat.format(date);
	}

	public static int getEpochTime(String strDate, String format){
		int unixTime = 0;
		try {
			SimpleDateFormat simpleDateformat = new SimpleDateFormat();
			simpleDateformat.applyPattern(format);
			Date date = simpleDateformat.parse(strDate);
			unixTime = (int)(date.getTime() / 1000);
		} catch(Exception e) {}
		return unixTime;
	}
	public static String getSuffix(String fileName) {
		if (fileName == null) return null;
		int point = fileName.lastIndexOf(".");
		if (point != -1) {
			return fileName.substring(point + 1);
		}
		return "";
	}

	public static String joinList(List<String> stringList, String delimiter) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < stringList.size(); i++) {
			result.append(stringList.get(i));
			if (i < stringList.size() -1) {
				result.append(delimiter);
			}
		}
		return result.toString();
	}

	public static Map<String, String> MapSort(Map<String, String> tmp)
	{
		Map<String, String> ret = tmp;

		try
		{
			// キーでソートする
			Object[] mapkey = tmp.keySet().toArray();
			java.util.Arrays.sort(mapkey);

			for (String nKey : tmp.keySet())
			{
				ret.put(nKey, tmp.get(nKey));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}

}
